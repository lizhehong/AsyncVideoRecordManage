package cn.hy.videorecorder.server;


public interface TranscodingServer<T> {
	/**
	 * 添加一条转码命令
	 * @param transcodingTask
	 */
	public void addRunCmd(T transcodingTask);
}
