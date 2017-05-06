package cn.hy.videorecorder.server.impl;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import cn.hy.videorecorder.bo.PullEndPackage;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.PullEndpointEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.repository.PullEndpointRepository;
import cn.hy.videorecorder.server.MonitorServer;
import cn.hy.videorecorder.server.PullEndpointClientServer;

@Service("pullEndpointClientServer")
public class PullEndpointClientServerImpl implements PullEndpointClientServer {

	private final RestTemplate restTemplate;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired @Qualifier("monitorServer")
	private MonitorServer monitorServer;
	
	@Autowired
	private PullEndpointRepository pullEndpointRepository;
	
	public PullEndpointClientServerImpl(RestTemplateBuilder restTemplateBuilder) {
		super();
		this.restTemplate = restTemplateBuilder.build();
	}
	
	@Override
	public boolean noticPullEndpoint(MonitorEntity monitorEntity) throws Exception {
		
		PullEndpointEntity pullEndpointEntity = findMinWorkByDoubleHeadrTime();
		
		if(pullEndpointEntity != null){
			NetIndentity net = pullEndpointEntity.getClientNet();
			
			//拼接推流端地址
			String pullEndPointUrl = "http://"+net.getIp()+":"+net.getPort()+"/cmd/live/add";
	
			HttpHeaders headers = new HttpHeaders();
	
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	
			headers.setContentType(type);
	
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			
			String cmd = monitorServer.gernatorFFmpegCmdByMonitorEntity(monitorEntity);
			
			if(StringUtils.isEmpty(cmd))
				return false;
			
			PullEndPackage paEndPackage = new PullEndPackage( cmd );
			
			restTemplate.postForObject(pullEndPointUrl+"?cmd="+paEndPackage.getCmd(), null,String.class);
			return true;
		}else{
			logger.info("找不到对应的推流端");
			return false;
		}
		
	}

	@Override
	public PullEndpointEntity findMinWorkByDoubleHeadrTime() {
		
		List<PullEndpointEntity> pullEndpointEntity = pullEndpointRepository.findAll();
		//未设置推流端
		if(pullEndpointEntity == null || pullEndpointEntity.size() == 0){
			return null;
		}
		
		return pullEndpointEntity.stream().min(Comparator.comparing(item->{
			PullEndpointEntity entity = (PullEndpointEntity)item;
			return entity.getHeartbeatMechanismTimeCur().getTime() - entity.getHeartbeatMechanismTimeLast().getTime();
		})).get();
		
	}
	
}
