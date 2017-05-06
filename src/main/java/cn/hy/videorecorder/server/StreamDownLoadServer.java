package cn.hy.videorecorder.server;


import cn.hy.videorecorder.bo.QueryTimeParam;

public interface StreamDownLoadServer extends Runnable{
	
	/**
	 * 下载指定的时间内文件
	 * @return 下载句柄
	 * @throws InterruptedException 
	 */
	public void downLoadByTimeZone(QueryTimeParam timeParm) throws Exception;

}
