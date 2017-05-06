package cn.hy.videorecorder.timer;

import java.util.concurrent.Callable;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import cn.hy.haikang.config.HCNetSDK;
import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.FFmpegProcess;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

public class DownloadTask implements  Callable<DownloadTask> {

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
	 * 下载进度
	 */
	private int downLoadProgress;
	
	public DownloadTask(NativeLong lFileHandle,VodParam vodParam,QueryTimeParam timeParm){
		super();
		this.lFileHandle = lFileHandle;
		this.vodParam = vodParam;
		this.timeParm = timeParm;
	}
	/**
	 * 返回非空 说明 需要继续执行
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
            	
    			if(flag){
    				FFmpegProcess fFmpegProcess = QueryTimeParamUtils.transcoding(timeParm);
    				if( fFmpegProcess != null ){
    					fFmpegProcess.getProcess().waitFor();//等待转换完畢
    					fFmpegProcess.getProcess().destroyForcibly();
    					fFmpegProcess.getErrorGobbler().destroy();
    					fFmpegProcess.getOutputGobbler().destroy();
    					timeParm.setDownLoadState(DownLoadState.已经下载);
    					QueryTimeParamUtils.storgeInfo(timeParm.getFile().getParentFile(),vodParam);
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
