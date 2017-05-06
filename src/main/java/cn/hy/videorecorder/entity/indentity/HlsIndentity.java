package cn.hy.videorecorder.entity.indentity;

import lombok.Data;

@Data
public class HlsIndentity {
	
	private String streamTargetName;
	
	private String stuffix;
	
	private String targetName;
	
	private String hlsUrl;
	
	private String videoUrl;

	public HlsIndentity(String stuffix, String targetName, String hlsUrl, String videoUrl) {
		super();
		this.stuffix = stuffix;
		this.targetName = targetName;
		this.hlsUrl = hlsUrl;
		this.videoUrl = videoUrl;
	}

	public HlsIndentity() {
		super();
	}
	
	
}
