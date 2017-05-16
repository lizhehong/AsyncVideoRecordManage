package cn.hy.videorecorder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;

public interface TranscodingAndDownLoadTaskRespotity extends JpaRepository<TranscodingAndDownLoadTaskEntity, Long>{

	/**
	 * 找到正在等待转码的任务
	 * @return
	 */
	public Page<TranscodingAndDownLoadTaskEntity> findByTaskStepWaiting(Pageable page);
}
