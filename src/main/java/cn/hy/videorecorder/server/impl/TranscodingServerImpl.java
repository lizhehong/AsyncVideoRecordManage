package cn.hy.videorecorder.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.timer.TranscodingTask;

/**
 * 资源损耗较大 特别是cpu所以这里的 poolSize 必须注意
 * @author Administrator
 *
 */
@Service("transcodingServer")
public class TranscodingServerImpl {
	/**
	 * 线程池数
	 */
	private final int poolSize = 2;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
	
	private List<TranscodingTask> transcodingTasks = new ArrayList<>();	
	
	public void addRunCmd(TranscodingTask transcodingTask){
		
		//这里其实有一部分逻辑
		//对于客户来说其实既然是时间段点播 那么最近的时间段才是他想要的 它不在乎其他的下载进度 也就是转码进度
		transcodingTasks.add(transcodingTask);
		
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay=2000)
	public void  arrangeTranscodingTask(){
		//时间降序排序器
		Comparator<TranscodingTask> c = new Comparator(){

			@Override
			public int compare(Object a0, Object a1) {
				TranscodingTask t1 = (TranscodingTask) a0;  
				TranscodingTask t2 = (TranscodingTask) a1;  
				long t1StartTime = t1.getQueryTimeParam().getStartTime().getTime();
				long t2StartTime = t2.getQueryTimeParam().getStartTime().getTime();
				return (int) (t1StartTime - t2StartTime);
			}         
		};
		List<TranscodingTask> clientTaskList = new ArrayList<>();
		
		Iterator<TranscodingTask> iterator = transcodingTasks.iterator();
		//拿到客户进行点播的进行转码
		while(iterator.hasNext()){
			TranscodingTask transcodingTask = iterator.next();
			if(transcodingTask.getQueryTimeParam().getVodReqState().equals(VodRequestState.已经请求)){
				clientTaskList.add(transcodingTask);
				iterator.remove();
			}
		}
		
		Collections.sort(clientTaskList,c);
		//if(clientTaskList.size() > 0)
			//logger.info("ffmpeg 转码命令 排序后：{}",clientTaskList);
		
		iterator = clientTaskList.iterator();
		
		//进行转码
		while(iterator.hasNext()){
			executorService.execute(iterator.next());
			iterator.remove();
		}
	}
	
}
