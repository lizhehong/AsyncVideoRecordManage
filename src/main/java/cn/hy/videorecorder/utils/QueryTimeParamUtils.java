package cn.hy.videorecorder.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.server.StreamDownLoadServer;

public class QueryTimeParamUtils {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 文件转码
	 * @param queryTimeParam
	 * @return ffmpeg cmd String
	 */
	public static String transcodingWithGenernatorCmd(QueryTimeParam queryTimeParam){
		try {
			File file = queryTimeParam.getFile();
			String fileName =  file.getName();
			
			fileName = fileName.substring(0,fileName.lastIndexOf("."))+".flv";
			//TODO 禁止输出减少 进程缓冲器不会溢出 同时限制CPU使用率
			//全速转码
			String command = "ffmpeg -y -i " +
			
							file.getAbsolutePath() +
							
							" -c:v libx264 -b:v 128k -r 15 -threads 2 -loglevel quiet -an -f flv "+
							//" -c:v copy -loglevel quiet -an -f flv " +
							
							file.getParentFile().getAbsolutePath()+
							
							"\\"+fileName;

			queryTimeParam.setFile(new File(file.getParentFile(),fileName));
			
			return command;
			
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
