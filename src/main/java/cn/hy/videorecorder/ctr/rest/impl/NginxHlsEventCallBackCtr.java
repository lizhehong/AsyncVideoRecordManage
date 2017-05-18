package cn.hy.videorecorder.ctr.rest.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.type.CallType;
import cn.hy.videorecorder.form.nginxhls.NginxHlsCallBackParam;
import cn.hy.videorecorder.repository.MonitorRepository;
import cn.hy.videorecorder.repository.PullEndpointRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class NginxHlsEventCallBackCtr {

	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MonitorRepository monitorRepository;
	@Autowired
	private PullEndpointRepository pullEndpointRepository;
	
	@ApiOperation(value = "hls回调", notes = "发布事件")
	@PostMapping("hlsCallBack/onPublic")
	public void onPublic(
			@ApiParam(name = "param", required = true, value = "回调参数") 
			@ModelAttribute("param")
			NginxHlsCallBackParam param){
		
		String monitorId = param.getName();
		MonitorEntity entity = monitorRepository.findOne(monitorId);
		entity.setPushState(true);
		entity.setStreamState(CallType.publish);
		monitorRepository.save(entity);
		logger.info("发布事件:"+param);
	}
	
	@ApiOperation(value = "hls回调", notes = "发布完成(流停止)")
	@PostMapping("hlsCallBack/onPublishDone")
	public void onPublishDone(
			@ApiParam(name = "param", required = true, value = "回调参数") 
			@ModelAttribute("param")
			NginxHlsCallBackParam param){
		
		String monitorId = param.getName();
		MonitorEntity entity = monitorRepository.findOne(monitorId);
		entity.setPushState(false);
		entity.setStreamState(CallType.publish_done);
		logger.info("发布完成(流停止):"+param);
		
	}
	
	@ApiOperation(value = "hls回调", notes = "更新事件")
	@PostMapping("hlsCallBack/onUpdate")
	public void onUpdate(
			@ApiParam(name = "param", required = true, value = "回调参数") 
			@ModelAttribute("param")
			NginxHlsCallBackParam param
			){
		String monitorId = param.getName();
		MonitorEntity entity = monitorRepository.findOne(monitorId);
		entity.setHeartTime(new Date());
		entity.setStreamState(CallType.update_publish);
		monitorRepository.save(entity);
		pullEndpointRepository.updateHeartByAddr(new Date(), param.getAddr());
		logger.info("更新事件:"+param);
		
	}
}
