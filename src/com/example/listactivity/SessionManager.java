package com.example.listactivity;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Xml;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SessionManager {
	private static DB dbHelper;
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static final String BASE_URL = "http://services.encomendaz.net/tracking.json?id=";
	
	static void initDB(Context context) {
		if (dbHelper == null)
		{
			dbHelper = new DB(context);
		}
	}
	
	static void startDB() {
		try {
        	dbHelper.createDataBase();
        } catch (IOException ioe) {
        	throw new Error("Unable to create database");
        }
        try {
        	dbHelper.openDataBase();
        }catch(SQLException sqle){
        	throw sqle;
        }
	}
	
	static ArrayList<HashMap<String, String>> getAllPackages() {

        SessionManager.startDB();
        
        ArrayList<HashMap<String, String>> packages = new ArrayList<HashMap<String, String>>();
        
        Cursor c = dbHelper.getPackages();
        c.moveToFirst();
        while(!c.isAfterLast())
        {
        	HashMap<String, String> tmp = new HashMap<String, String>();
        	tmp.put("trackingCode", c.getString(1));
        	tmp.put("name", c.getString(2));
        	tmp.put("status", "vamos l‡");
        	tmp.put("address", "uhul");
        	packages.add(tmp);
        	
        	c.moveToNext();
        }
        c.close();
        dbHelper.close();
        return packages;
	}
	
	static int addPackage(String packageName, String trackingCode) {
		SessionManager.startDB();
		
		String ret = dbHelper.addPackage(packageName, trackingCode);
		dbHelper.close();

		return (ret == "ok") ? 0 : -1;		
	}
	
	static int removePackage(String trackingCode)
	{
		SessionManager.startDB();
		int ret = dbHelper.removePackage(trackingCode);
		dbHelper.close();
		
		return ret;
	}
		
	static void parseRequest(String requestResponse)
	{
		try {
			JSONObject obj = new JSONObject(requestResponse);
			System.out.println(obj);
			JSONArray statuses = obj.getJSONArray("data");
			
			if (statuses.length() == 0) return;
			
			// open db connection
			for (int i = 0; i < statuses.length(); i++) 
			{
				
			}
			// close db connection
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	static void requestForPackage(String trackingCode) {	
		
		System.out.println("hello");
		
		client.get(BASE_URL + trackingCode, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println(response);
		        parseRequest(response);
		    }
		    @Override
		     public void onFailure(Throwable e, String response) {
		         // Response failed :(
		    	System.out.println("response failed");
		     }

		     @Override
		     public void onFinish() {
		         // Completed the request (either success or failure)
		     }
		});
	}
	
	static void refreshAllPackages() {
		
	}
}
