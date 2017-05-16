package cn.hy.videorecorder.form.transcodClient;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
public class UpdateOneTranscodClientForm {

	private String id;
	
	private NetIndentity clientNet;
	
}
