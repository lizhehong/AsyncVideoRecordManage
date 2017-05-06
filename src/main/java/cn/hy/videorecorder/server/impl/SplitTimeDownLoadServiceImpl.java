package cn.hy.videorecorder.server.impl;

import java.io.File;
import java.util.List;
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
import cn.hy.videorecorder.schdule.DownloadTaskSchdule;
import cn.hy.videorecorder.server.SplitTimeDownLoadService;
import cn.hy.videorecorder.utils.TimeUtils;

@Service("splitTimeDownLoadService")
public class SplitTimeDownLoadServiceImpl implements SplitTimeDownLoadService {

	@Value("${download.haikang.downloadTimeSplitSec}")
	private Integer haikangDownloadTimeSplitSec;
	
	@Autowired @Qualifier("downloadTaskSchdule")
	private DownloadTaskSchdule downloadTaskSchdule;
	
	/**
	 * 固定线程数
	 */
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
	
	
	public void createTimeSplitTask(VodParam vodParam) throws Exception {
		// 分割好的时间片段
		List<QueryTimeParam> oldQueryTimeParams = TimeUtils.splitTime(vodParam.getTime(),
				vodParam.getSplitSecStep());
		for (int i = 0; i < oldQueryTimeParams.size(); i++) {
			// 老的需要分割的时间
			QueryTimeParam queryTimeParam = oldQueryTimeParams.get(i);
			// 拼接文件名字
			File file = vodParam.getTime().getFile();
			// 固定的文件前缀
			String newFilename = "video-" + i;
			String suffix = ".mp4";
			// 新数据文件地址
			File newFile = new File(file.getAbsolutePath() + "\\" + newFilename + suffix);

			queryTimeParam.setFile(newFile);
			queryTimeParam.setDownLoadState(DownLoadState.未下载);
			vodParam.getQueryTimeParams().add(queryTimeParam);
		}
	}
	@Async
	public void startTask(VodParam vodParam) {
		try {
			switch (vodParam.getMonitorEntity().getVrUserType()) {
				case 海康:
					//切片时长
					vodParam.setSplitSecStep(haikangDownloadTimeSplitSec);
					fixedThreadPool.submit(new HaiKangServerImpl(vodParam,downloadTaskSchdule));
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
