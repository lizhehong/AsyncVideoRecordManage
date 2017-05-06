package cn.hy.videorecorder.form.pullendpoint;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
public class UpdateOnePullendpointForm {

	private String id;
	
	private NetIndentity clientNet;
	
	private String onPublicCallBackUrl;
	
	private String onPublishDoneCallBackUrl;
}
