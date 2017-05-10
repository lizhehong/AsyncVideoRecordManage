package cn.hy.videorecorder.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import cn.hy.haikang.config.HCNetSDK;
import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.server.impl.TranscodingServerImpl;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

/**
 * 下载任务 检测文件大小 达到则 转换文件
 * @author Administrator
 *
 */
public class DownLoadTaskAndSplitFileTranscoding implements  CallableI<DownLoadTaskAndSplitFileTranscoding>  {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	/**
	 * 下載的句柄
	 */
	private NativeLong lFileHandle;
	/**
	 * 当前点播参数
	 */
	private VodParam vodParam;
	
	/**
	 * 当前时间参数
	 */
	private QueryTimeParam timeParm;
	
	/**
	 * 当前下载进度
	 */
	private int downLoadProgress;
	
	/**
	 * 转码服务
	 */
	private TranscodingServerImpl transcodingServer;
	
	
	private List<Long> splitedList = new ArrayList<>();
	/**
	 * 为了让客户不会丢失前几秒 默认第一个是 用户输入的时间
	 */
	private Calendar curCalendar = Calendar.getInstance();
	
	private Date taskStartTime = new Date();
	
	public DownLoadTaskAndSplitFileTranscoding(NativeLong lFileHandle,VodParam vodParam,QueryTimeParam timeParm,TranscodingServerImpl transcodingServer){
		super();
		this.lFileHandle = lFileHandle;
		this.vodParam = vodParam;
		this.timeParm = timeParm;
		this.transcodingServer = transcodingServer;
		curCalendar.setTime(vodParam.getTime().getStartTime());
	}
	
	
	
	public VodParam getVodParam() {
		return vodParam;
	}



	public QueryTimeParam getTimeParm() {
		return timeParm;
	}



	@Override
	public DownLoadTaskAndSplitFileTranscoding call() throws Exception {
		try {
			IntByReference nPos = new IntByReference(0);
			
            hCNetSDK.NET_DVR_PlayBackControl(lFileHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
            
            this.downLoadProgress = nPos.getValue();
            
            if(downLoadProgress > 100 || downLoadProgress == 100){
            	
            	//停止文件下载信号
            	boolean flag = hCNetSDK.NET_DVR_StopGetFile(lFileHandle);
            	long sizeLen = timeParm.getDownLoadFile().length();
            	long timeLen = new Date().getTime()-taskStartTime.getTime();
            	logger.info("运行时间:{},文件大小:{},网速：{},下载完毕:{}",timeLen,sizeLen,sizeLen/(timeLen*1.024),timeParm);
            	//执行转码服务
    			if(flag && transcodingServer != null){
    				//logger.info("最后片段转码开始：{}",timeParm);
//    				String ffmpegCmdStr = QueryTimeParamUtils.transcodingWithGenernatorCmd(timeParm);
//    				if(!StringUtils.isEmpty(ffmpegCmdStr)){
//    					transcodingServer.addRunCmd(new TranscodingTask(ffmpegCmdStr,timeParm,vodParam,true));
//    				}
    			}
    			return null;
            }else{
            	//checkDownLoadProgressToTranscoding();
            	if(!timeParm.getDownLoadState().equals(DownLoadState.下载中)){
	            	try {
	        			timeParm.setDownLoadState(DownLoadState.下载中);
	        			QueryTimeParamUtils.storgeInfo(timeParm.getTranscodedFile().getParentFile(), vodParam);
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
            	}
            	return this;
            }
            
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}



	private void checkDownLoadProgressToTranscoding() {
		File file = timeParm.getDownLoadFile();
		
		//该视频点播源文件每秒的文件大小
		long vodSizeByperSec= vodParam.getMonitorEntity().getVodSizeByperSec();
		//检测文件是否到达指定的步长倍数
		logger.info("点播视频源文件:{},大小：{},数据库点播文件每秒大小：{},系统时间步长：{}",file.getAbsolutePath(),file.length(),vodSizeByperSec,vodParam.getSplitSecStep());
		if(file.exists() && file.isFile()){
			Long multiple = file.length() / (vodSizeByperSec*vodParam.getSplitSecStep());
			logger.info("当前文件进度大小：{}",multiple);
			if(!splitedList.contains(multiple)){//文件容量到达指定大小
				//计算命令需要的时间偏移量
				int curMinute = curCalendar.get(Calendar.MINUTE);
				int curSec = curCalendar.get(Calendar.SECOND);
				
				
				QueryTimeParam queryTimeParam = vodParam.getQueryTimeParams().get(0);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(queryTimeParam.getStartTime());
				int minute = calendar.get(Calendar.MINUTE);
				int sec = calendar.get(Calendar.SECOND);
				
				//计算小时差
				long hourDiff = (curCalendar.getTime().getTime() - calendar.getTime().getTime())/(24*60*60*1000);
				
				
				String offsetDate =	hourDiff + ":" +
									(curMinute - minute) + ":" +
									(curSec - sec)+".000";
				
				//还没进过抽取解码
				String ffmpegCmdStr = QueryTimeParamUtils.transcodingWithGenernatorCmd(timeParm,vodParam.getSplitSecStep(),offsetDate);
				
				
				if(!StringUtils.isEmpty(ffmpegCmdStr)){
					transcodingServer.addRunCmd(new TranscodingTask(ffmpegCmdStr,timeParm,vodParam,false));
				}
				
				
				splitedList.add(multiple);
				curCalendar.add(Calendar.SECOND, vodParam.getSplitSecStep());
			}
		}
	}
}
