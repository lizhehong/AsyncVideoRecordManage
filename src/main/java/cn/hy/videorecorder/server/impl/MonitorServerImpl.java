package cn.hy.videorecorder.server.impl;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.haikang.type.DownLoadState;
import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.TimeZone;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.comparator.QueryTimeParamComparator;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.TranscodingAndDownLoadTaskEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.entity.indentity.UserIndentity;
import cn.hy.videorecorder.entity.type.RtspStreamType;
import cn.hy.videorecorder.entity.type.SortDirection;
import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.form.monitor.VodMonitorForm;
import cn.hy.videorecorder.repository.MonitorRepository;
import cn.hy.videorecorder.repository.TranscodingAndDownLoadTaskRespotity;
import cn.hy.videorecorder.server.MonitorServer;
import cn.hy.videorecorder.server.SplitTimeDownLoadService;
import cn.hy.videorecorder.server.TranscodingServer;
import cn.hy.videorecorder.utils.QueryTimeParamUtils;
import cn.hy.videorecorder.utils.TimeUtils;

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
	
	@Autowired
	private MonitorRepository monitorInfoRepository;
	
	
	@Autowired 
	private TranscodingAndDownLoadTaskRespotity transcodingAndDownLoadTaskRespotity;
	
	@Value("${download.path}")
	private String downLoadPath;
	
	@Value("${download.haikang.downloadTimeSplitSec}")
	private Integer haikangDownloadTimeSplitSec;
	
	@Autowired @Qualifier("splitTimeDownLoadService")
	private SplitTimeDownLoadService splitTimeDownLoadService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired @Qualifier("transcodingByDistributedProcessServer")
	private TranscodingServer<TranscodingAndDownLoadTaskEntity> transcodingByDistributedProcessServer;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${cache.videoList.cacheMaxCount}")
	private Integer cacheMaxCount;
	
	@Value("${download.path}")
	private String downloadPath;
	
	
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
	
	public VodParam  startDownLoadActionToVodByOldIndexFile(VodMonitorForm vodMonitorForm,File indexFile) throws Exception{
		
		if(indexFile != null && indexFile.isFile()){
			logger.info("进入扩充点播列表");
			//拿到缓存文件
			VodParam vodParam  = objectMapper.readValue(indexFile, VodParam.class);
			
			QueryTimeParam queryTimeParam = new QueryTimeParam(vodMonitorForm.getStartTime(), vodMonitorForm.getEndTime());
			//切片用户点播视频 依据系统步长
			List<QueryTimeParam> newQueryTimeParamList = TimeUtils.fillFullMinAndSplitTime(queryTimeParam , vodParam.getSplitSecStep(),cacheMaxCount);
			//扩展该视频下的播放列表
			List<QueryTimeParam> oldQueryTimeParamList =vodParam.getQueryTimeParams();
			//迭代器用于循环中操作
			Iterator<QueryTimeParam> newQueryTimeParamIterator = newQueryTimeParamList.iterator();
			
			int cacheCount = 0;
			while(newQueryTimeParamIterator.hasNext()){
				QueryTimeParam newQtp = newQueryTimeParamIterator.next();
				boolean findOldQtp = false;
				
				for(QueryTimeParam oldQtp:oldQueryTimeParamList){
					if(
							oldQtp.getStartTime().getTime() == newQtp.getStartTime().getTime()
							&&
							oldQtp.getEndTime().getTime() == newQtp.getEndTime().getTime()
						){
						
						//申请视频缓存
						cacheCount = splitTimeDownLoadService.applyCacheReVideo(cacheCount, cacheMaxCount, vodMonitorForm.getStartTime(), oldQtp);
						findOldQtp = true;
						
						break;
					}
				}
				
				if(findOldQtp){
					newQueryTimeParamIterator.remove();
				}else{
					File file = vodParam.getTime().getDownLoadFile();

					newQtp.setDownLoadFile(new File(file.getAbsolutePath() + "\\" + "video-" + UUID.randomUUID().toString() + ".mp4"));
					
					newQtp.setTranscodedFile(new File(file.getAbsolutePath() + "\\" + "video-" + UUID.randomUUID().toString() + ".mp4"));
					
					newQtp.setDownLoadState(DownLoadState.未下载);
					//申请视频缓存
					cacheCount = splitTimeDownLoadService.applyCacheReVideo(cacheCount, cacheMaxCount, vodMonitorForm.getStartTime(), newQtp);
				}
			}
			//不存在索引文件的视频 加入索引文件
			oldQueryTimeParamList.addAll(newQueryTimeParamList);
			Collections.sort(oldQueryTimeParamList,new QueryTimeParamComparator(SortDirection.ASC));
			//设置文件索引的总时间
			QueryTimeParam time = vodParam.getTime();
			
			if(vodMonitorForm.getStartTime().getTime() < time.getStartTime().getTime()){
				time.setStartTime(vodMonitorForm.getStartTime());
			}
			
			if(vodMonitorForm.getEndTime().getTime() > time.getEndTime().getTime()){
				time.setEndTime(vodMonitorForm.getEndTime());
			}
			
			//异步执行开始任务
			splitTimeDownLoadService.startTask(vodParam);
			//固化内存信息
			QueryTimeParamUtils.storgeInfo(vodParam.getTime().getDownLoadFile(), vodParam);
			
			
			return vodParam;
		}else
			return null;
	}
	
	public VodParam  startDownLoadActionToVodByNewIndexFile(VodMonitorForm vodMonitorForm) throws Exception{
		
		MonitorEntity monitorEntity = monitorInfoRepository.findOne(vodMonitorForm.getMonitorId());
		if( monitorEntity !=null ){
			logger.info("进入点播列表未存在");
			VodParam vodParam = new VodParam();
			
			QueryTimeParam queryTimeParam = new QueryTimeParam();
			queryTimeParam.setDownLoadState(DownLoadState.已经下载);
			queryTimeParam.setVodReqState(VodRequestState.已经请求);
			queryTimeParam.setEndTime(vodMonitorForm.getEndTime());
			queryTimeParam.setStartTime(vodMonitorForm.getStartTime());
			queryTimeParam.setDownLoadFile(new File(downLoadPath+monitorEntity.getId()));
			
			vodParam.setTime(queryTimeParam);
			vodParam.setMonitorEntity(monitorEntity);
			//切片时长
			vodParam.setSplitSecStep(haikangDownloadTimeSplitSec);
			//优先切片让前端可以知道
			splitTimeDownLoadService.createTimeSplitTask(vodParam);
			//异步执行开始任务
			splitTimeDownLoadService.startTask(vodParam);
			//固话内存信息
			QueryTimeParamUtils.storgeInfo(queryTimeParam.getDownLoadFile(), vodParam);
			
			return vodParam;
		}else{
			return null;
		}
	}
	/**
	 * 发布一个点播
	 * @param vodMonitorForm
	 * @return
	 * @throws Exception
	 */
	public synchronized VodParam  publishVodMonitor(VodMonitorForm vodMonitorForm) throws Exception{
		
		String monitorId = vodMonitorForm.getMonitorId();
		
		File indexFile = new File(downloadPath+monitorId+"/index.json");
		
		if(indexFile.exists()){
			VodParam param = startDownLoadActionToVodByOldIndexFile(vodMonitorForm,indexFile);
			if(param == null)
				return  startDownLoadActionToVodByNewIndexFile(vodMonitorForm);
			else 
				return param;
		}else
			return startDownLoadActionToVodByNewIndexFile(vodMonitorForm);
	}
	/**
	 * 分布式 发布点播任务
	 */
	public VodParam PublishVodMonitorByDistributedProcessing(VodMonitorForm vodMonitorForm) throws Exception{
		
		MonitorEntity monitorEntity = monitorInfoRepository.findOne(vodMonitorForm.getMonitorId());
		TranscodingAndDownLoadTaskEntity transcodingTask = new TranscodingAndDownLoadTaskEntity();
		transcodingTask.setMonitorEntity(monitorEntity);
		transcodingTask.setTime(new TimeZone(vodMonitorForm.getStartTime(), vodMonitorForm.getEndTime()));
		transcodingByDistributedProcessServer.addRunCmd(transcodingTask);
		return null;
	}
}
