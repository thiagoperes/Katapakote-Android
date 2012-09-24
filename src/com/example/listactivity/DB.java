package com.example.listactivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
	
	//The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.listactivity/databases/";
 
    private static String DB_NAME = "katapakote";
 
    public SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    public DB(Context context) {
    	super(context, DB_NAME, null, 1);    	
        this.myContext = context;
    }	
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	
	public void createDataBase() throws IOException{		 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{ 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();

        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }
	
	private boolean checkDataBase() {
		/*
		 * File dbFile = new File(DB_PATH + DB_NAME);
    return dbFile.exists();
		 */
		SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
	
	private void copyDataBase() throws IOException{
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open("katapakote.db");
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	//myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	myDataBase = SQLiteDatabase.openOrCreateDatabase(myPath, null);
    }
    
    @Override
	public synchronized void close() {
    	    if(myDataBase != null)
    		    myDataBase.close();
    	    super.close();
	}
    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
    public Cursor getPackages() {
    	return myDataBase.query("packages", null, null, null, null, null, null);
    }
    
    public String addPackage(String packageName, String trackingCode) {
    	try {
    		ContentValues values = new ContentValues();
            values.put("name", packageName); // Contact Name
            values.put("tracking_code", trackingCode); // Contact Phone Number
        	
        	myDataBase.insertOrThrow("packages", null, values);
        	
        	return "ok";
		} catch (SQLException e) {
			// TODO: handle exception
			System.out.println(e.toString());
			return e.toString();
		}
    }
    
    public int removePackage(String trackingCode) {    	
    	return myDataBase.delete("packages", "tracking_code=?", new String[] {trackingCode});
    }
}
