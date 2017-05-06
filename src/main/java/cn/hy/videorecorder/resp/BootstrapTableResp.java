package cn.hy.videorecorder.resp;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BootstrapTableResp<T> {

	private Integer total;
	
	private List<T> rows = new ArrayList<>();

	public BootstrapTableResp(Integer total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	
	
}
