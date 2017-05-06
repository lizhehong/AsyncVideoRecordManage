package cn.hy.videorecorder.server.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.entity.indentity.UserIndentity;
import cn.hy.videorecorder.entity.type.RtspStreamType;
import cn.hy.videorecorder.server.MonitorServer;

@Service("monitorServer")
public class MonitorServerImpl implements MonitorServer{

	public final String prefix = "ffmpeg";
	
	public final String inputParam = "-rtsp_transport tcp -re -i";
	
	public final String outParam = "-an -c:v copy -f flv";
	
	public final String commRtspUrl = "rtsp://<user>:<password>@<ip>:<port>";
	
	public final String daHuaLive = "/cam/realmonitor?channel=<channelNum>&subtype=<streamType>";
	
	public final String haiKangLive = "/Streaming/Channels/<channelNum>0<streamType>";
	
	public final String haikangIpSubCam = "/mpeg4/ch1/sub/av_stream";
	
	public final String rtmpPath = "rtmp://192.168.1.192:1935/myapp/<targetName>";
	
	public String gernatorFFmpegCmdByMonitorEntity(MonitorEntity monitorEntity){
		String rtspUrl = createLiveAddress(monitorEntity);
		if(StringUtils.isEmpty(rtspUrl))
			return "";
		return String.format(
				"%s %s %s %s %s", 
				prefix,
				inputParam ,
				rtspUrl,
				outParam,
				rtmpPath.replaceAll("<targetName>", monitorEntity.getId())
		);
	}
	
	public String createLiveAddress(MonitorEntity monitorEntity){
		UserIndentity user = monitorEntity.getVrUser();
		NetIndentity net = monitorEntity.getStreamNet();
		String rtspUrl =  commRtspUrl.replaceAll("<user>", user.getName())
				.replaceAll("<password>", user.getPassword())
				.replaceAll("<ip>", net.getIp())
				.replaceAll("<port>", net.getPort()+"");
		String url = "";
		switch (monitorEntity.getVrUserType()) {
			case 海康:
				url = rtspUrl+haiKangLive.replaceAll("<channelNum>", monitorEntity.getChannelNum() + "");
				switch(monitorEntity.getRtspStreamType()) {
					case 主码流: return url.replaceAll("<streamType>","1");	
					case 子码流: return url.replaceAll("<streamType>","2");
					default :return "";
				}
						
			case 大华:
					return rtspUrl+daHuaLive.replaceAll("<channelNum>", monitorEntity.getChannelNum() + "")
					.replaceAll("<streamType>",monitorEntity.getRtspStreamType().equals(RtspStreamType.子码流)?"0":"1");
			case 三星:
				return "";
			case ip摄像头_海康:
				url = rtspUrl;
				switch(monitorEntity.getRtspStreamType()) {
					case 主码流:return url;
					case 子码流:return url+haikangIpSubCam;
					default :return "";
				}
			default:
				return "";
		}
		
		
	}
}
