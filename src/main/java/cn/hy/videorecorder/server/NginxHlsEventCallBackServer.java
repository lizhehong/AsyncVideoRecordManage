package cn.hy.videorecorder.server;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.form.nginxhls.NginxHlsCallBackParam;

public interface NginxHlsEventCallBackServer {

	/**
	 * 通知全部服务器
	 */
	public void noticAllServicer_OnPublish(NginxHlsCallBackParam param,MonitorEntity monitorEntity);

	public void noticAllServicer_onPublishDone(NginxHlsCallBackParam param, MonitorEntity monitorEntity);
}
