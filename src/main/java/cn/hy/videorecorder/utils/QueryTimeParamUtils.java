package cn.hy.videorecorder.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.FFmpegProcess;
import cn.hy.videorecorder.bo.OutHandler;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.server.StreamDownLoadServer;

public class QueryTimeParamUtils {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 文件转码
	 * @param queryTimeParam
	 * @return
	 */
	public static FFmpegProcess transcoding(QueryTimeParam queryTimeParam){
		try {
			File file = queryTimeParam.getFile();
			String fileName =  file.getName();
			
			fileName = fileName.substring(0,fileName.lastIndexOf("."))+".flv";
			//TODO 日志等级 调节 输出内容 则可以避免创建接收输出减少系统压力
			//全速转码
			String command = "ffmpeg -y -i " +
			
							file.getAbsolutePath() +
							
							" -c:v copy -an -f flv " +
							
							file.getParentFile().getAbsolutePath()+
							
							"\\"+fileName;

			queryTimeParam.setFile(new File(file.getParentFile(),fileName));
			
			Process process = Runtime.getRuntime().exec(command);
			
			OutHandler outputGobbler = new OutHandler(process.getInputStream(), "Info");  
			OutHandler errorGobbler = new OutHandler(process.getErrorStream(), "out");  
			
			
			errorGobbler.start();  
			outputGobbler.start();
			
			return new FFmpegProcess(process, outputGobbler, errorGobbler);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void downLoadWithSplitTime(List<QueryTimeParam> queryTimeParams,StreamDownLoadServer streamDownLoadServer){
		try {
			for(QueryTimeParam queryTimeParam:queryTimeParams){
				
				File parentFile = queryTimeParam.getFile().getParentFile();
				if(!parentFile.exists())
					parentFile.mkdirs();
				//保证文件存在的情况下 才进行下载任务
				streamDownLoadServer.downLoadByTimeZone(queryTimeParam);
				
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
