package com.example.listactivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PackageService extends Service {

   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public void onCreate() {
      //code to execute when the service is first created
   }

   @Override
   public void onDestroy() {
      //code to execute when the service is shutting down
   }

   @Override
   public void onStart(Intent intent, int startid) {
      //code to execute when the service is starting up
	   SessionManager.refreshAllPackages();
   }
}