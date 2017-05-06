package cn.hy.videorecorder.bo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutHandler extends Thread {
	// 控制线程状态
	volatile boolean status = true;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	BufferedReader br = null;

	String type = null;

	public OutHandler(InputStream is, String type) {
		br = new BufferedReader(new InputStreamReader(is));
		this.type = type;
	}

	/**
	 * 重写线程销毁方法，安全的关闭线程
	 */
	@Override
	public void destroy() {
		status = false;
	}

	/**
	 * 执行输出线程
	 */
	@Override
	public void run() {
		String msg = null;
		try {
			while (status) {
				if ((msg = br.readLine()) != null) {
					msg = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
