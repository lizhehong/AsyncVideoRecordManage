package cn.hy.videorecorder.form.monitor;

import java.util.Date;

import lombok.Data;

@Data
public class VodMonitorForm {

	private Date startTime;
	
	private Date endTime;
	
	private String monitorId;
}
