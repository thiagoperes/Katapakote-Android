package com.example.listactivity;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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
	    myMapController.setZoom(7);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.pino);
	    PackageItemizedOverlay itemizedoverlay = new PackageItemizedOverlay(drawable, this);
	   
	    mapOverlays.add(itemizedoverlay);
	    
	    GeoPoint point = new GeoPoint((int)(-22.9795 * 1E6), (int)(-43.2222 * 1E6));
	    OverlayItem overlayitem = new OverlayItem(point, "Em andamento", "");
	    itemizedoverlay.addOverlay(overlayitem);
	    
	    GeoPoint point2 = new GeoPoint((int)(-22.29 * 1E6), (int)(-43.10 * 1E6));
	    OverlayItem overlayitem2 = new OverlayItem(point2, "Encaminhado", "");
	    itemizedoverlay.addOverlay(overlayitem2);
	    
	    GeoPoint point3 = new GeoPoint((int)(-20.226 * 1E6), (int)(-41.036 * 1E6));
	    OverlayItem overlayitem3 = new OverlayItem(point3, "Encaminhado", "");
	    itemizedoverlay.addOverlay(overlayitem3);
	}

}
