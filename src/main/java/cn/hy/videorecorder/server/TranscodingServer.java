package cn.hy.videorecorder.server;

import cn.hy.videorecorder.entity.TranscodClientEntity;

public interface TranscodingServer<T> {
	/**
	 * 添加一条转码命令
	 * @param transcodingTask
	 * @param client 无客户端则任务缓存
	 */
	public void asyncStartTask(T transcodingTask,TranscodClientEntity client);
	
	public void asyncStartTask(T transcodingTask);
}
