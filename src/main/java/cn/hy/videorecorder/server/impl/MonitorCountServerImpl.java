package cn.hy.videorecorder.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hy.videorecorder.entity.MonitorClientServiceConnectionEntity;
import cn.hy.videorecorder.entity.MonitorCountEntity;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.form.monitorcount.AddOneMonitorCountForm;
import cn.hy.videorecorder.form.monitorcount.UpdateOneMonitorCountForm;
import cn.hy.videorecorder.repository.MonitorClientServiceConnectionRepository;
import cn.hy.videorecorder.repository.MonitorCountRepository;
import cn.hy.videorecorder.repository.MonitorRepository;
import cn.hy.videorecorder.server.MonitorCountServer;

@Service("monitorCountServer")
public class MonitorCountServerImpl implements MonitorCountServer {

	@Autowired
	private MonitorCountRepository monitorCountRepository;
	
	@Autowired
	private MonitorRepository monitorRepository;
	
	@Autowired
	private MonitorClientServiceConnectionRepository monitorClientServiceConnectionRepository;
	
	@Override
	public MonitorCountEntity addOneCount(AddOneMonitorCountForm form) {
		
		MonitorEntity monitor = monitorRepository.findOne(form.getMonitorId());
		
		MonitorClientServiceConnectionEntity monitorClientServiceConnection = monitorClientServiceConnectionRepository.findOne(form.getMonitorClientServiceConnectionId());
		
		
		MonitorCountEntity entity = new  MonitorCountEntity();		
		entity.setMonitor(monitor);
		entity.setMonitorClientServiceConnection(monitorClientServiceConnection);
		
		
		
		return monitorCountRepository.save(entity);
	}

	@Override
	public MonitorCountEntity updateOneCount(UpdateOneMonitorCountForm form) {

		monitorCountRepository.updateOnlineNum(form.getId(),form.getOnlineNum());
		
		return monitorCountRepository.findOne(form.getId());
	}
	
}
