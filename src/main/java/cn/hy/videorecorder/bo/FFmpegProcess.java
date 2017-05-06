package cn.hy.videorecorder.bo;

import lombok.Data;

@Data
public class FFmpegProcess {

	private Process process;
	
	private OutHandler outputGobbler;
	
	private OutHandler errorGobbler;

	public FFmpegProcess(Process process, OutHandler outputGobbler, OutHandler errorGobbler) {
		super();
		this.process = process;
		this.outputGobbler = outputGobbler;
		this.errorGobbler = errorGobbler;
	}
	
	
}

