package com.example.listactivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private static final int DETAIL_ACTIVITY = 1;
	private static final int ADD_ACTIVITY = 2;
	private static ArrayAdapter<String> adapter;
	
	private void refreshPackageList() {
		SessionManager.initDB(this.getApplicationContext());
		
		if (adapter == null)
		{
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SessionManager.getAllPackages());
			setListAdapter(adapter);
			return;
		}
		adapter.clear();
		
		for (String s : SessionManager.getAllPackages())
		{
			adapter.add(s);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        SessionManager.postRequest();
        
        refreshPackageList();
        
        this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "Item " + position, Toast.LENGTH_LONG).show();
				
				Intent i = new Intent(MainActivity.this, DetailActivity.class);
				startActivityForResult(i, DETAIL_ACTIVITY);
			}
		});
        registerForContextMenu(this.getListView());
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, AddPackageActivity.class);
				startActivityForResult(i, ADD_ACTIVITY);
			}
		});
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      if (v.getId() == this.getListView().getId()) 
      {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        //menu.setHeaderTitle("Opções");
        menu.add(Menu.NONE, 0, 0, "Remover pakote");
      }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = info.position;
            
      String trackingCode = getListAdapter().getItem(menuItemIndex).toString();
      
      if (SessionManager.removePackage(trackingCode) == 0)
      {
          Toast.makeText(this, String.format("Falha ao remover pakote %d - %s", menuItemIndex, trackingCode), Toast.LENGTH_SHORT).show();
      }
      else
      {
    	  Toast.makeText(this, "Pakote " + trackingCode + " removido", Toast.LENGTH_SHORT).show();
      }
      refreshPackageList();
      
      return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == RESULT_OK && requestCode == ADD_ACTIVITY) 
    	{
    		refreshPackageList();
    	}
    }
}
