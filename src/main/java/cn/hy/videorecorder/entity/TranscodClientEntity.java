package cn.hy.videorecorder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
@Entity
@Table(name="async_vrm_transcod_client")
public class TranscodClientEntity  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7593798640321996568L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
			@Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	private String id;
	
	@CreationTimestamp   
	private Date publishTime;
	
	@UpdateTimestamp   
	private Date updateTime;
	
	@Embedded
	private NetIndentity clientNet;
	
	private Boolean free;
	/**
	 * 同时转码池容量
	 */
	@Column(nullable=false)
	private Integer transcodPoolSize;
	@Column(nullable=false)
	private Integer nowDownLoadSize;
	/**
	 * 下载线程池
	 */
	@Column(nullable=false)
	private Integer downLoadPoolSize;
	
	/**
	 * 在线状态检测
	 */
	private Boolean online;
}
