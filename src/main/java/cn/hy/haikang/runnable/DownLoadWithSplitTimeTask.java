package cn.hy.haikang.runnable;

import cn.hy.haikang.server.impl.HaiKangServerImpl;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

public class DownLoadWithSplitTimeTask implements Runnable {

	
	private HaiKangServerImpl haiKangServerImpl;
	
	private VodParam vodParam;
	
	public DownLoadWithSplitTimeTask(VodParam vodParam,HaiKangServerImpl haiKangServerImpl) {
		super();
		this.haiKangServerImpl = haiKangServerImpl;
		this.vodParam = vodParam;
	}

	@Override
	public void run() {
		QueryTimeParamUtils.downLoadWithSplitTime(vodParam.getQueryTimeParams(),haiKangServerImpl);
	}

}
