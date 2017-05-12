package cn.hy.videorecorder.form.monitor;

import java.util.Date;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;


import lombok.Data;

@Data
public class VodMonitorForm {

	@Past(message="开始时间不对")
	private Date startTime;
	@Past(message="结束时间不对")
	private Date endTime;
	@Size(max=36,min=36,message="长度不匹配")
	private String monitorId;
}
