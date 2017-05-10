package cn.hy.videorecorder.ctr.rest;


import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.comparator.QueryTimeParamComparator;
import cn.hy.videorecorder.comparator.TranscodingTaskComparator;
import cn.hy.videorecorder.entity.MonitorEntity;
import cn.hy.videorecorder.entity.type.CallType;
import cn.hy.videorecorder.entity.type.SortDirection;
import cn.hy.videorecorder.entity.type.VideoRecordUserType;
import cn.hy.videorecorder.entity.type.VodRequestState;
import cn.hy.videorecorder.form.monitor.AddOneMonitorForm;
import cn.hy.videorecorder.form.monitor.UpdateOneMonitorForm;
import cn.hy.videorecorder.form.monitor.VodMonitorForm;
import cn.hy.videorecorder.repository.MonitorRepository;
import cn.hy.videorecorder.resp.MonitorResp;
import cn.hy.videorecorder.resp.type.MonitorRespMessage;
import cn.hy.videorecorder.server.MonitorServer;
import cn.hy.videorecorder.server.PullEndpointClientServer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class MonitorCtr {

	@Value("${download.path}")
	private String downloadPath;
	
	@Autowired
	private MonitorRepository monitorInfoRepository;
	
	@Autowired @Qualifier("pullEndpointClientServer")
	private PullEndpointClientServer pullEndpointClientServer;
	
	@Autowired @Qualifier("monitorServer")
	private MonitorServer monitorServer;
	
	@PostMapping("monitor")
	@ApiOperation(value = "创建视频", notes = "")
	public MonitorEntity addOne(
			@ApiParam(name = "form", required = true, value = "添加 表单参数") @RequestBody AddOneMonitorForm form ,
			HttpServletRequest req)
			throws Exception {
		MonitorEntity entity = new MonitorEntity();
		entity.setPushState(false);
		entity.setStreamState(CallType.none);
		BeanUtils.copyProperties(entity, form);
		return monitorInfoRepository.save(entity);
	}
	@ApiOperation(value = "更新视频", notes = "",hidden=true)
	@RequestMapping(value = "monitor", method = RequestMethod.PUT)
	public MonitorEntity updateOne(
			@ApiParam(name = "source", required = true, value = "更新 表单参数") @RequestBody UpdateOneMonitorForm form)
			throws Exception {
		if (StringUtils.isEmpty(form.getId()))
			return null;
		MonitorEntity entity = monitorInfoRepository.findOne(form.getId());
		if(entity == null) 
			return  null;
		BeanUtils.copyProperties(entity, form);
		return monitorInfoRepository.save(entity);
	}
	@ApiOperation(value = "分页查询视频", notes = "")
	@GetMapping("monitor/listByPage/{page}/{size}")
	public Page<MonitorEntity> listByPage(
			@ApiParam(name = "page", required = true, value = "实时页数") @PathVariable Integer page,
			@ApiParam(name = "size", required = true, value = "页面大小") @PathVariable Integer size) {
		PageRequest pageReq = new PageRequest(page, size, Direction.DESC, "publishTime");
		Page<MonitorEntity> monitorPage = monitorInfoRepository.findAll(pageReq);
		return monitorPage;
	}
	
	
	@ApiOperation(value = "获取一个视频", notes = "根据网络信息")
	@CrossOrigin(origins="*")
	@GetMapping("monitor/getOne")
	public MonitorEntity getOne(
			@ApiParam(name = "ip", required = true, value = "ip") 
			@RequestParam("ip")
			String ip,
			@ApiParam(name = "channelNum", required = true, value = "通道号") 
			@RequestParam("channelNum")
			Integer channelNum) {
		
		return monitorInfoRepository.findByIpAndChannelNum(ip,channelNum);
	}
	
	@ApiOperation(value = "获取一个视频", notes = "根据id")
	@CrossOrigin(origins="*")
	@GetMapping("monitor/getOne/{id}")
	public MonitorEntity getOne(
			@ApiParam(name = "id", required = true, value = "视频流id") @PathVariable("id") String id) {
		
		return monitorInfoRepository.findOne(id);
	}
	
	@ApiOperation(value = "删除视频", notes = "",hidden=true)
	@DeleteMapping("monitor")
	public void delOne(@ApiParam(name = "id", required = true) @RequestParam String id) {
		monitorInfoRepository.delete(id);
	}
	
	@ApiOperation(value = "发布一个直播视频", notes = "根据id")
	@PostMapping("monitorCount/publish_live/{id}")
	public ResponseEntity<MonitorResp> publishLiveMonitor(
			@ApiParam(name = "id", required = true, value = "视频流id") @PathVariable("id") String id) throws Exception {
		MonitorEntity monitorEntity = monitorInfoRepository.findOne(id);
		if(monitorEntity==null){
			return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.找不到对应的视频流), HttpStatus.NOT_FOUND);
		}else if(monitorEntity.getPushState()){
			return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.已经推送过), HttpStatus.ACCEPTED);
		}else{
			boolean state = pullEndpointClientServer.noticPullEndpoint(monitorEntity);//通知推流端
			if(state)
				return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.正常), HttpStatus.ACCEPTED);
			else
				return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.推流失败), HttpStatus.NOT_FOUND);
		}
	}
	@ApiOperation(value = "发布一个回放视频", notes = "")
	@CrossOrigin(origins="*")
	@PostMapping("monitor/publish_vod")
	public VodParam publishVodMonitor(
			@ApiParam(name = "vodMonitorForm", required = true, value = "点播单") @ModelAttribute VodMonitorForm vodMonitorForm) throws Exception {
		
		String monitorId = vodMonitorForm.getMonitorId();
		
		File indexFile = new File(downloadPath+monitorId+"/index.json");
		
		if(indexFile.exists()){
			VodParam param = monitorServer.startDownLoadActionToVodByOldIndexFile(vodMonitorForm,indexFile);
			if(param == null)
				return  monitorServer.startDownLoadActionToVodByNewIndexFile(vodMonitorForm);
			else 
				return param;
		}else
			return monitorServer.startDownLoadActionToVodByNewIndexFile(vodMonitorForm);
		
	}
	@ApiOperation(value = "发布一个直播视频(ip,通道,类型)", notes = "如果重复 则 拿第一个")
	@CrossOrigin(origins="*")
	@PostMapping("monitor/publish_live")
	public ResponseEntity<MonitorResp> publishLiveMonitor(
			@ApiParam(name = "ip", required = true, value = "ip") 
			@RequestParam("ip")
			String ip,
			@ApiParam(name = "channelNum", required = true, value = "通道号") 
			@RequestParam("channelNum")
			Integer channelNum,
			@ApiParam(name = "userType", required = true, value = "类型") 
			@RequestParam("userType")
			VideoRecordUserType userType) throws Exception {
		
		MonitorEntity monitorEntity = monitorInfoRepository.findOneByIpAndChannelNumAndVrUserType(ip,channelNum,userType);
		
		if(monitorEntity == null){
			return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.找不到对应的视频流), HttpStatus.NOT_FOUND);
		}else if(monitorEntity.getPushState()){
			return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.已经推送过), HttpStatus.ACCEPTED);
		}else{
			boolean state = pullEndpointClientServer.noticPullEndpoint(monitorEntity);//通知推流端
			if(state)
				return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.正常), HttpStatus.ACCEPTED);
			else
				return new ResponseEntity<MonitorResp>(new MonitorResp(monitorEntity, MonitorRespMessage.推流失败), HttpStatus.NOT_FOUND);
		}
	}
}
