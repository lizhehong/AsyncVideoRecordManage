package cn.hy.videorecorder.form.nginxhls;

import cn.hy.videorecorder.entity.type.CallType;
import lombok.Data;

@Data
public class NginxHlsCallBackParam {

	private String app;
	
	private String flashver;
	
	private String swfurl;
	
	private String tcurl;
	
	private String pageurl;
	
	private String addr;
	
	private String clientid;
	
	private CallType call;
	
	private String type;
	
	private String name;
	
	private String timestamp;
	
	private String time;
	
}
