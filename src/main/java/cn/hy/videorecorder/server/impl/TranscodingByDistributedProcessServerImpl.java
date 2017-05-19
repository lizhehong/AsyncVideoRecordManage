package cn.hy.videorecorder.server.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;

import cn.hy.videorecorder.bo.AsyncTranscodPackage;
import cn.hy.videorecorder.entity.TranscodClientEntity;
import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.entity.type.TaskStep;
import cn.hy.videorecorder.repository.TranscodingAndDownLoadTaskRespotity;
import cn.hy.videorecorder.repository.TranscodingClientRepsoitory;
import cn.hy.videorecorder.server.TranscodingServer;

@Service("transcodingByDistributedProcessServer")
public class TranscodingByDistributedProcessServerImpl implements TranscodingServer<TranscodingAndDownLoadTaskEntity> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private AsyncRestTemplate asyncRestTemplate;
	
	
	@Autowired
	private TranscodingClientRepsoitory transcodingClientRepsoitory;
	
	@Autowired
	private TranscodingAndDownLoadTaskRespotity transcodingAndDownLoadTaskRespotity;
	
	public TranscodingByDistributedProcessServerImpl(RestTemplateBuilder restTemplateBuilder) {
		super();
		this.asyncRestTemplate = new AsyncRestTemplate();
	}
	
	/**
	 * 添加解码命令堆栈
	 */
	@Override
	@Async
	public void asyncStartTask(TranscodingAndDownLoadTaskEntity task,TranscodClientEntity client) {
		if(task!=null){
			if(client != null){
				//通知客户端转码
				asyncNoticTranscodingClient(new AsyncTranscodPackage(task, client));
				
			}else{
				//如果没有合适的转码器 则 缓存起来
				if(task.getTaskStep() == null || task.getTaskStep().equals(TaskStep.none)){
					task.setTaskStep(TaskStep.waiting);
					transcodingAndDownLoadTaskRespotity.save(task);
					
				}
			}
			
		}
		
	}
	@PostConstruct
	public void PostConstruct(){
		transcodingClientRepsoitory.setOnline(false);
	}
	
	/**
	 * 定时 唤醒等待的任务
	 */
	@Scheduled(fixedDelay=500)
	public synchronized void refreshTask(){
		
		//找到多个转码器
		List<TranscodClientEntity> clientList = transcodingClientRepsoitory.findByFreeIsTrueAndOnline(true);
		if(clientList.size() == 0){
			return ;
		}
		//依据空闲的转码服务取出需要转码的任务 拿到前几个
		
		Page<TranscodingAndDownLoadTaskEntity> taskPage = transcodingAndDownLoadTaskRespotity.findByTaskStep(TaskStep.waiting,new PageRequest(0, clientList.size()));
		
		//存在转码任务
		if(taskPage.getTotalElements() > 0){
			
			List<TranscodingAndDownLoadTaskEntity> taskList = taskPage.getContent();
			//大小为可用转码器的数量一致
			for(int i=0;i<clientList.size();i++){
				TranscodClientEntity client = clientList.get(i);
				TranscodingAndDownLoadTaskEntity task = taskList.get(i);
				//异步通知转码客户端
				Long start = System.currentTimeMillis();
				asyncNoticTranscodingClient(new AsyncTranscodPackage(task, client));
				logger.info("Time:{}",System.currentTimeMillis()-start);
			}
		}
		
	}
	
	/**
	 * 通知解码器 异步
	 * @param entity
	 * @return 
	 */
	private void asyncNoticTranscodingClient(AsyncTranscodPackage reqPackage){
		
		
		TranscodClientEntity client = reqPackage.getClient();
		TranscodClientEntity clientInDb = transcodingClientRepsoitory.findOne(client.getId());
		if(clientInDb.getNowDownLoadSize() < clientInDb.getDownLoadPoolSize()){
			client.setNowDownLoadSize(clientInDb.getNowDownLoadSize()+1);
			client.setFree(true);
		}else
			client.setFree(false);
		transcodingClientRepsoitory.save(client);//切记要这一步
		
		//查询客户端是否已经达到设定的转码最大值
		
		TranscodingAndDownLoadTaskEntity task = reqPackage.getTask();
		
		task.setTaskStep(TaskStep.asyncTranscoding);
		task.setClient(client);
		
		transcodingAndDownLoadTaskRespotity.save(task);
	
		
		NetIndentity net = client.getClientNet();
		
		//拼接点播端转码地址
		String transcodingServerUrl = "http://"+net.getIp()+":"+net.getPort()+"/vod/transcod";
		
		//不同视频录像机采用不同的controller
		switch(task.getMonitorEntity().getVrUserType()){
			case 海康:
				transcodingServerUrl+="/hk";
				break;
			default:
				transcodingServerUrl+="/hk";
				break;
		}
		
		HttpEntity<TranscodingAndDownLoadTaskEntity> requestEntity = new HttpEntity<>(task, new HttpHeaders());

		asyncRestTemplate.postForEntity(transcodingServerUrl,requestEntity,String.class);
		
		
			
	}

	@Override
	public void asyncStartTask(TranscodingAndDownLoadTaskEntity transcodingTask) {
		throw new AbstractMethodError("方法没有实现");
	}
}
