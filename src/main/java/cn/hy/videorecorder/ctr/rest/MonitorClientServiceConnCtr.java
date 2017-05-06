package cn.hy.videorecorder.ctr.rest;


import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;
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

import cn.hy.videorecorder.entity.MonitorClientServiceConnectionEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.form.comm.PageForm;
import cn.hy.videorecorder.form.monitorClientserviceconn.AddOneMonitorClientForm;
import cn.hy.videorecorder.form.monitorClientserviceconn.UpdateOneMonitorClientForm;
import cn.hy.videorecorder.repository.MonitorClientServiceConnectionRepository;
import cn.hy.videorecorder.resp.BootstrapTableResp;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class MonitorClientServiceConnCtr {

	@Autowired
	private MonitorClientServiceConnectionRepository monitorClientServiceConnectionRepository;
	
	@PostMapping("monitorClientServiceConnection")
	@ApiOperation(value = "创建视频客户端订阅服务器", notes = "")
	public MonitorClientServiceConnectionEntity addOne(
			@ApiParam(name = "form", required = true, value = "添加 订阅表单参数") @ModelAttribute AddOneMonitorClientForm form)
			throws Exception {
		MonitorClientServiceConnectionEntity entity = new MonitorClientServiceConnectionEntity();
		BeanUtils.copyProperties(entity, form);
		return monitorClientServiceConnectionRepository.save(entity);
	}
	@ApiOperation(value = "更新视频", notes = "")
	@RequestMapping(value = "monitorClientServiceConnection", method = RequestMethod.PUT)
	public MonitorClientServiceConnectionEntity updateOne(
			@ApiParam(name = "form", required = true, value = "更新 订阅表单参数") @ModelAttribute UpdateOneMonitorClientForm form)
			throws Exception {
		if (StringUtils.isEmpty(form.getId()))
			return null;
		MonitorClientServiceConnectionEntity entity = monitorClientServiceConnectionRepository.findOne(form.getId());
		if(entity == null) 
			return  null;
		BeanUtils.copyProperties(entity, form);
		return monitorClientServiceConnectionRepository.save(entity);
	}
	@ApiOperation(value = "分页查询视频客户端订阅服务器", notes = "")
	@GetMapping("monitorClientServiceConnection/listByPage/{page}/{size}")
	public Page<MonitorClientServiceConnectionEntity> listByPage(
			@ApiParam(name = "page", required = true, value = "实时页数") @PathVariable Integer page,
			@ApiParam(name = "size", required = true, value = "页面大小") @PathVariable Integer size) {
		PageRequest pageReq = new PageRequest(page, size, Direction.DESC, "publishTime");
		Page<MonitorClientServiceConnectionEntity> monitorPage = monitorClientServiceConnectionRepository.findAll(pageReq);
		return monitorPage;
	}
	@ApiOperation(value = "分页查询视频客户端订阅服务器", notes = "")
	@GetMapping("monitorClientServiceConnection/listByPage")
	public BootstrapTableResp<MonitorClientServiceConnectionEntity> listByPage(
			@ModelAttribute PageForm form) {
		
		String sortName = "publishTime";
		if(!StringUtils.isEmpty(form.getSortName()))
			sortName = form.getSortName();
		
		PageRequest pageReq = new PageRequest(form.getPageNumber(), form.getPageSize(), form.getSortOrder(), sortName);
		Page<MonitorClientServiceConnectionEntity> monitorPage = monitorClientServiceConnectionRepository.findAll(pageReq);
		
		return new BootstrapTableResp<MonitorClientServiceConnectionEntity>(monitorPage.getSize(),monitorPage.getContent());
	}
	@ApiOperation(value = "获取一个视频客户端订阅服务器", notes = "根据id")
	@GetMapping("monitorClientServiceConnection/getOne/{id}")
	public MonitorClientServiceConnectionEntity getOne(
			@ApiParam(name = "id", required = true, value = "视频流id") @PathVariable("id") String id) {
		
		return monitorClientServiceConnectionRepository.findOne(id);
	}
	@ApiOperation(value = "删除视频客户端订阅服务器", notes = "")
	@DeleteMapping("monitorClientServiceConnection")
	public void delOne(@ApiParam(name = "id", required = true) @RequestParam String id) {
		monitorClientServiceConnectionRepository.delete(id);
	}
	
	@ApiOperation(value = "查询一个视频客户端订阅服务器", notes = "通过网络信息")
	@PostMapping("monitorClientServiceConnection/getOne")
	public MonitorClientServiceConnectionEntity getOne(
			@ApiParam(name = "net", required = true, value = "网络信息")
			@RequestBody NetIndentity net){
		return monitorClientServiceConnectionRepository.findByNet(net);
	}
}
