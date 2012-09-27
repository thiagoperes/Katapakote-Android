package com.example.listactivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DetailActivity extends ListActivity {

	private static ArrayList<HashMap<String, String>> list = null;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
                
        System.out.println(getIntent().getStringExtra("trackingCode"));
        
        list = SessionManager.getStatusesForPackage( getIntent().getStringExtra("trackingCode") );
        
        System.out.println(list);
        
        ArrayList<String> tmp = new ArrayList<String>();
        for(HashMap<String, String> h : list)
        {
        	tmp.add(h.get("description"));
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tmp);
        getListView().setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }
}
