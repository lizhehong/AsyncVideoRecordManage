package cn.hy.videorecorder.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="async_vrm_monitorcount")
@Data
public class MonitorCountEntity {

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	private MonitorEntity monitor;
	
	@OneToOne
	private MonitorClientServiceConnectionEntity monitorClientServiceConnection;
	
	private Integer onlineNum;
}
