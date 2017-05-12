package cn.hy.videorecorder.server.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cn.hy.haikang.server.impl.HaiKangServerImpl;
import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.schdule.DownLoadTranscoding;
import cn.hy.videorecorder.server.SplitTimeDownLoadService;
import cn.hy.videorecorder.timer.DownLoadTaskAndSplitFileTranscoding;
import cn.hy.videorecorder.timer.DownloadTaskAndBathTranscoding;
import cn.hy.videorecorder.utils.TimeUtils;
/**
 * 分割视频点播视频时长 下载
 * @author 李哲弘
 *
 */
@Service("splitTimeDownLoadService")
public class SplitTimeDownLoadServiceImpl implements SplitTimeDownLoadService {
	
	@Autowired @Qualifier("downloadTaskAndTranscodingFileSchdule")
	private DownLoadTranscoding<DownloadTaskAndBathTranscoding> downLoadTranscodingByBath;
	
	@Autowired @Qualifier("downloadTaskSplitFileTranscodingSchdule")
	private DownLoadTranscoding<DownLoadTaskAndSplitFileTranscoding> downLoadTranscodingBySplitFile;
	
	@Autowired @Qualifier("transcodingServer")
	private TranscodingServerImpl transcodingServer;
	
	
	@Value("${cache.videoList.cacheMaxCount}")
	private Integer cacheMaxCount;
	
	/**
	 * 固定线程数 同时N个下载任务
	 */
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
	
	public void createTimeSplitTask(VodParam vodParam) throws Exception {
		// 分割好的时间片段 
		List<QueryTimeParam> oldQueryTimeParams = TimeUtils.fillFullMinAndSplitTime(vodParam.getTime(),
				vodParam.getSplitSecStep(),cacheMaxCount);
		int cacheCount = 0;
		
		Date startTime = vodParam.getTime().getStartTime();
		for (int i = 0; i < oldQueryTimeParams.size(); i++) {
			// 老的需要分割的时间
			QueryTimeParam queryTimeParam = oldQueryTimeParams.get(i);
			// 拼接文件名字
			File file = vodParam.getTime().getDownLoadFile();

			queryTimeParam.setDownLoadFile(new File(file.getAbsolutePath() + "\\" + "video-" + UUID.randomUUID().toString() + ".mp4"));
			
			queryTimeParam.setTranscodedFile(new File(file.getAbsolutePath() + "\\" + "video-" + UUID.randomUUID().toString() + ".mp4"));
			
			queryTimeParam.setDownLoadState(DownLoadState.未下载);
			
			cacheCount = applyCacheReVideo(cacheCount, cacheMaxCount, startTime, queryTimeParam);
			
			
			vodParam.getQueryTimeParams().add(queryTimeParam);
		}
	}
	
	public int applyCacheReVideo(int cacheCount, int cacheMaxCount, Date startTime, QueryTimeParam queryTimeParam) {
		if(cacheCount > 0 && cacheCount < cacheMaxCount){//说明已经找到了第一个
			queryTimeParam.setVodReqState(VodRequestState.已经请求);
			cacheCount++;
		}else if(queryTimeParam.getStartTime().getTime() <= startTime.getTime() && startTime.getTime() <= queryTimeParam.getEndTime().getTime() ){//找出用户点播时间在 系统时间分隔中的哪些中
			queryTimeParam.setVodReqState(VodRequestState.已经请求);
			cacheCount++;
		}
		return cacheCount;
	}
	@Async
	public void startTask(VodParam vodParam) {
		try {
			switch (vodParam.getMonitorEntity().getVrUserType()) {
				case 海康:
					//注入 下载进度轮训 下载后的转码服务
					fixedThreadPool.submit(new HaiKangServerImpl(vodParam,downLoadTranscodingByBath,transcodingServer).DOWNLOAD_WITHSPLITTIME_TASK);
					break;
				case 大华:
					break;
	
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
