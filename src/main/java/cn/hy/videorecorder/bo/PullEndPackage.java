package cn.hy.videorecorder.bo;

import lombok.Data;

@Data
public class PullEndPackage {

	private String cmd;

	public PullEndPackage(String cmd) {
		super();
		this.cmd = cmd;
	}
	
	
	
}




