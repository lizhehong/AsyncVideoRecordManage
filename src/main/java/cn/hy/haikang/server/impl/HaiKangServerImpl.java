package cn.hy.haikang.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.NativeLong;

import cn.hy.haikang.config.HCNetSDK;
import cn.hy.haikang.config.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import cn.hy.haikang.config.HCNetSDK.NET_DVR_TIME;
import cn.hy.haikang.type.DownLoadState;
import cn.hy.haikang.utils.HaiKangConvertUtils;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.schdule.DownloadTaskSchdule;
import cn.hy.videorecorder.server.StreamDownLoadServer;
import cn.hy.videorecorder.timer.DownloadTask;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

public class HaiKangServerImpl implements StreamDownLoadServer{

	private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 当前的点播参数
	 */
	private VodParam vodParam;

	public static NativeLong userId = new NativeLong(-1);
	
	private DownloadTaskSchdule downloadTaskSchdule;
	
	/**
	 * 
	 * @param vodParam 已经分隔好的
	 */
	public HaiKangServerImpl(VodParam vodParam,DownloadTaskSchdule downloadTaskSchdule) {
		super();
		this.vodParam = vodParam;
		this.downloadTaskSchdule = downloadTaskSchdule;
		login();
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
			logger.warn("海康全局错误代码{},userId:{},channel:{},{}", hCNetSDK.NET_DVR_GetLastError(), userId,
					vodParam.getMonitorEntity().getChannelNum(), "运行错误");
			downLoadByTimeZone(timeParm);
		} else {
			
			logger.warn("海康全局错误代码{},userId:{},channel:{},{}", hCNetSDK.NET_DVR_GetLastError(), userId,
					vodParam.getMonitorEntity().getChannelNum(), "运行正确");
			//下载必须执行这一行 才可以正常运行
			hCNetSDK.NET_DVR_PlayBackControl(lPreviewHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
			
			//添加下载任务检测到 定时检测中
			downloadTaskSchdule.addDownloadTask(new DownloadTask(lPreviewHandle,vodParam,timeParm));
			
			
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

	@Override
	public void run() {
		QueryTimeParamUtils.downLoadWithSplitTime(vodParam.getQueryTimeParams(),this);
	}

	
}
