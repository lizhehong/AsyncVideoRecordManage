package cn.hy.videorecorder.bo;

import java.util.Date;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class TimeZone {

	private Date startTime;
	
	private Date endTime;

	public TimeZone(Date startTime, Date endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public TimeZone() {
		super();
	}
	
	
	
	
	
}
