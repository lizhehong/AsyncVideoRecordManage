package cn.hy.videorecorder.schdule;

import cn.hy.videorecorder.timer.CallableI;

public interface DownLoadTranscoding<T> {

	public void addDownloadTask(CallableI<T> Callable);
	
}
