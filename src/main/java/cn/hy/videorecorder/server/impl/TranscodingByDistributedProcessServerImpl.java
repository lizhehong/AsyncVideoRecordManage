package cn.hy.videorecorder.server.impl;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
	public void addRunCmd(TranscodingAndDownLoadTaskEntity task) {
		if(task!=null){
			//找到空闲转码服务器
			TranscodClientEntity client = transcodingClientRepsoitory.findFirstByFreeIsTrueOrderByUpdateTimeDesc();
			if(client != null){
				
				//通知客户端转码
				asyncNoticTranscodingClient(new AsyncTranscodPackage(task, client));
				
			}else{
				//如果没有合适的转码器 则 缓存起来
				if(!task.getTaskStep().equals(TaskStep.waiting)){//说明上一次已经进入等待过了 无需再次进入
					
					transcodingAndDownLoadTaskRespotity.save(task);
					
				}
			}
			
		}
		
	}
	/**
	 * 定时 唤醒等待的任务
	 */
	@Scheduled(fixedDelay=500)
	public void refreshTask(){
		//找到多个转码器
		List<TranscodClientEntity> clientList = transcodingClientRepsoitory.findByFreeIsTrueOrderByUpdateTimeDesc();
		if(clientList.size() > 0){
			//依据空闲的转码服务取出需要转码的任务 拿到前几个
			Page<TranscodingAndDownLoadTaskEntity> taskPage = transcodingAndDownLoadTaskRespotity.findByTaskStepWaiting(new PageRequest(0, clientList.size()));
			//存在转码任务
			if(taskPage.getTotalElements() > 0){
				
				List<TranscodingAndDownLoadTaskEntity> taskList = taskPage.getContent();
	
				for(int i=0;i<clientList.size();i++){
					TranscodClientEntity client = clientList.get(i);
					TranscodingAndDownLoadTaskEntity task = taskList.get(i);
					//异步通知转码客户端
					asyncNoticTranscodingClient(new AsyncTranscodPackage(task, client));				
				}
			}
		}
	}
	
	/**
	 * 通知解码器 异步
	 * @param entity
	 * @return 
	 */
	private void asyncNoticTranscodingClient(AsyncTranscodPackage reqPackage){
		
		TranscodingAndDownLoadTaskEntity task = reqPackage.getTask();
		
		task.setTaskStep(TaskStep.asyncTranscoding);
		
		transcodingAndDownLoadTaskRespotity.save(task);
		
		TranscodClientEntity client = reqPackage.getClient();
		client.setFree(false);
		
		transcodingClientRepsoitory.save(client);
		
		NetIndentity net = client.getClientNet();
		
		//拼接点播端转码地址
		String transcodingServerUrl = "http://"+net.getIp()+":"+net.getPort()+"/vod/transcod";
		
		switch(task.getMonitorEntity().getVrUserType()){
			case 海康:
				transcodingServerUrl+="/hk";
				break;
			default:
				transcodingServerUrl+="/hk";
				break;
		}
		
		
		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		headers.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());
		
		HttpEntity<TranscodingAndDownLoadTaskEntity> requestEntity = new HttpEntity<>(task, headers);
		//既然异步 则不理会返回值
		asyncRestTemplate.postForEntity(transcodingServerUrl,requestEntity,null);
		
			
	}
}
