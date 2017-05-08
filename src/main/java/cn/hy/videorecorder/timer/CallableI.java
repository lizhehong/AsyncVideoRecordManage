package cn.hy.videorecorder.timer;

import java.util.concurrent.Callable;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.VodParam;

public interface CallableI<T> extends Callable<T> {

	public VodParam getVodParam();

	public QueryTimeParam getTimeParm();
}
