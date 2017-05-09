package cn.hy.haikang.server.impl;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.NativeLong;

import cn.hy.haikang.config.HCNetSDK;
import cn.hy.haikang.config.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import cn.hy.haikang.config.HCNetSDK.NET_DVR_TIME;
import cn.hy.haikang.runnable.DownLoadWithSplitFileTask;
import cn.hy.haikang.runnable.DownLoadWithSplitTimeTask;
import cn.hy.haikang.type.DownLoadState;
import cn.hy.haikang.utils.HaiKangConvertUtils;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.schdule.DownLoadTranscoding;
import cn.hy.videorecorder.schdule.DownloadTaskAndTranscodingFileSchdule;
import cn.hy.videorecorder.schdule.DownloadTaskSplitFileTranscodingSchdule;
import cn.hy.videorecorder.server.StreamDownLoadServer;
import cn.hy.videorecorder.server.impl.TranscodingServerImpl;
import cn.hy.videorecorder.timer.DownLoadTaskAndSplitFileTranscoding;
import cn.hy.videorecorder.timer.DownloadTaskAndBathTranscoding;
/**
 * 海康默认支持依据时间下载(不使用时间分割器下载)
 * @author Administrator
 *
 */
public class HaiKangServerImpl implements StreamDownLoadServer{

	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 当前的点播参数
	 */
	private VodParam vodParam;

	public static NativeLong userId = new NativeLong(-1);
	
	private DownLoadTranscoding<DownloadTaskAndBathTranscoding> downLoadTranscoding;

	private TranscodingServerImpl transcodingServer;
	/**
	 * 为海康提供另一种时间分割下载的方法
	 */
	public final Runnable DOWNLOAD_WITHSPLITTIME_TASK;
	
	public final Runnable DOWNLOAD_WITHSPLITFILE_TASK;
	/**
	 * 
	 * @param vodParam 已经分隔好的
	 */
	public HaiKangServerImpl(VodParam vodParam,DownLoadTranscoding<DownloadTaskAndBathTranscoding> downLoadTranscoding,TranscodingServerImpl transcodingServer) {
		super();
		this.vodParam = vodParam;
		this.downLoadTranscoding = downLoadTranscoding;
		this.transcodingServer = transcodingServer;
		login();
		DOWNLOAD_WITHSPLITTIME_TASK = new DownLoadWithSplitTimeTask(vodParam,this);
		DOWNLOAD_WITHSPLITFILE_TASK = new DownLoadWithSplitFileTask(vodParam,this);
	}
	
	@Override
	public void downLoadByTimeZone(QueryTimeParam timeParm) throws Exception {
		NET_DVR_TIME struStartTime = HaiKangConvertUtils.DateToHaiKangLocalTime(timeParm.getStartTime());
		NET_DVR_TIME struStopTime = HaiKangConvertUtils.DateToHaiKangLocalTime(timeParm.getEndTime());
		
		NativeLong lPreviewHandle = hCNetSDK.NET_DVR_GetFileByTime(userId,
				new NativeLong(vodParam.getMonitorEntity().getChannelNum()), struStartTime, struStopTime,
				timeParm.getFile().getAbsolutePath());
		if (lPreviewHandle.intValue() < 0) {
			timeParm.setDownLoadState(DownLoadState.未下载);
			logger.warn("海康全局错误代码{},userId:{},channel:{},{},当前时间参数：{}", hCNetSDK.NET_DVR_GetLastError(), userId,
					vodParam.getMonitorEntity().getChannelNum(), "运行错误",timeParm);
			downLoadByTimeZone(timeParm);
		} else {
		
			logger.warn("海康全局错误代码{},userId:{},channel:{},{}", hCNetSDK.NET_DVR_GetLastError(), userId,
					vodParam.getMonitorEntity().getChannelNum(), "运行正确");
			//Ts 流 具有的特点就是精准的时间戳 能利用 工具 进行 视频截取
			hCNetSDK.NET_DVR_PlayBackControl(lPreviewHandle, HCNetSDK.NET_DVR_SET_TRANS_TYPE, 2, null);
			logger.warn("海康全局错误代码{},userId:{},channel:{},{},当前时间参数：{}", hCNetSDK.NET_DVR_GetLastError(), userId,
					vodParam.getMonitorEntity().getChannelNum(), "设置视频类型",timeParm);
			
			//下载必须执行这一行 才可以正常运行
			hCNetSDK.NET_DVR_PlayBackControl(lPreviewHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
			
			
			//设置下载速度
//			hCNetSDK.NET_DVR_PlayBackControl(lPreviewHandle, HCNetSDK.NET_DVR_SETSPEED, 180000, null);
//			logger.warn("海康全局错误代码{},userId:{},channel:{},{},当前时间参数：{}", hCNetSDK.NET_DVR_GetLastError(), userId,
//					vodParam.getMonitorEntity().getChannelNum(), "设置码率",timeParm);
//			
			
			//添加下载任务检测到 定时检测中
			downLoadTranscoding.addDownloadTask(new DownloadTaskAndBathTranscoding(lPreviewHandle,vodParam,timeParm,transcodingServer));
			//非转码
			//downloadTaskSchdule.addDownloadTask(new DownloadTask(lPreviewHandle,vodParam,timeParm,null));
			
		}		
	}
	
	public static void logout(){
		if(!userId.equals(new NativeLong(-1))){
			hCNetSDK.NET_DVR_Logout(userId);
			userId = new NativeLong(-1);
		}
	}
	public void login(){
		if(userId.equals(new NativeLong(-1))){
			init();
			NET_DVR_DEVICEINFO_V30 m_strDeviceInfo  = new NET_DVR_DEVICEINFO_V30();
			MonitorEntity entity = vodParam.getMonitorEntity();
			userId = hCNetSDK.NET_DVR_Login_V30(entity.getStreamNet().getIp(), Short.valueOf(entity.getVrPort()+""), entity.getVrUser().getName(), entity.getVrUser().getPassword(), m_strDeviceInfo);	
		}
	}
	public void init(){
		boolean initOk = hCNetSDK.NET_DVR_Init();
		
		if(initOk == Boolean.FALSE){
			logger.error("海康网络启动：失败");
			System.exit(1);
		}else{
			logger.warn("海康网络启动：成功");
		}
	}
	/**
	 * 兼容其他方案的做法 没有对时间进行处理
	 */
	@Override
	public void run() {
		try {
			QueryTimeParam queryTimeParam = vodParam.getTime();
			vodParam.setQueryTimeParams(Arrays.asList(queryTimeParam));
			File file = queryTimeParam.getFile();
			if(!file.exists())
				file.mkdirs();
			if(!file.isFile())
				queryTimeParam.setFile(new File(file,"video-"+UUID.randomUUID().toString()+".mp4"));
			downLoadByTimeZone(queryTimeParam);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
