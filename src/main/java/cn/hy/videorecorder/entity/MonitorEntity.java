package cn.hy.videorecorder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.entity.indentity.VideoRecordIndentity;
import cn.hy.videorecorder.entity.type.CallType;
import cn.hy.videorecorder.entity.type.RtspStreamType;
import cn.hy.videorecorder.entity.type.VideoRecordUserType;
import lombok.Data;

@Entity
@Data
@Table(name="async_vrm_monitor")
public class MonitorEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -74895628134535031L;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
			@Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	private String id;
		
	@CreationTimestamp
	private Date publishTime;
	
	@UpdateTimestamp
	private Date updateTime;
	
	private VideoRecordIndentity vrUser;

	private NetIndentity streamNet;
	
	private Integer vrPort;

	private Integer channelNum;
	
	private Boolean pushState;
	
	private VideoRecordUserType vrUserType;
	
	private Date heartTime;
	
	private CallType streamState;
	
	private RtspStreamType rtspStreamType;
	/**
	 * 点播每秒视频大小
	 */
	@Column(name="vodsize_by_per_sec")
	private Long VodSizeByperSec;
}
