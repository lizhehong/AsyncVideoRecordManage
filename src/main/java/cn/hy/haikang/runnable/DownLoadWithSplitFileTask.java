package cn.hy.haikang.runnable;

import java.io.File;
import java.util.Arrays;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.server.StreamDownLoadServer;

public class DownLoadWithSplitFileTask implements Runnable {

	private StreamDownLoadServer streamDownLoadServer;
	
	private VodParam vodParam;
	
	public DownLoadWithSplitFileTask(VodParam vodParam,StreamDownLoadServer streamDownLoadServer) {
		super();
		this.streamDownLoadServer = streamDownLoadServer;
		this.vodParam = vodParam;
	}
	/**
	 * 时间整分化
	 */
	@Override
	public void run() {
		try {
			QueryTimeParam queryTimeParam = vodParam.getTime();
			
			
			
			
			
			vodParam.setQueryTimeParams(Arrays.asList(queryTimeParam));
			File file = queryTimeParam.getFile();
			if(!file.exists())
				file.mkdirs();
			if(!file.isFile())
				queryTimeParam.setFile(new File(file,"video-0.mp4"));
			streamDownLoadServer.downLoadByTimeZone(queryTimeParam);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
