package com.ernest.mobilecontextaware.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ernest.mobilecontextaware.R;
import com.ernest.mobilecontextaware.util.BaiduLocationUtil;
import com.ernest.mobilecontextaware.util.BaiduMapUtil;

public class BaiduMapTest extends Activity{
	private final static String TAG = "BaiduMapTest";
	
	private final static double LAT_BEIJING = 39.915f;
	private final static double LON_BEIJING = 116.404f;
	private final static int NEARBY_RADIUS = 5000;
	
	private ContextAwareApplicaiton mApp;
	private BaiduLocationUtil mBaiduLocationUtil;
	private BMapManager mBMapManager;
	private PopupMapView mMapView;
	private MapController mMapController;
	private MKSearch mMKSearch;
	private AutoCompleteTextView mAutoCompleteTextView;
	private ArrayAdapter<String> mSuggestArrayAdapter;
	private BDLocation mCurrentLocation;
	private GeoPoint mCurrentPoint;
	private Button mCitySearchButton, mNearbySearchButton, mMyLocationButton;
	private TextView mPopupTextView;
	private View mPopupInfo, mPopupLeft, mPopupRight;
	private PopupOverlay mPopupOverlay;
	private LocationOverlay mLocationOverlay;
	private MyItemizedOverlay mItemizedOverlay;
	private OverlayItem mCurrentItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "OnCreate() start");
		
		//Initial global BMapManager before using BaiduMap SDK.
		mApp = (ContextAwareApplicaiton)getApplication();
		if (null == (mBMapManager = mApp.getBMapManager())) {
			mBMapManager = new BMapManager(getApplicationContext());
			mBMapManager.init(mApp.getKey(), new ContextAwareApplicaiton.MyMKGeneralListener());
			mApp.setBMapManager(mBMapManager);
        }
		mApp.setLoactionHandler(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == BaiduLocationUtil.NEW_LOC_MSG){ processOnReceiveNewLocation(); }
			}
		});
		
		//BMapManager must be initiated before setContentView(), or it goes wrong
		setContentView(R.layout.baidumaptest_main);
		mMapView = (PopupMapView)findViewById(R.id.baidumapsView);
		mMapController = mMapView.getController();
		GeoPoint defaultpoint = new GeoPoint((int)(LAT_BEIJING * 1E6), (int)(LON_BEIJING * 1E6));
		initMapView(mMapView, defaultpoint);
		
