package com.ernest.mobilecontextaware.util;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.util.Log;

public class WeatherWSUtil {
	private final static String TAG = "WeatherWSUtil";
	
	public final static int NEW_WEATHER_MSG = 0x101;
	public final static String WEATHER_CUR = "CurrentWeather";
	public final static String WEATHER_TODAY = "TodayWeather";
	private final static String SERVICE_NS = "http://WebXml.com.cn/";
	private final static String SERVICE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";
	
	public static boolean GetWeather(Context context, String city)
	{
		SoapObject weatherDetail = WeatherWSUtil.getWeatherByCity(city);
		if(null == weatherDetail){
			Log.i(TAG, "Fail to get weather in " + city);
			return false;
		}else{
			Log.i(TAG, "Get weather raw infos success.");
		}
		//Format raw ex.: "今日天气实况：气温：28℃；风向/风力：南风1级；湿度：37%"
		String raw = null;
		try {
			raw = weatherDetail.getProperty(4).toString();
		} catch (Exception e) { 
			Log.i(TAG, "Weather service bad.");
			return false;
		}
		String[] semicolonRaw = raw.split("；");
		String temperature = semicolonRaw[0].split("：")[2];
		String weatherCurrent = 
					"\n   天气实况： " + 
					"\n      气温： " + temperature + 
					"\n      风向/风力： " + semicolonRaw[1].split("：")[1] +
					"\n      湿度： " + semicolonRaw[2].split("：")[1];
		//Format dateAndWeatherRaw ex.: "4月13日 多云转阵雨"
		String dateAndWeatherRaw = weatherDetail.getProperty(7).toString();
		String tempRange = weatherDetail.getProperty(8).toString();
		String weatherToday = 
					"\n   今日预报： " + 
					"\n      日期：  " + dateAndWeatherRaw.split(" ")[0] + 
					"\n      天气： " + dateAndWeatherRaw.split(" ")[1] + 
					"\n      气温： " + tempRange + 
					"\n      风度： " + weatherDetail.getProperty(9).toString() + "\n";
//		Message msg = new Message();
//		msg.what = NEW_WEATHER_MSG;
//		Bundle data = new Bundle();
//		data.putString(WEATHER_CUR, weatherCurrent);
//		data.putString(WEATHER_TODAY, weatherToday);
//		msg.setData(data);
//		ContextAwareApplicaiton.GetInstance().getWeatherHandler().sendMessage(msg);
//		Log.i(TAG, "Weather message is sent.");
		SharedDataUtil.WriteCurWeatherInfos(context, weatherCurrent, weatherToday, temperature, tempRange);
		return true;
	}
	
	private static SoapObject getWeatherByCity(String cityName){
		final String methodName = "getWeather";
		
		SoapObject outSoapObject = new SoapObject(SERVICE_NS, methodName);
		outSoapObject.addProperty("theCityCode", cityName);
		
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);//Soap1.1
		envelope.bodyOut = outSoapObject;
		envelope.dotNet = true;
		
		final HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
		httpTransportSE.debug = true;
		
		FutureTask<SoapObject> task = new FutureTask<SoapObject>(new Callable<SoapObject>() {
			@Override
			public SoapObject call() throws Exception {
				httpTransportSE.call(SERVICE_NS + methodName, envelope);
				if(null != envelope.getResponse()){
					SoapObject result = (SoapObject)envelope.bodyIn;
					SoapObject detail = (SoapObject)result.getProperty(methodName + "Result");
					return detail;
				}
				return null;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	public static List<String> GetProvinceList(){
		final String methodName = "getRegionProvince";
		
		final HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
		httpTransportSE.debug = true;
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);//Soap1.1
		SoapObject outSoapObject = new SoapObject(SERVICE_NS, methodName);
		envelope.bodyOut = outSoapObject;
		envelope.dotNet = true;
		
		FutureTask<List<String>> task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				httpTransportSE.call(SERVICE_NS + methodName, envelope);
				if(null != envelope.getResponse()){
					SoapObject result = (SoapObject)envelope.bodyIn;
					SoapObject detail = (SoapObject)result.getProperty(methodName + "Result");
					return parseProvinceOrCity(detail);
				}
				return null;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> GetCityListByProvince(String province){
		final String methodName = "getSupportCityString";
		
		final HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
		httpTransportSE.debug = true;
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);//Soap1.1
		SoapObject outSoapObject = new SoapObject(SERVICE_NS, methodName);
		outSoapObject.addProperty("theRegionCode", province);
		envelope.bodyOut = outSoapObject;
		envelope.dotNet = true;
		
		FutureTask<List<String>> task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				httpTransportSE.call(SERVICE_NS + methodName, envelope);
				if(null != envelope.getResponse()){
					SoapObject result = (SoapObject)envelope.bodyIn;
					SoapObject detail = (SoapObject)result.getProperty(methodName + "Result");
					return parseProvinceOrCity(detail);
				}
				return null;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<String> parseProvinceOrCity(SoapObject detail){
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < detail.getPropertyCount(); i++) {
			result.add(detail.getProperty(i).toString().split(",")[0]);
		}
		return result;
	}
	*/
	
}
