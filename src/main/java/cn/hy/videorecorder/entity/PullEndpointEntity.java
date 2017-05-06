package cn.hy.videorecorder.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

/**
 * 记录 推流客户端的身份
 * @author Administrator
 *
 */
@Data
@Entity
@Table(name="async_vrm_pullEndpoint")
public class PullEndpointEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -224275327348817950L;

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
	
	/**
	 * 当前心跳时间
	 * 由nginx回调机制修改
	 */
	private Date heartbeatMechanismTimeCur;
	
	/**
	 * 上一次心跳时间
	 */
	private Date heartbeatMechanismTimeLast;
}
