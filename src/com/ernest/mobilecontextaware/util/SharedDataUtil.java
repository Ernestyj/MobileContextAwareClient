package com.ernest.mobilecontextaware.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;

public class SharedDataUtil {
	private final static String TAG = "SharedDataUtil";
	
	private final static String SHAREDPREFERENCES_FILE = "mobilecontextaware";
	
	//private final static String USER_INDEX = "user.index";	//auto-increament, should be set to 0
	private final static String IMEI = "userid";
	private final static String USER_NAME = "username";
	private final static String USER_PASSWORD = "userpassword";
	private final static String CUR_DATE_TIME = "datetime";
	private final static String CUR_PROVINCE = "province";
	private final static String CUR_CITY = "city";
	private final static String CUR_DISTRICT = "district";
	private final static String CUR_ADDR = "address";
	private final static String CUR_LAT = "latitude";
	private final static String CUR_LON = "longtitude";
	private final static String CUR_RADIUS = "radius";
	private final static String CUR_TYPE = "nettype";
	private final static String CUR_OPERATOR = "operator";
	private final static String REALTIME_TEMP = "realtimetemperature";
	private final static String TODAY_TEMP_RANGE = "temperaturerange";
	private final static String CUR_TEMP = "temperature";
	private final static String CUR_LIGHT = "light";
	private final static String CUR_ACCEL_X = "x";
	private final static String CUR_ACCEL_Y = "y";
	private final static String CUR_ACCEL_Z = "z";
	private final static String CUR_WEATHER = "CurrentWeather";
	private final static String TODAY_WEATHER = "TodayWeather";
	
	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor mEditor;
	
	public static boolean WriteDefaultUserInfo(Context context){
		initSharedData(context);
		//mEditor.putString(USER_INDEX, "0");
		mEditor.putString(USER_NAME, "defaultName");
		mEditor.putString(USER_PASSWORD, "defaultPassword");
		return mEditor.commit();
	}
	
	public static boolean WriteCurLocationInfos(Context context, BDLocation location){
		initSharedData(context);
		mEditor.putString(CUR_DATE_TIME, location.getTime());
		mEditor.putString(CUR_PROVINCE, location.getProvince());
		mEditor.putString(CUR_CITY, location.getCity());
		mEditor.putString(CUR_DISTRICT, location.getDistrict());
		mEditor.putString(CUR_ADDR, location.getAddrStr());
		mEditor.putString(CUR_LAT, String.valueOf(location.getLatitude()));
		mEditor.putString(CUR_LON, String.valueOf(location.getLongitude()));
		mEditor.putString(CUR_RADIUS, String.valueOf(location.getRadius()));
		mEditor.putString(CUR_TYPE, BaiduLocationUtil.GetSourceType(location.getLocType()));
		mEditor.putString(CUR_OPERATOR, BaiduLocationUtil.GetSourceType(location.getOperators()));
		return mEditor.commit();
	}
	
	public static boolean WriteCurTemperature(Context context, float temp){
		initSharedData(context);
		mEditor.putString(CUR_TEMP, String.valueOf(temp));
		return mEditor.commit();
	}
	
	public static boolean WriteCurLight(Context context, float light){
		initSharedData(context);
		mEditor.putString(CUR_LIGHT, String.valueOf(light));
		return mEditor.commit();
	}
	
	public static boolean WriteCurAcceleration(Context context, float[] values){
		initSharedData(context);
		mEditor.putString(CUR_ACCEL_X, String.valueOf(values[0]));
		mEditor.putString(CUR_ACCEL_Y, String.valueOf(values[1]));
		mEditor.putString(CUR_ACCEL_Z, String.valueOf(values[2]));
		return mEditor.commit();
	}
	
	public static boolean WriteCurWeatherInfos(Context context, String cur, String today, String temp, String tempRange){
		initSharedData(context);
		mEditor.putString(CUR_WEATHER, cur);
		mEditor.putString(TODAY_WEATHER, today);
		mEditor.putString(REALTIME_TEMP, temp);
		mEditor.putString(TODAY_TEMP_RANGE, tempRange);
		return mEditor.commit();
	}
	
