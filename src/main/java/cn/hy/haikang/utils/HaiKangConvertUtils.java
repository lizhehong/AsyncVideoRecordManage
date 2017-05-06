package cn.hy.haikang.utils;

import java.util.Calendar;
import java.util.Date;

import cn.hy.haikang.config.HCNetSDK.NET_DVR_TIME;


public class HaiKangConvertUtils {
	
	public static NET_DVR_TIME DateToHaiKangLocalTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		NET_DVR_TIME struStartTime = new NET_DVR_TIME();
		struStartTime.dwYear = calendar.get(Calendar.YEAR);
		struStartTime.dwMonth = calendar.get(Calendar.MONDAY)+1;
		struStartTime.dwDay = calendar.get(Calendar.DAY_OF_MONTH);
		struStartTime.dwHour = calendar.get(Calendar.HOUR_OF_DAY);
		struStartTime.dwMinute = calendar.get(Calendar.MINUTE);
		struStartTime.dwSecond = calendar.get(Calendar.SECOND);
		return struStartTime;
	}
	
}
