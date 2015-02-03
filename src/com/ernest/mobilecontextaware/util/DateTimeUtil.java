package com.ernest.mobilecontextaware.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	
	public static String GetCurDateTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//mm:分钟，hh:12小时制
		Date curDate = new Date(System.currentTimeMillis());	//获取当前时间     
		return simpleDateFormat.format(curDate);
	}

}
