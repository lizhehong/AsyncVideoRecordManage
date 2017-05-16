package cn.hy.videorecorder.ctr.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hy.videorecorder.entity.TranscodClientEntity;
import cn.hy.videorecorder.entity.indentity.NetIndentity;
import cn.hy.videorecorder.form.transcodClient.TranscodedCallBackForm;
import cn.hy.videorecorder.form.transcodClient.UpdateOneTranscodClientForm;
import cn.hy.videorecorder.repository.TranscodingClientRepsoitory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 转码客户端
 * @author Administrator
 *
 */
@RestController
public class TranscodClientCtr {

	@Autowired
	private TranscodingClientRepsoitory transcodingClientRepsoitory;
	
	@PostMapping("transcodClient")
	@ApiOperation(value = "创建转码端", notes = "")
	public TranscodClientEntity addOne(
			@ApiParam(name = "form", required = true, value = "添加 表单参数") @RequestBody NetIndentity net ,
			HttpServletRequest req)
			throws Exception {
		TranscodClientEntity entity = new TranscodClientEntity();
		entity.setClientNet(net);
		return transcodingClientRepsoitory.save(entity);
	}
	@ApiOperation(value = "更新转码端", notes = "",hidden=true)
	@RequestMapping(value = "transcodClient", method = RequestMethod.PUT)
	public TranscodClientEntity updateOne(
			@ApiParam(name = "source", required = true, value = "更新 表单参数") @RequestBody UpdateOneTranscodClientForm form)
			throws Exception {
		if (StringUtils.isEmpty(form.getId()))
			return null;
		TranscodClientEntity entity = transcodingClientRepsoitory.findOne(form.getId());
		if(entity == null) 
			return  null;
		BeanUtils.copyProperties(entity, form);
		return transcodingClientRepsoitory.save(entity);
	}
	
	@ApiOperation(value = "转码端任务完成回调", notes = "主要用于客户端程序主动请求更改状态",hidden=true)
	@RequestMapping(value = "transcodClient/callBack", method = RequestMethod.POST)
	public TranscodClientEntity transcodedCallBack(
			@ApiParam(name = "transcodedCallBackForm", required = true, value = "更新 表单参数") TranscodedCallBackForm transcodedCallBackForm)
			throws Exception {
		if (StringUtils.isEmpty(transcodedCallBackForm.getId()))
			return null;
		TranscodClientEntity entity = transcodingClientRepsoitory.findOne(transcodedCallBackForm.getId());
		if(entity == null) 
			return  null;
		
		//TODO 回调逻辑
		
		
		
		return transcodingClientRepsoitory.save(entity);
	}
	
	
	@ApiOperation(value = "分页查询转码端", notes = "")
	@GetMapping("transcodClient/listByPage/{page}/{size}")
	public Page<TranscodClientEntity> listByPage(
			@ApiParam(name = "page", required = true, value = "实时页数") @PathVariable Integer page,
			@ApiParam(name = "size", required = true, value = "页面大小") @PathVariable Integer size) {
		PageRequest pageReq = new PageRequest(page, size, Direction.DESC, "publishTime");
		Page<TranscodClientEntity> monitorPage = transcodingClientRepsoitory.findAll(pageReq);
		return monitorPage;
	}
	
	
	@ApiOperation(value = "获取一个转码端", notes = "根据id")
	@CrossOrigin(origins="*")
	@GetMapping("transcodClient/getOne/{id}")
	public TranscodClientEntity getOne(
			@ApiParam(name = "id", required = true, value = "视频流id") @PathVariable("id") String id) {
		
		return transcodingClientRepsoitory.findOne(id);
	}
	
	@ApiOperation(value = "删除转码端", notes = "",hidden=true)
	@DeleteMapping("transcodClient")
	public void delOne(@ApiParam(name = "id", required = true) @RequestParam String id) {
		transcodingClientRepsoitory.delete(id);
	}
	
}
