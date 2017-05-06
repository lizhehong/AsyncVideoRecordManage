package cn.hy.videorecorder.server;

import cn.hy.videorecorder.entity.MonitorCountEntity;
import cn.hy.videorecorder.form.monitorcount.AddOneMonitorCountForm;
import cn.hy.videorecorder.form.monitorcount.UpdateOneMonitorCountForm;

public interface MonitorCountServer {

	public MonitorCountEntity addOneCount(AddOneMonitorCountForm form);
	
	public MonitorCountEntity updateOneCount(UpdateOneMonitorCountForm form);
}
