package cn.hy.videorecorder.schdule;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.type.CallType;
import cn.hy.videorecorder.repository.MonitorRepository;

@Service("monitorSchdule")
public class MonitorSchdule {
	
	@Autowired
	private MonitorRepository monitorRepository;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 检测正在推送的视频流的状态
	 */
	@Scheduled(fixedDelay=1000*5)
	public void checkMonitorState(){
		long nowTime = new Date().getTime();
		
		//List<MonitorEntity> monitorEntities = monitorRepository.findByPushState(true);
		List<MonitorEntity> monitorEntities = monitorRepository.findAll();
		for(MonitorEntity entity:monitorEntities){
			Date heartTime = entity.getHeartTime();
			if(heartTime !=null && nowTime - entity.getHeartTime().getTime() >  30*1000){
				logger.info(nowTime - entity.getHeartTime().getTime()+"");
				entity.setStreamState(CallType.none);
				entity.setPushState(false);
				entity.setHeartTime(null);//防止 总是进入save
				monitorRepository.save(entity);
			}
		}
	}
}
