package com.ernest.mobilecontextaware.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorsUtil implements SensorEventListener{
	private static final String TAG = "SensorsUtil";
	
	private static final int SENSOR_DELAY = 1000 * 1000 * 10; //10s
	private SensorManager mSensorManager;
	private Context mContext;
	
	public SensorsUtil(Context context){
		mContext = context;
		mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
	}
	
	public void registerAllSensorListener(){
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SENSOR_DELAY);
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SENSOR_DELAY);
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY);
	}
	
	public void unRegisterSensorListener(){
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.i(TAG, "onSensorChanged()");
		float[] values = event.values;
		int sensorType = event.sensor.getType();
		switch (sensorType) {
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			SharedDataUtil.WriteCurTemperature(mContext, values[0]);
			break;
		case Sensor.TYPE_LIGHT:
			SharedDataUtil.WriteCurLight(mContext, values[0]);
		case Sensor.TYPE_ACCELEROMETER:
			SharedDataUtil.WriteCurAcceleration(mContext, values);
			break;
		default:
			break;
		}
	}

}
