package cn.hy.videorecorder.server.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;

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
	
	
	ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
	
	public void addRunCmd(TranscodingTask transcodingTask){
		
		executorService.execute(transcodingTask);
		
	}
	
}
