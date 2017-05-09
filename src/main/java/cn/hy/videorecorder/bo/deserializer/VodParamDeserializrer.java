package cn.hy.videorecorder.bo.deserializer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.repository.MonitorRepository;

@Component("vodParamDeserializrer")
public class VodParamDeserializrer extends JsonDeserializer<VodParam>  {

	@Autowired
	private MonitorRepository monitorRepository;
	
	@Value("${download.path}")
	private String downLoadPath;
	@Override
	public VodParam deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JsonNode node = jp.getCodec().readTree(jp);  
			VodParam vodParam = new VodParam();
			String monitorId = node.get("parentPathName").textValue();
			vodParam.setMonitorEntity(monitorRepository.findOne(monitorId));
			vodParam.setSplitSecStep(node.get("splitSecStep").intValue());
			JsonNode videoNodes = node.get("videos");
			if(videoNodes.isArray()&&videoNodes.size() > 0){
				for(JsonNode videoNoe:videoNodes){
					QueryTimeParam time = new QueryTimeParam();
					time.setStartTime(sdf.parse(videoNoe.get("startTime").textValue()));
					time.setStartTime(sdf.parse(videoNoe.get("EndTime").textValue()));
					time.setVodReqState(VodRequestState.valueOf(videoNoe.get("vodReqState").textValue()));
					time.setDownLoadState(DownLoadState.valueOf(videoNoe.get("downLoadState").textValue()));
					time.setFile(new File(downLoadPath+monitorId+"/"+videoNoe.get("fileName").asText()));
					vodParam.getQueryTimeParams().add(time);
				}
			}
			//vodParam.setQueryTimeParams(queryTimeParams);
			
			QueryTimeParam time = new QueryTimeParam();
			time.setStartTime(sdf.parse(node.get("startTime").textValue()));
			time.setStartTime(sdf.parse(node.get("EndTime").textValue()));
			time.setVodReqState(VodRequestState.valueOf(node.get("vodReqState").textValue()));
			time.setDownLoadState(DownLoadState.valueOf(node.get("downLoadState").textValue()));
			time.setFile(new File(downLoadPath+monitorId));
			
			vodParam.setTime(time );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}

}
