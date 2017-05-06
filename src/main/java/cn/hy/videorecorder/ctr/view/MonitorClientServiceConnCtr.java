package cn.hy.videorecorder.ctr.view;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import springfox.documentation.annotations.ApiIgnore;


@Controller
@Component("monitorClientServiceConnView")
@ApiIgnore
public class MonitorClientServiceConnCtr {

	@GetMapping("subscribe/index")
	public String index(){
		
		return "subscribe";
	}
	
}
