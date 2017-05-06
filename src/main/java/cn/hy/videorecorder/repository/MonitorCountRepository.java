package cn.hy.videorecorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.hy.videorecorder.entity.MonitorCountEntity;

public interface MonitorCountRepository extends JpaRepository<MonitorCountEntity,Long>{

	@Modifying
	@Transactional 
	@Query("UPDATE MonitorCountEntity SET onlineNum = ?2 WHERE id = ?1")	
	public int updateOnlineNum(Long id, Integer onlineNum);

	@Query("FROM MonitorCountEntity WHERE monitor.id=?1 AND monitorClientServiceConnection.id = ?2")
	public MonitorCountEntity findByMidAndMcId(String monitorId, String monitorClientServiceConnectionId);

}
