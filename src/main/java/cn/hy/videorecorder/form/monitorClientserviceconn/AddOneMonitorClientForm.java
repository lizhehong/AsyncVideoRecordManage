package cn.hy.videorecorder.form.monitorClientserviceconn;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
public class AddOneMonitorClientForm {

	private NetIndentity net;
	
	private String onPublishCallBackAction;
	
	private String onPublishDoneCallBackAction;
}
