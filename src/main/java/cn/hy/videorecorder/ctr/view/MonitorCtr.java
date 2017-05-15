package cn.hy.videorecorder.ctr.view;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import cn.hy.videorecorder.bo.VodParam;
import cn.hy.videorecorder.form.monitor.VodMonitorForm;
import cn.hy.videorecorder.server.MonitorServer;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Component("monitorViewCtr")
@ApiIgnore
public class MonitorCtr {

	@Autowired @Qualifier("monitorServer")
	private MonitorServer monitorServer;
	
	@GetMapping("monitor/index")
	public String index(){
		
		
		return "/index";
	}
	@GetMapping("monitor/vod")
	public ModelAndView vod(@ModelAttribute @Valid VodMonitorForm vodMonitorForm) throws Exception{
		ModelAndView mav = new ModelAndView("/vod");
		
		mav.addObject("videoSpeedUpNum", vodMonitorForm.getSppeedUpNum());
	
		VodParam vodParam = monitorServer.publishVodMonitor(vodMonitorForm);
		
	    mav.addObject("vodParam", vodMonitorForm );
		
		return mav;
	}
}