	public static boolean WriteIMEI(Context context){
		initSharedData(context);
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mEditor.putString(IMEI, telephonyManager.getDeviceId());
		return mEditor.commit();
	}
	
	public static String GetIMEI(Context context){
		initSharedData(context);
		return mSharedPreferences.getString(IMEI, null);
	}
	
	public static String GetCurDistrictOrCity(Context context){
		initSharedData(context);
		String district = mSharedPreferences.getString(CUR_DISTRICT, null);
		String district_pocessed = null;
		if(null != district){
			if(district.length() >= 3){ //Ex.定南县->定南
				if(district.contains("市")) district_pocessed = district.split("市")[0];
				if(district.contains("县")) district_pocessed = district.split("县")[0];
			}else{	//Ex.郫县->郫县
				district_pocessed = district;
			}
		}else{	//Deprecated
			String city = mSharedPreferences.getString(CUR_CITY, null).split("市")[0];
			Log.i(TAG, "Get city: " + city);
			return city;
		}
		Log.i(TAG, "Get district: " + district_pocessed);
		return district_pocessed;
	}
	
	public static String GetDataUpdateTest(Context context){	//Test only
		initSharedData(context);
		return  "传感器信息: " +
				"\n      " + "温度： " + mSharedPreferences.getString(CUR_TEMP, "N/A") +
				"\n      " + "光强： " + mSharedPreferences.getString(CUR_LIGHT, "N/A") +
				"\n      " + "加速度X: " + mSharedPreferences.getString(CUR_ACCEL_X, "N/A") +
				"\n      " + "加速度Y: " + mSharedPreferences.getString(CUR_ACCEL_Y, "N/A") +
				"\n      " + "加速度Z: " + mSharedPreferences.getString(CUR_ACCEL_Y, "N/A");
	}
	
	public static String ReadSharedData(Context context){
		initSharedData(context);
		String info = "   用户基本资料: " + 
				"\n      " + "IMEI： " + mSharedPreferences.getString(IMEI, "N/A") +
				"\n      " + "用户名： " + mSharedPreferences.getString(USER_NAME, "N/A") +
				"\n      " + "用户密码： " + mSharedPreferences.getString(USER_PASSWORD, "N/A") +
				"\n   当前位置信息: " + 
				"\n      " + "时间： " + mSharedPreferences.getString(CUR_DATE_TIME, "N/A") +
				"\n      " + "省份： " + mSharedPreferences.getString(CUR_PROVINCE, "N/A") +
				"\n      " + "城市： " + mSharedPreferences.getString(CUR_CITY, "N/A") +
				"\n      " + "地区： " + mSharedPreferences.getString(CUR_DISTRICT, "N/A") +
				"\n      " + "地址： " + mSharedPreferences.getString(CUR_ADDR, "N/A") +
				"\n      " + "纬度： " + mSharedPreferences.getString(CUR_LAT, "N/A") +
				"\n      " + "经度： " + mSharedPreferences.getString(CUR_LON, "N/A") +
				"\n      " + "半径： " + mSharedPreferences.getString(CUR_RADIUS, "N/A") +
				"\n      " + "类型： " + mSharedPreferences.getString(CUR_TYPE, "N/A") +
				"\n      " + "运营商： " + mSharedPreferences.getString(CUR_OPERATOR, "N/A") +
				"\n   传感器信息: " +
				"\n      " + "温度： " + mSharedPreferences.getString(CUR_TEMP, "N/A") +
				"\n      " + "光强： " + mSharedPreferences.getString(CUR_LIGHT, "N/A") +
				"\n      " + "加速度X: " + mSharedPreferences.getString(CUR_ACCEL_X, "N/A") +
				"\n      " + "加速度Y: " + mSharedPreferences.getString(CUR_ACCEL_Y, "N/A") +
				"\n      " + "加速度Z: " + mSharedPreferences.getString(CUR_ACCEL_Y, "N/A") +
				mSharedPreferences.getString(CUR_WEATHER, "N/A") + " " + mSharedPreferences.getString(REALTIME_TEMP, "N/A") + 
				mSharedPreferences.getString(TODAY_WEATHER, "N/A") + " " + mSharedPreferences.getString(TODAY_TEMP_RANGE, "N/A");
		return info;
	}
	
