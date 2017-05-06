package cn.hy.videorecorder.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.type.VideoRecordUserType;

public interface MonitorRepository extends JpaRepository<MonitorEntity,String>{

	@Query("FROM MonitorEntity WHERE streamNet.ip =?1 AND channelNum=?2")
	public MonitorEntity findByIpAndChannelNum(String ip,Integer channelNum);

	@Modifying
	@Transactional 
	@Query("UPDATE MonitorEntity SET pushState = ?1 WHERE id = ?2")
	public void updatePushStateById(boolean pushState, String monitorId);

	public List<MonitorEntity> findByPushState(Boolean pushState);

	@Query("FROM MonitorEntity WHERE streamNet.ip =?1 AND channelNum=?2 AND vrUserType=?3")
	public MonitorEntity findOneByIpAndChannelNumAndVrUserType(String ip, Integer channelNum,VideoRecordUserType userType);
	
}
