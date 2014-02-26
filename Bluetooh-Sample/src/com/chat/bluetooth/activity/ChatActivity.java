package com.chat.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.bluetooth.library.business.CommunicationBusinessLogic;
import com.android.bluetooth.library.manager.Constants;
import com.android.bluetooth.library.util.ToastUtil;
import com.chat.bluetooth.R;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * 05/10/2012 14:41:34
 */
public class ChatActivity extends GenericActivity{

	private Button buttonService;
	private Button buttonClient;
	private ImageButton buttonSend;
	private EditText editTextMessage;
	private ListView listVewHistoric;
	private ArrayAdapter<String> historic;
	
	private ToastUtil toastUtil;
	private CommunicationBusinessLogic communicationBusinessLogic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
		
		settingsAttributes();
		settingsView();
		
		verifyBluetooth();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clean:
                historic.clear();
                historic.notifyDataSetChanged();
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void settingsAttributes() {
		toastUtil = new ToastUtil(this);
		communicationBusinessLogic = new CommunicationBusinessLogic(this, handler);
	}

	@Override
	public void settingsView() {
		editTextMessage = (EditText)findViewById(R.id.editTextMessage);
		
		historic = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listVewHistoric = (ListView)findViewById(R.id.listVewHistoric);
		listVewHistoric.setAdapter(historic);
		
		buttonSend = (ImageButton)findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String message = editTextMessage.getText().toString(); 
				
				if(message.trim().length() > 0){
					if(communicationBusinessLogic.sendMessage(message)){
						editTextMessage.setText(""); 
						
						historic.add("I: " + message); 
						historic.notifyDataSetChanged();			
					}
				}else{
					toastUtil.showToast(getString(R.string.enter_message));
				}
			}
		});
		
		buttonService = (Button)findViewById(R.id.buttonService);
		buttonService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Constants.BT_TIMER_VISIBLE); 
				startActivityForResult(discoverableIntent, Constants.BT_VISIBLE);
			}
		});
		
		buttonClient = (Button)findViewById(R.id.buttonClient);
		buttonClient.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				communicationBusinessLogic.startFoundDevices();
			}
		});
	}
	
	public void verifyBluetooth() {
		if (communicationBusinessLogic.getBluetoothManager().verifySuportedBluetooth()) {
			if (!communicationBusinessLogic.getBluetoothManager().isEnabledBluetooth()) { 
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enableBtIntent, Constants.BT_ACTIVATE);
			}
		} else {
			toastUtil.showToast(getString(R.string.no_support_bluetooth));
			finish();
		}
	}
	
	private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            synchronized (msg) {
                switch (msg.what) {
                	case 1:
                		toastUtil.showToast((String)(msg.obj));
                		break;
                	case 2:
                		historic.add((String)(msg.obj));
       				 	historic.notifyDataSetChanged();
       				 	
       				 	listVewHistoric.requestFocus();
       				 	break;
                }
            }
        };
    };
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case Constants.BT_ACTIVATE:
				if (RESULT_OK != resultCode) {
					toastUtil.showToast(getString(R.string.activate_bluetooth_to_continue));
					finish(); 
				}
				break;
				
			case Constants.BT_VISIBLE:
				if (resultCode == Constants.BT_TIMER_VISIBLE) {
					
					communicationBusinessLogic.stopCommucanition();
					communicationBusinessLogic.startServer();
				} else {
					toastUtil.showToast(getString(R.string.device_must_visible));
				}
				break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		communicationBusinessLogic.unregisterFilter();
		communicationBusinessLogic.stopCommucanition();
	}

}