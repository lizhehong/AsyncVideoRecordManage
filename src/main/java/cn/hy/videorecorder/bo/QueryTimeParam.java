package cn.hy.videorecorder.bo;

import java.io.File;
import java.util.Date;

import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.entity.type.VodRequestState;
import lombok.Data;

@Data
public class QueryTimeParam {

	private Date StartTime;
	
	private Date endTime;
	
	private File file;
	
	private DownLoadState downLoadState;
	
	private VodRequestState vodReqState;
	
	public QueryTimeParam(Date startTime, Date endTime) {
		super();
		StartTime = startTime;
		this.endTime = endTime;
	}

	public QueryTimeParam() {
		super();
	}
	
}
