package cn.hy.videorecorder.ctr.rest.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hy.videorecorder.entity.TranscodClientEntity;
import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;
import cn.hy.videorecorder.entity.type.TaskStep;
import cn.hy.videorecorder.form.transcodClient.TranscodedCallBackForm;
import cn.hy.videorecorder.repository.TranscodingAndDownLoadTaskRespotity;
import cn.hy.videorecorder.repository.TranscodingClientRepsoitory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class TranscodingAndDownLoadTaskCtr {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TranscodingAndDownLoadTaskRespotity transcodingAndDownLoadTaskRespotity;
	
	@Autowired
	private TranscodingClientRepsoitory transcodingClientRepsoitory;
	
	@Value("${download.path}")
	private String downLoadPath;
	
	@ApiOperation(value = "转码端任务完成回调", notes = "主要用于客户端程序主动请求更改状态")
	@RequestMapping(value = "transcodTask/callBack", method = RequestMethod.POST)
	public void transcodedCallBack(
			@ApiParam(name = "transcodedCallBackForm", required = true, value = "更新 表单参数") 
			@ModelAttribute
			TranscodedCallBackForm transcodedCallBackForm,
			@ApiParam(name = "outFile", required = true, value = "上传的文件")
			@RequestParam("outFile")
			MultipartFile outFile)
			throws Exception {
		logger.info("转码端任务完成回调:{},类型：{},文件名：{}",transcodedCallBackForm,outFile.getContentType(),outFile.getOriginalFilename());
		TranscodingAndDownLoadTaskEntity task = transcodingAndDownLoadTaskRespotity.findOne(transcodedCallBackForm.getTaskId());
	
		if(task == null) 
			return;
		
		TranscodClientEntity client = transcodingClientRepsoitory.findOne(transcodedCallBackForm.getClientId());
		if(client == null)
			return;
		
		//任务状态转换
		task.setTaskStep(TaskStep.transcoded);
		//空目录检测
		File file = new File(downLoadPath,task.getMonitorEntity().getId());
		if(!file.exists())
			file.mkdirs();
		String fileName = UUID.randomUUID().toString()+".mp4";
		task.setFile(file.getName()+"/"+fileName);
		
		
		//具体文件处理
		InputStream in = outFile.getInputStream();
		FileOutputStream out = new FileOutputStream(new File(file,fileName));
		
		IOUtils.copy(in,  out);
		
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
		
		transcodingAndDownLoadTaskRespotity.save(task);
		//客户端状态转换
		
		client.setFree(true);
		transcodingClientRepsoitory.save(client);
	}
	
}
