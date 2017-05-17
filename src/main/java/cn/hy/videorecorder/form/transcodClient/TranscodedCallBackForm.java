package cn.hy.videorecorder.form.transcodClient;

import lombok.Data;

@Data
public class TranscodedCallBackForm {
	
	private String clientId;
	
	//转换任务的id
	private Long taskId;
}
