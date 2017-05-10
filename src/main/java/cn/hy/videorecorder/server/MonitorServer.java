package cn.hy.videorecorder.server;

import java.io.File;

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
	 * 以下载去执行点播 不存在点播索引文件时
	 * @param vodMonitorForm
	 * @return
	 * @throws Exception
	 */
	public VodParam  startDownLoadActionToVodByNewIndexFile(VodMonitorForm vodMonitorForm) throws Exception;
	
	/**
	 * 以下载去执行点播 存在点播索引文件时
	 * 做了文件判断
	 * @param vodMonitorForm
	 * @param indexFile
	 * @return
	 * @throws Exception
	 */
	public VodParam  startDownLoadActionToVodByOldIndexFile(VodMonitorForm vodMonitorForm,File indexFile) throws Exception;
}
