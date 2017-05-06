package cn.hy.videorecorder.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hy.videorecorder.bo.QueryTimeParam;

public class TimeUtils {

	private static  Logger logger = LoggerFactory.getLogger(TimeUtils.class);
	
	/**
	 * 时间段平均分割
	 * 时间太长 可能会占用内存或者切割时间
	 * @param queryTimeParam	查询时间
	 * @param secondStep 		秒步长
	 * @return
	 */
	public static List<QueryTimeParam> splitTime(QueryTimeParam queryTimeParam,int secondStep) {
		List<QueryTimeParam> queryTimeParams = new ArrayList<>();
		if( queryTimeParam.getEndTime()!=null && queryTimeParam.getStartTime()!=null ){

			//得到時間差
			Long timeLong = queryTimeParam.getEndTime().getTime() - queryTimeParam.getStartTime().getTime();

			//拿到切片个数
			long num = (timeLong/(1000*secondStep));
			logger.info("切片数：{},时长：{}",num,timeLong);
			Date startTime = queryTimeParam.getStartTime();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startTime);
			if(num > 0){
				for(int i=0; i < num;i++){
					
					QueryTimeParam newQueryTime = new QueryTimeParam();
					newQueryTime.setStartTime(startTime);
					//计算结尾时间
					calendar.add(Calendar.SECOND, secondStep);
					newQueryTime.setEndTime(calendar.getTime());
					
					queryTimeParams.add(newQueryTime);
					startTime = calendar.getTime();//末尾时间作为第二次的开始时间
				}
				if(!startTime.equals(queryTimeParam.getEndTime())){
					//最后的时长
					QueryTimeParam newQueryTime = new QueryTimeParam();
					newQueryTime.setStartTime(startTime);
					newQueryTime.setEndTime(queryTimeParam.getEndTime());
					queryTimeParams.add(newQueryTime);
				}
			}else {//不用切片的情况下
				queryTimeParams.add(queryTimeParam);
			}
			
		}
		
		return queryTimeParams;
	}
}
