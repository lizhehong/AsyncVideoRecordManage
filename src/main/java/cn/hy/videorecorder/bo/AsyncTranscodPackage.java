package cn.hy.videorecorder.bo;

import cn.hy.videorecorder.entity.TranscodClientEntity;
import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;
import lombok.Data;

@Data
public class AsyncTranscodPackage {

	private TranscodingAndDownLoadTaskEntity task;
	
	private TranscodClientEntity client;

	public AsyncTranscodPackage(TranscodingAndDownLoadTaskEntity task, TranscodClientEntity client) {
		super();
		this.task = task;
		this.client = client;
	}
	
	
}
