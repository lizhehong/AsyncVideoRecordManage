package cn.hy.videorecorder.entity.indentity;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class NetIndentity {
	
	private String ip;
	
	private Integer port;

	public NetIndentity() {
		super();
	}

	public NetIndentity(String ip, Integer port) {
		super();
		this.ip = ip;
		this.port = port;
	}

}
