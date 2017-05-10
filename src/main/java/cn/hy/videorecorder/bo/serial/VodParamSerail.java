package cn.hy.videorecorder.bo.serial;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;


public class VodParamSerail extends JsonSerializer<VodParam>{
	
	@Override
	public void serialize(VodParam vodParam, JsonGenerator gen, SerializerProvider sp)
			throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeNumberField("channelNum", vodParam.getMonitorEntity().getChannelNum());
		QueryTimeParam queryTimeParam = vodParam.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		
		gen.writeStringField("startTime",sdf.format(queryTimeParam.getStartTime()));
		gen.writeStringField("endTime",sdf.format(queryTimeParam.getEndTime()));
		
		gen.writeStringField("downLoadState",queryTimeParam.getDownLoadState().name());
		gen.writeStringField("vodReqState",queryTimeParam.getVodReqState().name());
		
		gen.writeStringField("parentPathName", queryTimeParam.getDownLoadFile().getName());
		gen.writeNumberField("splitSecStep", vodParam.getSplitSecStep()==null?0:vodParam.getSplitSecStep());
		gen.writeStringField("vrUserType",  vodParam.getMonitorEntity().getVrUserType().name());
		
		gen.writeArrayFieldStart("videos");
			for(QueryTimeParam queryTimeParamItem :vodParam.getQueryTimeParams()){
				gen.writeStartObject();
				gen.writeStringField("fileName",queryTimeParamItem.getTranscodedFile().getName());
				gen.writeStringField("sourceName",queryTimeParamItem.getDownLoadFile().getName());
				gen.writeStringField("downLoadState", queryTimeParamItem.getDownLoadState().name());
				gen.writeStringField("startTime",sdf.format(queryTimeParamItem.getStartTime()));
				gen.writeStringField("endTime",sdf.format(queryTimeParamItem.getEndTime()));
				
				gen.writeStringField("vodReqState", queryTimeParamItem.getVodReqState().name());
				gen.writeEndObject();
			}
		gen.writeEndArray();
		
		gen.writeEndObject();
	}

}
