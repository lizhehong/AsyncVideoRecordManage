package cn.hy.videorecorder.server;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.PullEndpointEntity;


public interface PullEndpointClientServer {

	public boolean noticPullEndpoint(MonitorEntity monitorEntity) throws Exception ;

	
	public  PullEndpointEntity findMinWorkByDoubleHeadrTime();
}
