package cn.hy.videorecorder.form.monitor;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.entity.indentity.VideoRecordIndentity;
import cn.hy.videorecorder.entity.type.CallType;
import cn.hy.videorecorder.entity.type.RtspStreamType;
import cn.hy.videorecorder.entity.type.VideoRecordUserType;
import lombok.Data;


@Data
public class UpdateOneMonitorForm {

	private String id;
	
	private VideoRecordIndentity vrUser;

	private NetIndentity streamNet;
	
	private Integer vrPort;

	private Integer channelNum;
	
	private Boolean pushState;
	
	private VideoRecordUserType vrUserType;
	
	private CallType streamState;
	
	private RtspStreamType rtspStreamType;

}
