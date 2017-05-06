package cn.hy.videorecorder.server;

import cn.hy.videorecorder.bo.VodParam;

public interface SplitTimeDownLoadService{

	/**
	 * 开始时间分割下载任务
	 * @param vodParam
	 */
	public void startTask(VodParam vodParam);
	
	/**
	 * 创建时间切片任务
	 * 
	 * @param vodParam
	 * @throws Exception
	 */
	public void createTimeSplitTask(VodParam vodParam) throws Exception;
}
