package cn.hy.videorecorder.comparator;

import java.util.Comparator;

import cn.hy.videorecorder.entity.type.SortDirection;
import cn.hy.videorecorder.timer.TranscodingTask;
/**
 * 依据启末时间排序
 * @author Administrator
 *
 */
public class TranscodingTaskComparator implements Comparator<TranscodingTask>{

	private SortDirection direction;
	
	public TranscodingTaskComparator(SortDirection direction) {
		super();
		this.direction = direction;
	}


	@Override
	public int compare(TranscodingTask t1, TranscodingTask t2) {
		long t1StartTime = t1.getQueryTimeParam().getStartTime().getTime();
		long t2StartTime = t2.getQueryTimeParam().getStartTime().getTime();
		if(direction.equals(SortDirection.ASC))
			return (int) (t1StartTime - t2StartTime);
		else
			return (int) ( t2StartTime - t1StartTime);
	}   
	
}
