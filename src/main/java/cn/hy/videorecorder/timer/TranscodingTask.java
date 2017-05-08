package cn.hy.videorecorder.timer;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;

public class TranscodingTask implements  Runnable {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String cmd ;
	/**
	 * 当前转换任务的文件对应的文件夹
	 */
	private QueryTimeParam queryTimeParam;
	/**
	 * 当前点播任务的参数
	 */
	private VodParam vodParam;
	
	private boolean delOrignFile;
	
	public TranscodingTask(String cmd,QueryTimeParam queryTimeParam,VodParam vodParam,boolean delOrignFile) {
		super();
		this.cmd = cmd;
		this.queryTimeParam = queryTimeParam;
		this.vodParam = vodParam;
		this.delOrignFile = delOrignFile;
	}
	
	public QueryTimeParam getQueryTimeParam() {
		return queryTimeParam;
	}


	public VodParam getVodParam() {
		return vodParam;
	}

	@Override
	public void run(){
		try {
			Process process = Runtime.getRuntime().exec(cmd);//运行转码程序
			process.waitFor();//等待转码完成
			process.destroyForcibly();//销毁转码进程
			
			//更新视频状态
			queryTimeParam.setDownLoadState(DownLoadState.已经下载);
			
			String orignFilePath = "";
			//删除原来文件
			if(delOrignFile){
				//拿到转码后的文件
				File transcondedFile = queryTimeParam.getFile();
				//删除原文件
				String fileName = transcondedFile.getName();
				fileName =(fileName.substring(0, fileName.indexOf(".")))+".mp4";
				File orignFile = new File(transcondedFile.getParentFile(),fileName);
				orignFilePath = orignFile.getAbsolutePath();
				orignFile.delete();
			}
			
			//固化内存信息
			QueryTimeParamUtils.storgeInfo(queryTimeParam.getFile().getParentFile(), vodParam);
			
			logger.info("转码成功：{},删除源文件：{}",queryTimeParam,orignFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "TranscodingTask [queryTimeParam=" + queryTimeParam + "]";
	}

}
