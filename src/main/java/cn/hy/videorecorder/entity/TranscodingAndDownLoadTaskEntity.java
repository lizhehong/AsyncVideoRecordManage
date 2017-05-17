package cn.hy.videorecorder.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import cn.hy.videorecorder.bo.TimeZone;
import cn.hy.videorecorder.entity.type.TaskStep;
import lombok.Data;

/**
 * 任务列表
 * @author Administrator
 *
 */
@Data
@Entity
@Table(name="async_vrm_transcod_download_task")
public class TranscodingAndDownLoadTaskEntity {
	
	/**
	 * 设备id
	 */
	@Id
	@GeneratedValue
	private Long id;
	/**
	 * 对应的视频信息
	 */
	@OneToOne
	private MonitorEntity monitorEntity;
	
	/**
	 * 任务步骤
	 */
	private TaskStep taskStep;
	
	private TimeZone time;

	@OneToOne(cascade=CascadeType.DETACH)
	private TranscodClientEntity client;
	/**
	 * 记录文件的存放相对地址
	 */
	private String file;
}
