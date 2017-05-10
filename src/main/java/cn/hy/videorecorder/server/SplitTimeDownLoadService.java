package cn.hy.videorecorder.server;

import java.util.Date;

import cn.hy.videorecorder.bo.QueryTimeParam;
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
	
	/**
	 * 申请缓存视频
	 * @param cacheCount
	 * @param cacheMaxCount
	 * @param startTime
	 * @param queryTimeParam
	 * @return 已经缓存的个数
	 */
	public int applyCacheReVideo(int cacheCount, int cacheMaxCount, Date startTime, QueryTimeParam queryTimeParam);
}
