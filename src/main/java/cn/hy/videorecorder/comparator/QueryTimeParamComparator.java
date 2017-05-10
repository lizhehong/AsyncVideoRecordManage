package cn.hy.videorecorder.comparator;

import java.util.Comparator;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.entity.type.SortDirection;

public class QueryTimeParamComparator implements Comparator<QueryTimeParam>{

	private SortDirection direction;
	
	public QueryTimeParamComparator(SortDirection direction) {
		super();
		this.direction = direction;
	}


	@Override
	public int compare(QueryTimeParam q1, QueryTimeParam q2) {
		long t1StartTime = q1.getStartTime().getTime();
		long t2StartTime = q2.getStartTime().getTime();
		if(direction.equals(SortDirection.ASC))
			return (int) (t1StartTime - t2StartTime);
		else
			return (int) ( t2StartTime - t1StartTime);
	}   
	
}