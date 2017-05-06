package cn.hy.videorecorder.schdule;

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

import cn.hy.videorecorder.timer.DownloadTask;

@Service("downloadTaskSchdule")
public class DownloadTaskSchdule {

	private List<Future<DownloadTask>> oldDownloadTasks = new ArrayList<>();
	/**
	 * 固定线程池
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(3);
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 添加并行任务进程
	 * @param runnable
	 */
	public void addDownloadTask(DownloadTask downloadTask){
		oldDownloadTasks.add(executorService.submit(downloadTask));
	}
	/**
	 * 如果存在下载任务，则检测下载任务是否完成
	 * 检查任务是否完成
	 * 全部为异步 所以这里基本很低延时
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Scheduled(fixedDelay=1000*1)
	public synchronized void checkDownLoadTask() throws Exception{
		Iterator<Future<DownloadTask>> iterator = oldDownloadTasks.iterator();
		List<Future<DownloadTask>> newDownloadTasks = new ArrayList<>();
		while(iterator.hasNext()){
			Future<DownloadTask> dFuture = iterator.next();
			if(dFuture.isDone()){//保证任务执行完毕
				DownloadTask downloadTask = dFuture.get();
				iterator.remove();// 删除老任务
				if(downloadTask != null)// 追加新任务
					newDownloadTasks.add(executorService.submit(downloadTask));
			}
		}
		//老任务的检测放到下一个轮回
		oldDownloadTasks.addAll(newDownloadTasks);	
	}
	
}
