package com.gatonimo.gatonimosrecall;



import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
    
	private static final int PICK_CONTACT = 1;
	private static final String LOG_TAG = null;
	private boolean hizoClick=false;
	private CheckBox check;
	private EditText tel;
	private String number;
	private boolean recall;
	private boolean paused=false;
	private EndCallListener callListener;
	private TelephonyManager mTM;
	private SharedPreferences pref ;
	private Editor editor;
	private boolean isPhoneCalling;
	private TextView TextSegs,TextRecall;
	private Handler handler;
	protected int i=5;
	
	

	
	
	/*public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    isRecall = ((CheckBox) ).isChecked();*/

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "NCREATE");
        
        
        handler=new Handler();
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); 
        editor = pref.edit();
        
        number=pref.getString("1", null);
        recall=pref.getBoolean("2", false);
        isPhoneCalling=pref.getBoolean("3",false);
        InicializaUI();
        CreaListener();
        
        if(check.isChecked() && isPhoneCalling){
        	TextRecall.setText(pref.getString("4", null));
        	ReCall();
        }
        	
        

    }
	
	private void Borra() {
		Log.i(LOG_TAG, "BORRAAA");
		mTM.listen(callListener, PhoneStateListener.LISTEN_NONE);
		
		
	}

	private void CreaListener() {
		
			Log.i(LOG_TAG, "CREAAA");
			callListener = new EndCallListener(this);
			mTM = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
			
		
	}

	private void InicializaUI() {
		//Widgets
        Button buttonPickContact = (Button)findViewById(R.id.pickcontact);
        tel=(EditText)findViewById(R.id.telefono);
        final CheckBox check=(CheckBox)findViewById(R.id.checkRecall);
        this.check=check;
        TextSegs=(TextView)findViewById(R.id.textSegs);
        TextRecall=(TextView)findViewById(R.id.textRecall);
        
        check.setChecked(recall);
        tel.setText(number);
        
        check.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(recall == true){
					recall=false;
					TextRecall.setText("Check to redial");
				}
				else{
					recall=true;
					TextRecall.setText("Uncheck to stop");
					Call();
				}
					
				
			}
		});
        
        buttonPickContact.setOnClickListener(new Button.OnClickListener(){

			   @Override
			    public void onClick(View arg0) {
			   // TODO Auto-generated method stub


			   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			   intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
			   startActivityForResult(intent, 1);             


			    }});

		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.activity_main);
	  
	  InicializaUI();
	  
	}
	
	/*@Override
	protected void onSaveInstanceState(Bundle outState) {
	 // TODO Auto-generated method stub
	 super.onSaveInstanceState(outState);
	 outState.putBoolean("1", creado);
	 Log.i(LOG_TAG, "OnSave");
	}

	/*@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	 // TODO Auto-generated method stub
	 super.onRestoreInstanceState(savedInstanceState);
	 creado=savedInstanceState.getBoolean("1");
	 Log.i(LOG_TAG, "OnREEStore");
	}*/
	
	protected void onPause(){
        super.onPause();
        Log.i(LOG_TAG, "NPAUSE");
        paused=true;
        
        
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    Log.i(LOG_TAG, "NSTOP");
	    paused=true;
        editor.putString("1", tel.getText().toString());
        editor.putBoolean("2", recall);
        
        editor.commit();
	}
	
	@Override
	public void onResume() {
	    super.onResume(); 
	    paused=false;
	    
	}
	
	@Override
	protected void onRestart() {
	    super.onRestart();
	    
	}
	
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        Log.i(LOG_TAG, "onDESTROY");
	        editor.putString("4", TextRecall.getText().toString());
	        editor.commit();
	        Borra();
	        //editor.clear();
	        //editor.commit();
	 	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);

   if(requestCode == PICK_CONTACT){
   if(resultCode == RESULT_OK){
    Uri contactData = data.getData();
    @SuppressWarnings("deprecation")
	Cursor cursor =  managedQuery(contactData, null, null, null, null);
    cursor.moveToFirst();

      number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

      //contactName.setText(name);
      Log.i(LOG_TAG, "NUMERO");
      tel.setText(number);
      //contactEmail.setText(email);
     }
     }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        MenuItem ShareOpt = menu.findItem(R.id.compartir);
        ShareActionProvider myShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(ShareOpt); 
        
        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.gatonimo.gatonimosrecall");
        myShareActionProvider.setShareIntent(i);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        return super.onOptionsItemSelected(item);
    }


	public void Call() {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"+tel.getText()));
		startActivityForResult(callIntent,2);
		
	}
	
	public void ReCall(){
		Log.i(LOG_TAG, "ENTRO!!");
		Thread hilo=new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(i--;i>=0;i--){
					TextSegs.post(new Runnable() {
					    public void run() {
					        TextSegs.setText("In "+i+" secs...");
					    } 
					});
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.i(LOG_TAG, "HILOOO");
				if(check.isChecked() && !paused)
					Call();
				else{
					hizoClick=false;
					Log.i(LOG_TAG, "NOOOO");}
			}
		});
		hilo.start();
	    
	}
	

	public void Restart() {
		// restart app
		Intent i = getBaseContext().getPackageManager()
			.getLaunchIntentForPackage(
				getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("Recall", true);
		startActivity(i);
		//startActivityForResult(i,2);
		//finish();
		
		
	}

	public void guardaEstado(boolean isPhoneCalling) {
		editor.putBoolean("3", isPhoneCalling);
		editor.commit();
		
	}
}
