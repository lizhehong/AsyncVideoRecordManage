package cn.hy.videorecorder.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;
import cn.hy.videorecorder.entity.type.TaskStep;

public interface TranscodingAndDownLoadTaskRespotity extends JpaRepository<TranscodingAndDownLoadTaskEntity, Long>{

	/**
	 * 找到正在等待转码的任务
	 * @return
	 */
	public Page<TranscodingAndDownLoadTaskEntity> findByTaskStep(TaskStep taskStep,Pageable page);
	/**
	 * 找到对应视频下的 转码和下载 任务
	 * @param monitorId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<TranscodingAndDownLoadTaskEntity> findByMonitorEntityIdAndTimeStartTimeGreaterThanEqualAndTimeEndTimeLessThanEqual(String monitorId,Date startTime,Date endTime);
}
