package cn.hy.videorecorder.bo;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.hy.videorecorder.bo.serial.VodParamSerail;
import cn.hy.videorecorder.entity.MonitorEntity;
import lombok.Data;

@Data
@JsonSerialize(using=VodParamSerail.class)
public class VodParam {
	
	
	/**
	 * 任务实际执行的时间
	 */
	private QueryTimeParam time;
	
	/**
	 * 依据QueryTimeParam 分后后的 时间容器
	 */
	private List<QueryTimeParam> queryTimeParams = new ArrayList<>();
	/**
	 * 切片的时长(秒)
	 */
	private Integer splitSecStep;
	
	private MonitorEntity monitorEntity;
	

}
