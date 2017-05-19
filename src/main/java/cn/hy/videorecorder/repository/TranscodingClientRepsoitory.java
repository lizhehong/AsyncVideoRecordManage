package cn.hy.videorecorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.hy.videorecorder.entity.TranscodClientEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;

public interface TranscodingClientRepsoitory extends JpaRepository<TranscodClientEntity, String>{

	/**
	 * 找到单个空闲的转码服务器
	 * @return
	 */
	public TranscodClientEntity findFirstByFreeIsTrueAndOnline(boolean online);

	/**
	 * 找到多个空闲的转码服务器
	 * @return
	 */
	public List<TranscodClientEntity> findByFreeIsTrueAndOnline(boolean online);
	
	@Modifying
	@Transactional 
	@Query("UPDATE TranscodClientEntity SET online=?2,free=?2 WHERE id=?1")
	public void setOnline(String id,Boolean online);

	@Modifying
	@Transactional 
	@Query("UPDATE TranscodClientEntity SET online=?1,free=?1")
	public void setOnline(Boolean online);
	
	
	public TranscodClientEntity findFirstByClientNet(NetIndentity net);
}
