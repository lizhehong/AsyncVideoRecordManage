package cn.hy.videorecorder.resp;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.resp.type.MonitorRespMessage;
import lombok.Data;

@Data
public class MonitorResp {

	private MonitorEntity content;
	
	private MonitorRespMessage message;

	public MonitorResp(MonitorEntity content, MonitorRespMessage message) {
		super();
		this.content = content;
		this.message = message;
	}
	
	
}
