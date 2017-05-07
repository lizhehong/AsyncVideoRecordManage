package cn.hy.videorecorder.timer;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import cn.hy.haikang.config.HCNetSDK;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.server.impl.TranscodingServerImpl;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

public class DownloadTask implements  Callable<DownloadTask> {

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
	
	public DownloadTask(NativeLong lFileHandle,VodParam vodParam,QueryTimeParam timeParm,TranscodingServerImpl transcodingServer){
		super();
		this.lFileHandle = lFileHandle;
		this.vodParam = vodParam;
		this.timeParm = timeParm;
		this.transcodingServer = transcodingServer;
	}
	/**
	 * 返回非空 说明 需要继续执行(轮训下载进度)
	 */
	@Override
	public DownloadTask call() {
		try {
			IntByReference nPos = new IntByReference(0);
			
            hCNetSDK.NET_DVR_PlayBackControl(lFileHandle, HCNetSDK.NET_DVR_PLAYGETPOS, 0, nPos);
            
            this.downLoadProgress = nPos.getValue();
            
            if(downLoadProgress > 100 || downLoadProgress == 100){
            	
            	//停止文件下载信号
            	boolean flag = hCNetSDK.NET_DVR_StopGetFile(lFileHandle);
            	//执行转码服务
    			if(flag){
    				logger.info("转码开始：{}",timeParm);
    				String ffmpegCmdStr = QueryTimeParamUtils.transcodingWithGenernatorCmd(timeParm);
    				if(!StringUtils.isEmpty(ffmpegCmdStr)){
    					transcodingServer.addRunCmd(new TranscodingTask(ffmpegCmdStr,timeParm,vodParam));
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
