package cn.hy.videorecorder.bo;

import java.util.Date;

import lombok.Data;

@Data
public class TimeZone {

	private Date startTime;
	
	private Date endTime;

	public TimeZone(Date startTime, Date endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	
	
	
}
