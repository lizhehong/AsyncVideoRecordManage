package cn.hy.videorecorder.schdule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.timer.DownloadTask;

@Service("downloadTaskSchdule")
public class DownloadTaskSchdule {

	private List<Future<DownloadTask>> oldDownloadTasks = new ArrayList<>();
	/**
	 * 固定线程池 同时监听N路下载
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(30);
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 添加并行任务进程
	 * @param runnable
	 */
	public void addDownloadTask(DownloadTask downloadTask){
		//拿到当前需要下载的时间片段
		QueryTimeParam timeParam =  downloadTask.getTimeParm();
		File localFile = new File(timeParam.getFile().getParentFile(),"index.json");
		//TODO 检测是否下载 如果是则 无需进入
	
		
		oldDownloadTasks.add(executorService.submit(downloadTask));
	}
	/**
	 * 如果存在下载任务，则检测下载任务是否完成
	 * 检查任务是否完成
	 * 全部为异步 所以这里基本很低延时
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Scheduled(fixedDelay=300)
	public void checkDownLoadTask() throws Exception{
		long start = System.currentTimeMillis();
		Iterator<Future<DownloadTask>> iterator = oldDownloadTasks.iterator();
		List<Future<DownloadTask>> newDownloadTasks = new ArrayList<>();
		while(iterator.hasNext()){
			Future<DownloadTask> dFuture = iterator.next();
			if(dFuture.isDone()){//保证任务执行完毕
				DownloadTask downloadTask = dFuture.get();
				iterator.remove();// 取消任务监听
				if(downloadTask !=null)//本次监听下 还没有完成 下载任务需要下一次监听
					newDownloadTasks.add((executorService.submit(downloadTask)));
			}
		}
		//老任务的检测放到下一个轮回	
		oldDownloadTasks.addAll(newDownloadTasks);
		logger.info("一轮检测下载任务运行时间：{},下次剩余监听量：{}",System.currentTimeMillis()-start,oldDownloadTasks.size());
	}
	
}
