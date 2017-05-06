package cn.hy.videorecorder.server.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.StreamNetBo;
import cn.hy.videorecorder.entity.MonitorClientServiceConnectionEntity;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.form.nginxhls.NginxHlsCallBackParam;
import cn.hy.videorecorder.repository.MonitorClientServiceConnectionRepository;
import cn.hy.videorecorder.server.NginxHlsEventCallBackServer;

@Service("nginxHlsEventCallBackServer")
public class NginxHlsEventCallBackServerImpl implements NginxHlsEventCallBackServer {

	@Autowired 
	private MonitorClientServiceConnectionRepository monitorClientServiceConnectionRepository;
	
	@Value("${spring.mvc.date-format}")
	private String dateFormate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private final RestTemplate restTemplate;
	
	public NginxHlsEventCallBackServerImpl(RestTemplateBuilder restTemplateBuilder) {
		super();
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async
	@Override
	public void noticAllServicer_OnPublish(NginxHlsCallBackParam param,MonitorEntity monitorEntity){
		
		List<MonitorClientServiceConnectionEntity>  entityList = monitorClientServiceConnectionRepository.findAll();
		for(MonitorClientServiceConnectionEntity entity:entityList){
			String action = entity.getOnPublishCallBackAction();
			if(!StringUtils.isEmpty(action))
				postToAim(monitorEntity, entity, action);
		}
		
	}
	
	@Async
	@Override
	public void noticAllServicer_onPublishDone(NginxHlsCallBackParam param, MonitorEntity monitorEntity) {
		List<MonitorClientServiceConnectionEntity>  entityList = monitorClientServiceConnectionRepository.findAll();
		for(MonitorClientServiceConnectionEntity entity:entityList){
			String action = entity.getOnPublishDoneCallBackAction();
			if(!StringUtils.isEmpty(action))
				postToAim(monitorEntity, entity, action);
		}
		
	}
	@Async
	private void postToAim(MonitorEntity monitorEntity, MonitorClientServiceConnectionEntity entity, String action) {
		try {
			NetIndentity net = entity.getNet();
			String url = "Http://"+net.getIp()+":"+net.getPort()+action;
			HttpHeaders headers = new HttpHeaders();

			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");

			headers.setContentType(type);

			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			DateFormat oldDateFormate = objectMapper.getDateFormat();
			DateFormat dateFormat =new SimpleDateFormat(dateFormate);
			objectMapper.setDateFormat(dateFormat );
			
			StreamNetBo bo = new StreamNetBo();
			BeanUtils.copyProperties(bo, monitorEntity);
			
			HttpEntity<String> formEntity = new HttpEntity<String>(objectMapper.writeValueAsString(bo), headers);
			
			restTemplate.postForObject(url, formEntity,null);
			
			objectMapper.setDateFormat(oldDateFormate);
			
		} catch (Exception e) {
			
		}
	}


	
	
}
