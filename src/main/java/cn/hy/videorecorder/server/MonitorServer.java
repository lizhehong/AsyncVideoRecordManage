package cn.hy.videorecorder.server;

import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.form.monitor.VodMonitorForm;

public interface MonitorServer {

	/**
	 * 创建直播地址m monitorEntity
	 * @return
	 */
	public String createLiveAddress(MonitorEntity monitorEntity);
	/**
	 * 生成ffmpeg 命令
	 * @param monitorEntity
	 * @return
	 */
	public String gernatorFFmpegCmdByMonitorEntity(MonitorEntity monitorEntity);
	
	/**
	 * 以下载去执行点播
	 * @param vodMonitorForm
	 * @return
	 * @throws Exception
	 */
	public VodParam  startDownLoadActionToVod(VodMonitorForm vodMonitorForm) throws Exception;
}
