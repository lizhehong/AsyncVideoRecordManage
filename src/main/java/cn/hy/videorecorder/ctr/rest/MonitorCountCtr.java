package cn.hy.videorecorder.ctr.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hy.videorecorder.entity.MonitorCountEntity;
import cn.hy.videorecorder.form.monitorcount.AddOneMonitorCountForm;
import cn.hy.videorecorder.form.monitorcount.UpdateOneMonitorCountForm;
import cn.hy.videorecorder.repository.MonitorCountRepository;
import cn.hy.videorecorder.server.MonitorCountServer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 直播流人数统计
 * @author Administrator
 *
 */
@RestController
public class MonitorCountCtr {

	@Autowired
	private MonitorCountRepository monitorCountRepository;
	
	@Autowired @Qualifier("monitorCountServer")
	private MonitorCountServer monitorCountServer;
	
	@PostMapping("monitorCount")
	@ApiOperation(value = "创建视频统计", notes = "")
	public MonitorCountEntity addOne(
			@ApiParam(name = "form", required = true, value = "添加 表单参数") @RequestBody AddOneMonitorCountForm form ,
			HttpServletRequest req)
			throws Exception {
		
		return monitorCountServer.addOneCount(form);
	}
	@ApiOperation(value = "更新统计", notes = "依据统计id")
	@RequestMapping(value = "monitorCount", method = RequestMethod.PUT)
	public MonitorCountEntity updateOne(
			@ApiParam(name = "form", required = true, value = "更新 表单参数") @RequestBody UpdateOneMonitorCountForm form)
			throws Exception {
		
		return monitorCountServer.updateOneCount(form);
	}
	@ApiOperation(value = "分页查询统计", notes = "")
	@GetMapping("monitorCount/listByPage/{page}/{size}")
	public Page<MonitorCountEntity> listByPage(
			@ApiParam(name = "page", required = true, value = "实时页数") @PathVariable Integer page,
			@ApiParam(name = "size", required = true, value = "页面大小") @PathVariable Integer size) {
		PageRequest pageReq = new PageRequest(page, size, Direction.DESC, "publishTime");
		Page<MonitorCountEntity> monitorPage = monitorCountRepository.findAll(pageReq);
		return monitorPage;
	}
	@ApiOperation(value = "获取一个统计", notes = "根据视频流id 和 订阅直播的服务器id 查询")
	@GetMapping("monitorCount/getOne/{monitorId}/{MonitorClientServiceConnectionId}")
	public MonitorCountEntity getOne(
			@ApiParam(name = "monitorId", required = true, value = "视频流id")
			@PathVariable("monitorId")
			String monitorId,
			@ApiParam(name = "MonitorClientServiceConnectionId", required = true, value = "订阅直播的服务器id")
			@PathVariable("MonitorClientServiceConnectionId")
			String MonitorClientServiceConnectionId){
		
		
		return monitorCountRepository.findByMidAndMcId(monitorId,MonitorClientServiceConnectionId);
	
	}
	
	@ApiOperation(value = "获取一个统计", notes = "根据id")
	@GetMapping("monitorCount/getOne/{id}")
	public MonitorCountEntity getOne(
			@ApiParam(name = "id", required = true, value = "视频流id") @PathVariable("id") Long id) {
		
		return monitorCountRepository.findOne(id);
	}
	@ApiOperation(value = "删除视频统计", notes = "")
	@DeleteMapping("monitorCount")
	public void delOne(@ApiParam(name = "id", required = true) @RequestParam Long id) {
		monitorCountRepository.delete(id);
	}
	
}
