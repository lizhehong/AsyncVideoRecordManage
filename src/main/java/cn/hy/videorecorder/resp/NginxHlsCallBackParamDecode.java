package cn.hy.videorecorder.resp;

import java.util.Date;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
public class NginxHlsCallBackParamDecode {

	private NetIndentity pullEndPointNet;
	
	private NetIndentity monitorNet;
	
	private Integer channelNum;
	
	private Date startTime;
	
	private Date endTime;
}