//		mCitySearchButton = (Button)findViewById(R.id.citysearchbutton);
//		mCitySearchButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mMKSearch.poiSearchInCity(mCurrentLocation.getCity(),
//						mAutoCompleteTextView.getText().toString());
//			}
//		});
		mNearbySearchButton = (Button)findViewById(R.id.nearbysearchbutton);
		mNearbySearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMKSearch.poiSearchNearBy(mAutoCompleteTextView.getText().toString(), 
						mCurrentPoint, NEARBY_RADIUS);
			}
		});
		mMyLocationButton = (Button)findViewById(R.id.mylocationbutton);
		mMyLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(0 != mBaiduLocationUtil.requetLocation()) Log.i(TAG, "Request failed.");
				Toast.makeText(BaiduMapTest.this, "Locating...", Toast.LENGTH_LONG).show();
			}
		});
		mAutoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.map_autocompletetextview);
		mSuggestArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mAutoCompleteTextView.setAdapter(mSuggestArrayAdapter);
		mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() <= 0) return;
				mMKSearch.suggestionSearch(s.toString(), mCurrentLocation.getCity());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void afterTextChanged(Editable s) { }
		});
		mPopupTextView = (TextView)getLayoutInflater().inflate(R.layout.popupview_with_1, 
				null).findViewById(R.id.popuptextview);
		mPopupOverlay = new PopupOverlay(mMapView, new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {//at most 3 clickable area
				if(index == 0){
					mPopupOverlay.hidePop();
//					mCurrentItem.setGeoPoint(new GeoPoint(mCurrentItem.getPoint().getLatitudeE6()+5000,
//							mCurrentItem.getPoint().getLongitudeE6()+5000));
//					mItemizedOverlay.updateItem(mCurrentItem);
					mMapView.refresh();
				}else if(index == 2){
//					mCurrentItem.setMarker(getResources().getDrawable(R.drawable.nav_turn_via_1));
//					mItemizedOverlay.updateItem(mCurrentItem);
//				    mMapView.refresh();
				}
			}
		});
		mMapView.setPopupOverlay(mPopupOverlay);
		
		mBaiduLocationUtil = new BaiduLocationUtil(getApplicationContext());

		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiResult(MKPoiResult result, int type, int error) {
				if(0 != error || null == result){
					Toast.makeText(BaiduMapTest.this, "Sorry, search mistake.", Toast.LENGTH_LONG).show();
					return;
				}else if(error == MKEvent.ERROR_RESULT_NOT_FOUND){
					Toast.makeText(BaiduMapTest.this, "Sorry, no results.", Toast.LENGTH_LONG).show();
					return;
				}
				//move Map focus to the first POI
				if(result.getCurrentNumPois() > 0){
					MyPoiOverlay poiOverLay = new MyPoiOverlay(BaiduMapTest.this, mMapView, mMKSearch);
					poiOverLay.setData(result.getAllPoi());	
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(mLocationOverlay);
					mMapView.getOverlays().add(poiOverLay);
					mMapView.refresh();
					//when the POI type equals 2(bus) or 4(subway), coordinate of POI is null
                    for( MKPoiInfo info : result.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
				}else if(result.getCityListNum() > 0) {
					//if no result found in the city but in other cities, return these cities
                    String strInfo = "Result found in other cities: ";
                    for (int i = 0; i < result.getCityListNum(); i++) {
                        strInfo += result.getCityListInfo(i).city;
                        strInfo += ",";
                    }
                    Toast.makeText(BaiduMapTest.this, strInfo, Toast.LENGTH_LONG).show();
                }
			}
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				if(0 != error){
					Toast.makeText(BaiduMapTest.this, "Sorry, no results.", Toast.LENGTH_SHORT).show();
					return;
				}else{
					Toast.makeText(BaiduMapTest.this, "Search success.", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult result, int error) {
				if (result == null || result.getAllSuggestions() == null) return;
				mSuggestArrayAdapter.clear();
				for(MKSuggestionInfo info : result.getAllSuggestions()){
					if(null != info.key){
						mSuggestArrayAdapter.add(info.key);
					}
				}
				mSuggestArrayAdapter.notifyDataSetChanged();
			}
			@Override
			public void onGetAddrResult(MKAddrInfo result, int error) {
				if(0 != error){
					Toast.makeText(BaiduMapTest.this, "Search error: " + error, Toast.LENGTH_SHORT).show();
					return;
				}
				GeoPoint point = result.geoPt;
				mMapController.animateTo(point);  
			    if (result.type == MKAddrInfo.MK_GEOCODE) {    
			        Toast.makeText(BaiduMapTest.this, 
			        		"Latitude: " + point.getLatitudeE6() / 1E6 + ", Longtitude: " + point.getLongitudeE6() / 1E6, 
			        		Toast.LENGTH_LONG).show();  
			    }  
			    if (result.type == MKAddrInfo.MK_REVERSEGEOCODE) {  
			        Toast.makeText(BaiduMapTest.this, result.strAddr, Toast.LENGTH_LONG).show();  
			    } 
			}
			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type, int error) { }
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult result, int error) { }
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult result, int error) { }
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult result, int error) { }
			@Override
			public void onGetBusDetailResult(MKBusLineResult result, int error) { }
		});
	}
	
	@Override
	protected void onResume() {
		mMapView.onResume();
		if(null != mBMapManager) mBMapManager.start();  
		mBaiduLocationUtil.start();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		if(null != mBMapManager) mBMapManager.stop();
		mBaiduLocationUtil.stop();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		if(null != mBMapManager) mBMapManager.stop();
		mBaiduLocationUtil.stop();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		mMapView = null;
		mMKSearch.destory();
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		mMapView.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}
	
	private void processOnReceiveNewLocation(){
		if(null == mMapController) return;
		mCurrentLocation = mBaiduLocationUtil.getCurLocation();
		//micro degree( = 1 degree * 10^6)
		mCurrentPoint = new GeoPoint((int)(mCurrentLocation.getLatitude() * 1E6), 
					(int)(mCurrentLocation.getLongitude() * 1E6));
		mLocationOverlay = new LocationOverlay(mMapView);
		mLocationOverlay.setLocationMode(MyLocationOverlay.LocationMode.NORMAL);
		LocationData locationData = new LocationData();
		locationData.latitude = mCurrentLocation.getLatitude();
		locationData.longitude = mCurrentLocation.getLongitude();
		locationData.accuracy = mCurrentLocation.getRadius();
		locationData.direction = mCurrentLocation.getDerect();
		mLocationOverlay.setData(locationData);
		mLocationOverlay.enableCompass();
		//mLocationOverlay.setMarker(arg0);
		mMapView.getOverlays().add(mLocationOverlay);
		
//		mItemizedOverlay = new MyItemizedOverlay(getResources().getDrawable(R.drawable.icon_marka), 
//				mMapView);
//		initItemizedOverlay();
		
		mMapView.refresh();
		mMapController.setCenter(mCurrentPoint);
		mMapController.setZoom(12);
		mMapController.animateTo(mCurrentPoint);
	}
	
	private void initMapView(MapView mapView, GeoPoint geoPoint){
		mapView.setLongClickable(true);
		mapView.getController().setCenter(geoPoint);
		mapView.getController().setZoom(12);
		mapView.getController().enableClick(true);
		mapView.setBuiltInZoomControls(true);
	}
	
