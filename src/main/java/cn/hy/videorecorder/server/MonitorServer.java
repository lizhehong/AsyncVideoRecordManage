package cn.hy.videorecorder.server;

import cn.hy.videorecorder.entity.MonitorEntity;

public interface MonitorServer {

	public String createLiveAddress(MonitorEntity monitorEntity);
	
	
	public String gernatorFFmpegCmdByMonitorEntity(MonitorEntity monitorEntity);
	
}
