package cn.hy.videorecorder.bo;

import cn.hy.videorecorder.entity.indentity.NetIndentity;
import lombok.Data;

@Data
public class StreamNetBo {

	private NetIndentity streamNet;

	private Integer channelNum;
}
