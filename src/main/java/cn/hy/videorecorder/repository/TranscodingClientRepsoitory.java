package cn.hy.videorecorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.hy.videorecorder.entity.TranscodClientEntity;

public interface TranscodingClientRepsoitory extends JpaRepository<TranscodClientEntity, String>{

	/**
	 * 找到单个空闲的转码服务器
	 * @return
	 */
	public TranscodClientEntity findFirstByFreeIsTrue();

	/**
	 * 找到多个空闲的转码服务器
	 * @return
	 */
	public List<TranscodClientEntity> findByFreeIsTrue();
	
	@Query("UPDATE TranscodClientEntity SET online=true WHERE id=?1")
	public void setOnline(String id);
}
