package com.example.listactivity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MainActivity extends ListActivity {
	
	private static final int DETAIL_ACTIVITY = 1;
	private static final int ADD_ACTIVITY = 2;
	
	private void refreshPackageList() {
		SessionManager.initDB(this.getApplicationContext());
		
		setListAdapter(new ListAdapter(this, SessionManager.getAllPackages()));
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
                
        refreshPackageList();
        
        SessionManager.requestForPackage("RU467405286CH");
        
        this.getListView().setOnItemClickListener(new OnItemClickListener() {

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
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.add(Menu.NONE, 0, 0, "Remover pakote");
      }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = info.position;
            
      ListAdapter tmpAdapter = (ListAdapter)getListAdapter();
      String trackingCode = tmpAdapter.getItem(menuItemIndex).get("trackingCode");
      
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
    
    private class ListAdapter extends BaseAdapter {
    	private Context context; // needed to create the view
    	private ArrayList<HashMap<String, String>> packages;
    	
		public ListAdapter(Context c, ArrayList<HashMap<String, String>> list) {
			context = c;
			packages = list;
		}

		public int getCount() {
			return packages.size(); // TODO: Implementar contador
		}

		public HashMap<String, String> getItem(int position) {
			return packages.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder vh;

			if (convertView == null) {

				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.package_cell, null);
				vh.packageName = (TextView)convertView.findViewById(R.id.packageNameTV);
				vh.trackingCode = (TextView)convertView.findViewById(R.id.trackingCodeTV);
				vh.status = (TextView)convertView.findViewById(R.id.statusTV);
				vh.address = (TextView)convertView.findViewById(R.id.addressTV);

				convertView.setTag(vh);

			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			// acessa e coloca os valores
			HashMap<String, String> tmp = getItem(position);
			
			vh.packageName.setText(tmp.get("name").toString());
			vh.trackingCode.setText(tmp.get("trackingCode").toString());
			vh.status.setText(tmp.get("status").toString());
			vh.address.setText(tmp.get("address").toString());
			
			return convertView;
		}

		class ViewHolder {
			TextView packageName;
			TextView trackingCode;
			TextView status;
			TextView address;
		}
    }
}
