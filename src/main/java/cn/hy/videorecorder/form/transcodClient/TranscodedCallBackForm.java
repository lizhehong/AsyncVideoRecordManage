package cn.hy.videorecorder.form.transcodClient;

import java.io.File;
import java.util.List;

import lombok.Data;

@Data
public class TranscodedCallBackForm {

	private String id;
	
	private List<File> outFileList;
}
