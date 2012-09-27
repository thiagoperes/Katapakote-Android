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
	
	static void initDB(Context context) 
	{
		if (dbHelper == null)
		{
			dbHelper = new DB(context);
		}
	}
	
	static void startDB() 
	{
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
	
	static ArrayList<HashMap<String, String>> getHashMapForRawQuery(String query)
	{
		startDB();
		
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        
        Cursor c = dbHelper.myDataBase.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast())
        {
        	HashMap<String, String> tmp = new HashMap<String, String>();
        	for(int i = 0; i < c.getColumnCount(); i++)
        	{
        		tmp.put(c.getColumnName(i), c.getString(i));
        	}
        	result.add(tmp);
        	
        	c.moveToNext();
        }
        
        c.close();
        dbHelper.close();
        
        return result;
	}
	
	static ArrayList<HashMap<String, String>> getAllPackages() 
	{
        return getHashMapForRawQuery("select * from packages");
	}
	
	static ArrayList<HashMap<String, String>> getStatusesForPackage(String trackingCode) 
	{
		String query = "SELECT * from packages, statuses WHERE tracking_code = '"+trackingCode+"' AND id_package = packages._id AND description IS NOT NULL";
		return getHashMapForRawQuery(query);
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
		
	static void parseRequest(String requestResponse, String trackingCode)
	{
		try {
			JSONObject obj = new JSONObject(requestResponse);
			JSONArray statuses = obj.getJSONArray("data");
			
			//System.out.println(statuses);
			
			if (statuses.length() == 0) return;
			
			startDB();
			String query = "insert into statuses ( id_package, uf, city, date, description ) values ";
			
			// Get package id
			int packageId = -1;
			
			String q = "select * from packages where tracking_code = '" + trackingCode + "'";
			//System.out.println(q);
			Cursor c = dbHelper.myDataBase.rawQuery(q, null);
			if (c.getCount() != 1)
			{
				System.out.println("Could not find a package matching tracking code: "+trackingCode);
				c.close();
				return;
			}
			else
			{
				c.moveToFirst();
				packageId = c.getInt(0);
			}
			c.close();
			
			// END: Get package id
			
			for (int i = 0; i < statuses.length(); i++) 
			{
				String values = String.format("( %d, ", packageId);
				JSONObject status = (JSONObject) statuses.get(i);
				
				if(!status.has("date"))
				{
					continue;
				}
				
				// Checks if a s has already been added
				String tmpQuery = "select * from packages as p, statuses as s where p.tracking_code = '"+trackingCode+"' and p._id = s.id_package and s.date = '"+status.getString("date")+"'";
				c = dbHelper.myDataBase.rawQuery(tmpQuery, null);
				if(c.getCount() > 0)
				{
					c.close();
					//System.out.println("Status already added, moving on...");
					continue;
				}
				c.close();
				
				values = values + ( (status.has("state") ? String.format("'%s', ", status.get("state")) : "null, " ) );
				values = values + ( (status.has("city") ? String.format("'%s', ", status.get("city")) : "null, " ) );
				values = values + ( (status.has("date") ? String.format("'%s', ", status.get("date")) : "null, " ) );
				values = values + ( (status.has("description") ? String.format("'%s'", status.get("description")) : "null" ) );
				values += " )";
				
				String finalQuery = query + values;
				
				dbHelper.myDataBase.execSQL(finalQuery);
			}
			
			// Print statuses
			c = dbHelper.myDataBase.rawQuery("select * from statuses", null);
			/*c.moveToFirst();
			while(!c.isAfterLast())
	        {
				System.out.println(c.getString(1) + "< this");
	        	c.moveToNext();
	        }*/
			System.out.printf("%d statuses so far\n", c.getCount());
	        c.close();
			
			dbHelper.close();
			// close db connection
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	static void requestForPackage(final String trackingCode) {	
				
		String url = BASE_URL + trackingCode;
		System.out.println("Requesting for package: " + url);
		
		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println("Request succeeded");
		        parseRequest(response, trackingCode);
		    }
		    @Override
		     public void onFailure(Throwable e, String response) {
		         // Response failed :(
		    	System.out.println("Request failed");
		     }

		     @Override
		     public void onFinish() {
		         // Completed the request (either success or failure)
		     }
		});
	}
	
	static void refreshAllPackages() {
		ArrayList<HashMap<String, String>> p = getAllPackages();
		for (HashMap<String, String> h : p)
		{
			requestForPackage(h.get("tracking_code"));
		}
	}
}
