package com.ernest.mobilecontextaware.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	
	public static String GetCurDateTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//mm:���ӣ�hh:12Сʱ��
		Date curDate = new Date(System.currentTimeMillis());	//��ȡ��ǰʱ��     
		return simpleDateFormat.format(curDate);
	}

}
