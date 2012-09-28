package com.example.listactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class AddPackageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_package);

        final EditText codeET = (EditText)findViewById(R.id.codeText);
        final EditText nameET = (EditText)findViewById(R.id.nameText);
        
        Button btn = (Button)findViewById(R.id.button2);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentIntegrator integrator = new IntentIntegrator(AddPackageActivity.this);
				integrator.initiateScan();
			}
		});
        
        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AddPackageActivity.this);
				builder.setCancelable(true)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.cancel();
				           }
				       });
				       /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });*/				
				// TODO Auto-generated method stub
				if(codeET.length() < 13)
				{
					builder.setMessage("O codigo deve ter 13 caracteres.");
					AlertDialog alert = builder.create();
					alert.show();
					return;
				}
				if(nameET.length() == 0)
				{
					builder.setMessage("O nome n‹o pode ser vazio.");
					AlertDialog alert = builder.create();
					alert.show();
					return;
				}
				
				if (SessionManager.addPackage(nameET.getText().toString(), codeET.getText().toString().toUpperCase()) == 0)
				{
					Intent data = new Intent();
					setResult(RESULT_OK, data);
					finish();
				}
				else
				{
					builder.setMessage("Houve um problema ao adicionar o pakote");
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_package, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
  	  if (scanResult != null) {
  		  EditText et = (EditText)findViewById(R.id.codeText);
  		  et.setText(scanResult.getContents());
  	    // handle scan result
  	  }
    }
}
