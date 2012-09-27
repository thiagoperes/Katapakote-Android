package com.example.listactivity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DetailActivity extends ListActivity {

	private static ArrayList<HashMap<String, String>> list = null;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);
        
        String trackingCode = getIntent().getStringExtra("trackingCode");
        
        list = SessionManager.getStatusesForPackage( trackingCode );
        
        
        View header = View.inflate(this, R.layout.detail_header, null);
        
        TextView tv = (TextView)header.findViewById(R.id.trackindCodeTV);
        tv.setText(trackingCode);
        tv = (TextView)header.findViewById(R.id.packageNameTV);
        tv.setTag(getIntent().getStringExtra("name"));
        
        getListView().addHeaderView(header);
        
        ArrayList<String> tmp = new ArrayList<String>();
        for(HashMap<String, String> h : list)
        {
        	tmp.add(h.get("description"));
        }
        
        if (tmp.isEmpty())
        {
        	tmp.add("Sem informações");
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tmp);
        getListView().setAdapter(adapter);
    }
}
