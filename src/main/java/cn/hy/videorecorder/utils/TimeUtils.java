package cn.hy.videorecorder.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hy.videorecorder.bo.QueryTimeParam;
import cn.hy.videorecorder.bo.TimeZone;
import cn.hy.videorecorder.entity.type.VodRequestState;

public class TimeUtils {

	private static  Logger logger = LoggerFactory.getLogger(TimeUtils.class);
	/**
	 * 系统时间步长 找出当前时间所在的时间区间内 步长在 60能整除的
	 * @param date
	 * @param secStep
	 * @return
	 */
	public static TimeZone findTimeZoneByTimeStepInMin(Date date,int secStep){
		//一分钟 60秒
		int perMin = 60;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int sec = calendar.get(Calendar.SECOND);
		calendar.add(Calendar.SECOND, -sec);
		int multiple = (perMin / secStep) + 1;
		List<Integer> list = new ArrayList<>();
		for(int i=0;i<multiple;i++){
			int val = i*secStep;
			list.add(val);
		}
		
		for(int i=0;i<list.size();i++){
			if(sec >= list.get(i) && sec <= list.get(i+1)){
				calendar.add(Calendar.SECOND, list.get(i) );
				Date sT = calendar.getTime();
				calendar.add(Calendar.SECOND, secStep);
				Date eT = calendar.getTime();
				return new TimeZone(sT, eT);
			}
		}
		return null;
		
		
	}

	/**
	 * 将时间化整为下一整分钟 例如 10:20:01->10:20:00
	 */
	public static Date dateArrangeToLastMin(Date date){
		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.setTime(date);
		int sec = tmpCalendar.get(Calendar.SECOND);
		if(sec > 0){
			tmpCalendar.add(Calendar.SECOND, -sec);
		}
		return tmpCalendar.getTime() ;
	}
	/**
	 * 将时间化整为下一整分钟 例如 10:20:01->10:21:00
	 * @param date
	 */
	public static Date dateArrangeNetMin(Date date){
		Calendar tmpCalendar = Calendar.getInstance();
		tmpCalendar.setTime(date);
		int sec = tmpCalendar.get(Calendar.SECOND);
		if(sec > 0){
			tmpCalendar.add(Calendar.SECOND, -sec);
			tmpCalendar.add(Calendar.MINUTE, 1);
		}
		return tmpCalendar.getTime();
	}
	/**
	 * 填充整分钟再 时间段平均分割 升序排列
	 * 时间太长 可能会占用内存或者切割时间
	 * @param queryTimeParam	查询时间
	 * @param secondStep 		秒步长
	 * @return
	 */
	public static List<QueryTimeParam> fillFullMinAndSplitTime(QueryTimeParam queryTimeParam,int secondStep,int cacheMaxCount) {
		List<QueryTimeParam> queryTimeParams = new ArrayList<>();
		if( queryTimeParam.getEndTime()!=null && queryTimeParam.getStartTime()!=null ){	
			//时间节点化为整分
			Date startTime = dateArrangeToLastMin(queryTimeParam.getStartTime());
			Date endTime = dateArrangeNetMin(queryTimeParam.getEndTime());
			
			//得到時間差
			Long timeLong = endTime.getTime() - startTime.getTime();

			//拿到切片个数
			long num = (timeLong/(1000*secondStep));
			//限制
			if(num > cacheMaxCount)
				num = cacheMaxCount + 1;
			logger.info("切片数：{},时长：{},开始：{},结束：{}",num,timeLong,startTime,endTime);
		
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startTime);
			if(num > 0){
				for(int i=0; i < num;i++){
					
					QueryTimeParam newQueryTime = new QueryTimeParam();
					newQueryTime.setStartTime(startTime);
					//计算结尾时间
					calendar.add(Calendar.SECOND, secondStep);
					newQueryTime.setEndTime(calendar.getTime());
					newQueryTime.setVodReqState(VodRequestState.未请求);
					queryTimeParams.add(newQueryTime);
					startTime = calendar.getTime();//末尾时间作为第二次的开始时间
				}
				//防止步长不是整分倍
				if(!startTime.equals(endTime)){
					//最后的时长
					QueryTimeParam newQueryTime = new QueryTimeParam();
					newQueryTime.setStartTime(startTime);
					newQueryTime.setEndTime(endTime);
					newQueryTime.setVodReqState(VodRequestState.未请求);
					queryTimeParams.add(newQueryTime);
				}
			}else {//不用切片的情况下
				queryTimeParams.add(queryTimeParam);
			}
			
		}
		
		return queryTimeParams;
	}
}
