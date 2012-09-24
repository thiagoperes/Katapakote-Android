package com.example.listactivity;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SessionManager {
	private static DB dbHelper;
	private static AsyncHttpClient client = new AsyncHttpClient();
	
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
	
	static ArrayList<String> getAllPackages() {

        SessionManager.startDB();
        
        ArrayList<String> packages = new ArrayList<String>();
        
        Cursor c = dbHelper.getPackages();
        c.moveToFirst();
        while(!c.isAfterLast())
        {
        	packages.add(c.getString(1));
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
	
	static void postRequest() {
		RequestParams params = new RequestParams();
		params.put("Usuario", "KRAFT12345");
		params.put("Senha", "PUVR3AS1=D");
		params.put("Tipo", "L");
		params.put("Resultado", "T");
		params.put("Objetos", "RA650936674CN");
		
		System.out.println("REQUEST STARTED");
		client.post("http://websro.correios.com.br/sro_bin/sroii_xml.eventos", params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        System.out.println(response);
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
}
