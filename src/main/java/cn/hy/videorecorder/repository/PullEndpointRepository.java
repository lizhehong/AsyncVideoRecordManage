package cn.hy.videorecorder.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.hy.videorecorder.entity.PullEndpointEntity;

public interface PullEndpointRepository extends JpaRepository<PullEndpointEntity,String>{

	@Query("FROM PullEndpointEntity WHERE clientNet.ip = ?1")
	public PullEndpointEntity findByAddr(String addr);

	/**
	 * 依据ip地址 更改推流端心跳
	 * @param nowDate
	 * @param adrr
	 * @return
	 */
	@Modifying
	@Transactional 
	@Query("UPDATE PullEndpointEntity SET heartbeatMechanismTimeLast = heartbeatMechanismTimeCur, heartbeatMechanismTimeCur = ?1 WHERE clientNet.ip = ?2")
	public int updateHeartByAddr(Date nowDate,String adrr);

}