	public static List<NameValuePair> GetUserInfoNameValuePairList(Context context){
		initSharedData(context);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair(IMEI, mSharedPreferences.getString(IMEI, "N/A")));
		params.add(new BasicNameValuePair(USER_NAME, mSharedPreferences.getString(USER_NAME, "N/A")));
		params.add(new BasicNameValuePair(USER_PASSWORD, mSharedPreferences.getString(USER_PASSWORD, "N/A")));
		
		return params;
	}
	
	public static List<NameValuePair> GetContextNameValuePairList(Context context){
		initSharedData(context);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair(IMEI, mSharedPreferences.getString(IMEI, "N/A")));
				
		params.add(new BasicNameValuePair(CUR_PROVINCE, mSharedPreferences.getString(CUR_PROVINCE, "N/A")));
		params.add(new BasicNameValuePair(CUR_CITY, mSharedPreferences.getString(CUR_CITY, "N/A")));
		params.add(new BasicNameValuePair(CUR_DISTRICT, mSharedPreferences.getString(CUR_DISTRICT, "N/A")));
		params.add(new BasicNameValuePair(CUR_ADDR, mSharedPreferences.getString(CUR_ADDR, "N/A")));
		params.add(new BasicNameValuePair(CUR_LAT, mSharedPreferences.getString(CUR_LAT, "N/A")));
		params.add(new BasicNameValuePair(CUR_LON, mSharedPreferences.getString(CUR_LON, "N/A")));
		params.add(new BasicNameValuePair(CUR_RADIUS, mSharedPreferences.getString(CUR_RADIUS, "N/A")));
		params.add(new BasicNameValuePair(CUR_TYPE, mSharedPreferences.getString(CUR_TYPE, "N/A")));
		params.add(new BasicNameValuePair(CUR_OPERATOR, mSharedPreferences.getString(CUR_OPERATOR, "N/A")));
		
		params.add(new BasicNameValuePair(CUR_TEMP, mSharedPreferences.getString(CUR_TEMP, "N/A")));
		params.add(new BasicNameValuePair(CUR_LIGHT, mSharedPreferences.getString(CUR_LIGHT, "N/A")));
		params.add(new BasicNameValuePair(CUR_ACCEL_X, mSharedPreferences.getString(CUR_ACCEL_X, "N/A")));
		params.add(new BasicNameValuePair(CUR_ACCEL_Y, mSharedPreferences.getString(CUR_ACCEL_Y, "N/A")));
		params.add(new BasicNameValuePair(CUR_ACCEL_Z, mSharedPreferences.getString(CUR_ACCEL_Z, "N/A")));
		
		params.add(new BasicNameValuePair(REALTIME_TEMP, mSharedPreferences.getString(REALTIME_TEMP, "N/A")));
		params.add(new BasicNameValuePair(TODAY_TEMP_RANGE, mSharedPreferences.getString(TODAY_TEMP_RANGE, "N/A")));
		
		return params;
	}
	
//	public static List<NameValuePair> GetDataFotTest(Context context){	//test only
//		initSharedData(context);
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair(USER_NAME, mSharedPreferences.getString(USER_NAME, "N/A")));
//		params.add(new BasicNameValuePair(USER_PASSWORD, mSharedPreferences.getString(USER_PASSWORD, "N/A")));
//		return params;
//	}
	
	private static void initSharedData(Context context){
		mSharedPreferences = context.getSharedPreferences(SHAREDPREFERENCES_FILE, Context.MODE_WORLD_WRITEABLE);
		mEditor = mSharedPreferences.edit();
	}

}