//	private void initItemizedOverlay(){
//		OverlayItem item0 = new OverlayItem(new GeoPoint(mCurrentPoint.getLatitudeE6() + 1000, 
//				mCurrentPoint.getLongitudeE6() + 1000), "Overlay0", "");
//		OverlayItem item1 = new OverlayItem(new GeoPoint(mCurrentPoint.getLatitudeE6() + 3000, 
//				mCurrentPoint.getLongitudeE6() + 3000), "Overlay1", "");
//		OverlayItem item2 = new OverlayItem(new GeoPoint(mCurrentPoint.getLatitudeE6() + 6000, 
//				mCurrentPoint.getLongitudeE6() + 6000), "Overlay2", "");
//		OverlayItem item3 = new OverlayItem(new GeoPoint(mCurrentPoint.getLatitudeE6() + 6000, 
//				mCurrentPoint.getLongitudeE6() + 6000), "Overlay3", "");
//		item1.setMarker(getResources().getDrawable(R.drawable.icon_markb));
//		item2.setMarker(getResources().getDrawable(R.drawable.icon_markc));
//		item3.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
//		mItemizedOverlay.addItem(item0);
//		mItemizedOverlay.addItem(item1);
//		mItemizedOverlay.addItem(item2);
//		mItemizedOverlay.addItem(item3);
//		mMapView.getOverlays().add(mItemizedOverlay);
//		View v = getLayoutInflater().inflate(R.layout.popupview_with_3, null);
//        mPopupInfo = (View) v.findViewById(R.id.popinfo);
//        mPopupLeft = (View) v.findViewById(R.id.popleft);
//        mPopupRight = (View) v.findViewById(R.id.popright);
//	}
	
	private class MyPoiOverlay extends PoiOverlay{
		private MKSearch mSearch;	
		public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search){
			super(activity, mapView);
			mSearch = search;
		}
		@Override
		protected boolean onTap(int i) {
			super.onTap(i);
			MKPoiInfo poiInfo = this.getPoi(i);
			if(poiInfo.hasCaterDetails){
				mSearch.poiDetailSearch(poiInfo.uid);
			}
			return true;
		}
	}
	
	private class LocationOverlay extends MyLocationOverlay{
		public LocationOverlay(MapView mapView) {
			super(mapView);
		}
		@Override
		protected boolean dispatchTap() {
			mPopupOverlay.showPopup(BaiduMapUtil.getBitmapFromView(mPopupTextView), 
						mCurrentPoint, 8);
  			return true;
		}
	}
	
	private class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
		public MyItemizedOverlay(Drawable defaultMarker, MapView mapView){
			super(defaultMarker, mapView);
		}
		@Override
		protected boolean onTap(int index) {
			mCurrentItem = getItem(index) ;
			if (index == 3){
				mPopupTextView.setText("A special view");
				mPopupOverlay.showPopup(BaiduMapUtil.getBitmapFromView(mPopupTextView), 
						new GeoPoint(mCurrentItem.getPoint().getLatitudeE6(),
								mCurrentItem.getPoint().getLongitudeE6()), 32);
			}
			else{
			   mPopupTextView.setText(getItem(index).getTitle());
			   Bitmap[] bitMaps={BaiduMapUtil.getBitmapFromView(mPopupLeft), 		
				    BaiduMapUtil.getBitmapFromView(mPopupInfo), 		
				    BaiduMapUtil.getBitmapFromView(mPopupRight)};
			   mPopupOverlay.showPopup(bitMaps, mCurrentItem.getPoint(), 32);
			}
			return true;
		}
		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			if (mPopupOverlay != null){
                mPopupOverlay.hidePop();
                mMapView.removeView(mPopupTextView);
			}
			return false;
		}
	}
	
}

class PopupMapView extends MapView{
	private PopupOverlay mPopupOverlay;
	public PopupMapView(Context context) {
		super(context);
	}
	public PopupMapView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public PopupMapView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!super.onTouchEvent(event)){
			if (mPopupOverlay != null && event.getAction() == MotionEvent.ACTION_UP){
//				mPopupOverlay.hidePop();
			}
		}
		return true;
	}
	public void setPopupOverlay(PopupOverlay popupOverlay){
		mPopupOverlay = popupOverlay;
	}
}
