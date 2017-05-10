package cn.hy.videorecorder.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.server.StreamDownLoadServer;

public class QueryTimeParamUtils {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static Logger logger = LoggerFactory.getLogger(QueryTimeParamUtils.class);
	
	public static String transcodingWithGenernatorCmd(QueryTimeParam queryTimeParam,int secStep,String offsetDate){
		try {
			//TODO 禁止输出减少 进程缓冲器不会溢出 同时限制CPU使用率
			//全速转码
			String command = "ffmpeg -y "
					
							+ " -ss "+ offsetDate
							
							+ " -t "+ secStep +
			
							" -i " + queryTimeParam.getDownLoadFile().getAbsolutePath() +
							
							" -c:v libx264 -b:v 128k -r 15 -threads 2 -loglevel quiet -an -f mp4 "+
							
							queryTimeParam.getTranscodedFile().getAbsolutePath();
			
			logger.info("当前的视频转换命令：{}",command);
			
			return command;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 文件转码
	 * @param queryTimeParam
	 * @return ffmpeg cmd String
	 */
	public static String transcodingWithGenernatorCmd(QueryTimeParam queryTimeParam){
		try {
			//TODO 禁止输出减少 进程缓冲器不会溢出 同时限制CPU使用率
			//全速转码
			String command = "ffmpeg -y -i " +
			
							queryTimeParam.getDownLoadFile().getAbsolutePath() +
							
							" -c:v libx264 -b:v 128k -r 15 -threads 2 -loglevel quiet -an -f mp4 "+
							//" -c:v copy -loglevel quiet -an -f flv " +
							
							queryTimeParam.getTranscodedFile().getAbsolutePath();
			
			logger.info("当前的视频转换命令：{}",command);
			
			return command;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void downLoadWithSplitTime(List<QueryTimeParam> queryTimeParams,StreamDownLoadServer streamDownLoadServer){
		try {
			for(QueryTimeParam queryTimeParam:queryTimeParams){
				
				if(queryTimeParam.getVodReqState().equals(VodRequestState.已经请求)){
					File parentFile = queryTimeParam.getDownLoadFile().getParentFile();
					if(!parentFile.exists())
						parentFile.mkdirs();
					//保证文件存在的情况下 才进行下载任务
					streamDownLoadServer.downLoadByTimeZone(queryTimeParam);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 固化内存信息
	 * @param parentFile
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static void storgeInfo(File parentFile,Object MemInfo) throws Exception {
		//每次下载后都去更新索引文件
		String jsonItem = objectMapper.writeValueAsString(MemInfo);
		File file = new File(parentFile,"index.json");
		OutputStream os = new FileOutputStream(file);
		IOUtils.write(jsonItem, os );
		IOUtils.closeQuietly(os);
	}
}
