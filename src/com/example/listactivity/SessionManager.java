package com.example.listactivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

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
	
	private static void startDB() 
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
	
	//
	//	Makes a SQL query and converts the result into an ArrayList of hashmaps 
	//
	private static ArrayList<HashMap<String, String>> getHashMapsForQuery(String query)
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
	
	//
	// Gets all package information with the last status aswell
	//
	static ArrayList<HashMap<String, String>> getAllPackages() 
	{
		ArrayList<HashMap<String, String>> tmp  = getHashMapsForQuery("select * from packages");
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String,String>>();
		for(HashMap<String, String> hash : tmp)
		{
			HashMap<String, String> lastStatus = getLastStatusForPackage(hash.get("tracking_code"));
			if(lastStatus != null)
			{
				hash.putAll(lastStatus);
				ret.add(hash);
			}
		}
		
		System.out.println("printing stuff "+ret);
        return ret;
	}
	
	//
	// Gets all status information for a given package
	//
	static ArrayList<HashMap<String, String>> getStatusesForPackage(String trackingCode) 
	{
		String query = "SELECT * from packages, statuses WHERE tracking_code = '"+trackingCode+"' AND id_package = packages._id AND description IS NOT NULL";		
		return getHashMapsForQuery(query);
	}
	
	//
	// Gets the single most recent status information for a given package
	//
	static HashMap<String, String> getLastStatusForPackage(String trackingCode)
	{
		String query = "SELECT date FROM statuses AS s, packages AS p WHERE p._id = s.id_package and p.tracking_code = '"+trackingCode+"'";
		ArrayList<HashMap<String, String>> tmp = getHashMapsForQuery(query);
		Map<Date, String> dates = new TreeMap<Date, String>();
		
		for(HashMap<String, String> s : tmp)
		{
			SimpleDateFormat f = new SimpleDateFormat("EEE, MMM dd HH:mm:ss ZZZZ yyyy");
			Date d = null;
			try {
				d = f.parse(s.get("date"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//System.out.println("parsed date correctly " + d);
			
			dates.put(d, s.get("date"));
		}
		
		Object[] tmpA = dates.values().toArray();
		
		if(tmpA.length == 0)
		{
			return null;
		}
		
		String z = (String)tmpA[0];
		
		System.out.println(z);
		
		String query1 = String.format("SELECT description FROM statuses AS s, packages AS p WHERE date = '%s' AND p._id = s.id_package and p.tracking_code = '%s'", z, trackingCode);
				
		return getHashMapsForQuery(query1).get(0);
	}
	
	//
	// Adds a package to the database
	//
	static int addPackage(String packageName, String trackingCode) {
		SessionManager.startDB();
		
		String ret = dbHelper.addPackage(packageName, trackingCode);
		dbHelper.close();

		return (ret == "ok") ? 0 : -1;		
	}
	
	//
	// Removes a package from the database
	//
	static int removePackage(String trackingCode)
	{
		SessionManager.startDB();
		int ret = dbHelper.removePackage(trackingCode);
		dbHelper.close();
		
		return ret;
	}
	
	//
	// Parses status request from JSON and adds to the database 
	//
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
	
	//
	// Asynchronously requests status information for a single package
	//
	static void requestForPackage(final String trackingCode) 
	{	
		String url = BASE_URL + trackingCode;
		System.out.printf("Request for package - %s", url);
		
		client.get(url, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.printf("Request succeeded - %s", trackingCode);
		        parseRequest(response, trackingCode);
		    }
		    @Override
		     public void onFailure(Throwable e, String response) {
		    	System.out.printf("Request failed - %s", trackingCode);
		     }

		     @Override
		     public void onFinish() {
		         // Completed the request (either success or failure)
		     }
		});
	}
	
	//
	// Requests information for all packages
	//
	static void refreshAllPackages() 
	{
		ArrayList<HashMap<String, String>> p = getAllPackages();
		for (HashMap<String, String> h : p)
		{
			requestForPackage(h.get("tracking_code"));
		}
	}
}
