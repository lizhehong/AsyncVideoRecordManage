package cn.hy.videorecorder.form.comm;


import org.springframework.data.domain.Sort.Direction;

import lombok.Data;

@Data
public class PageForm {

	private Integer pageSize;
	
	private Integer pageNumber;
	
	private String searchText;
	
	private String sortName;
	
	private Direction sortOrder;
	
}
