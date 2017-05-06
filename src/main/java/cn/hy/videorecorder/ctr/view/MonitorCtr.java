package cn.hy.videorecorder.ctr.view;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@Component("monitorViewCtr")
@ApiIgnore
public class MonitorCtr {

	@GetMapping("monitor/index")
	public String index(){
		
		
		return "index";
	}
}
