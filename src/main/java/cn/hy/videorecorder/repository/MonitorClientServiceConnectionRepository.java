package cn.hy.videorecorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.hy.videorecorder.entity.MonitorClientServiceConnectionEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;

public interface MonitorClientServiceConnectionRepository extends JpaRepository<MonitorClientServiceConnectionEntity,String> {

	MonitorClientServiceConnectionEntity findByNet(NetIndentity net);

}
