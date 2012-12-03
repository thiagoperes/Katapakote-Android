package com.example.listactivity;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class PackageMapActivity extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	  	
	    double lat = -22.9859;
	    double lng = -43.2333;

	    GeoPoint gp = new GeoPoint((int)(lat * 1E6), (int)(lng * 1E6));
	    MapController myMapController = mapView.getController();
	    myMapController.setCenter(gp);
	    myMapController.setZoom(11);
	}

}
