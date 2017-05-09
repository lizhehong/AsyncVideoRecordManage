package cn.hy.videorecorder.timer;

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
 * 下载任务 同时批量转码 既下载完成片段转码
 * @author Administrator
 *
 */
public class DownloadTaskAndBathTranscoding implements  CallableI<DownloadTaskAndBathTranscoding> {

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
	
	public DownloadTaskAndBathTranscoding(NativeLong lFileHandle,VodParam vodParam,QueryTimeParam timeParm,TranscodingServerImpl transcodingServer){
		super();
		this.lFileHandle = lFileHandle;
		this.vodParam = vodParam;
		this.timeParm = timeParm;
		this.transcodingServer = transcodingServer;
	}
	
	
	
	public VodParam getVodParam() {
		return vodParam;
	}



	public QueryTimeParam getTimeParm() {
		return timeParm;
	}



	/**
	 * 返回非空 说明 需要继续执行(轮训下载进度)
	 */
	@Override
	public DownloadTaskAndBathTranscoding call() {
		try {
			IntByReference nPos = new IntByReference(0);
			
            hCNetSDK.NET_DVR_PlayBackControl(lFileHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
            
            this.downLoadProgress = nPos.getValue();
            
            if(downLoadProgress > 100 || downLoadProgress == 100){
            	
            	timeParm.setDownLoadState(DownLoadState.已经下载);
            	
            	//停止文件下载信号
            	boolean flag = hCNetSDK.NET_DVR_StopGetFile(lFileHandle);
            	logger.info("下载完毕:{}",timeParm);
            	//执行转码服务
    			if(flag && transcodingServer != null){
    				logger.info("转码开始：{}",timeParm);
    				String ffmpegCmdStr = QueryTimeParamUtils.transcodingWithGenernatorCmd(timeParm);
    				if(!StringUtils.isEmpty(ffmpegCmdStr)){
    					transcodingServer.addRunCmd(new TranscodingTask(ffmpegCmdStr,timeParm,vodParam,true));
    				}
    			}
    			return null;
            }else{
            	return this;
            }
            
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

}
