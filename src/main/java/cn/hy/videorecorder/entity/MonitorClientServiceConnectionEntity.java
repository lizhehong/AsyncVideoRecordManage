package cn.hy.videorecorder.entity;

import java.io.Serializable;
import java.util.Date;

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
@Table(name="async_vrm_serviceconn")
public class MonitorClientServiceConnectionEntity implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8121415278258326047L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
			@Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy") })
	private String id;
		
	private String name;
	
	@CreationTimestamp
	private Date publishTime;
	
	@UpdateTimestamp
	private Date updateTime;
	
	private NetIndentity net;
	
	private String onPublishDoneCallBackAction;
	
	private String onPublishCallBackAction;
}
