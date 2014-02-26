package com.android.bluetooth.library.business;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import com.android.bluetooth.library.alertdialog.AlertDialogDevicesFound;
import com.android.bluetooth.library.broadcast.EventsBluetoothReceiver;
import com.android.bluetooth.library.communication.BluetoothComunication;
import com.android.bluetooth.library.manager.BluetoothManager;
import com.android.bluetooth.library.manager.IManager.OnBluetoothDeviceSelectedListener;
import com.android.bluetooth.library.manager.IManager.OnConnectionBluetoothListener;
import com.android.bluetooth.library.manager.IManager.OnSearchBluetoothListener;
import com.android.bluetooth.library.task.BluetoothClientTask;
import com.android.bluetooth.library.task.BluetoothServiceTask;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 19:51:29 05/05/2013
 */
public class CommunicationBusinessLogic implements OnConnectionBluetoothListener, 
										  OnBluetoothDeviceSelectedListener,
										  OnSearchBluetoothListener{
	
	private Context context;
	private Handler handler;
	
	private BluetoothManager bluetoothManager;
	private BluetoothComunication bluetoothComunication;
	private AlertDialogDevicesFound alertDialogDevicesFound;
	private EventsBluetoothReceiver eventsBluetoothReceiver;
	
	public CommunicationBusinessLogic(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
		
		bluetoothManager = new BluetoothManager();
		alertDialogDevicesFound = new AlertDialogDevicesFound(context, this);
		eventsBluetoothReceiver = new EventsBluetoothReceiver(context, this);
		
		registerFilter();
	}
	
	public void registerFilter(){
		eventsBluetoothReceiver.registerFilters();
	}
	
	public void unregisterFilter(){
		eventsBluetoothReceiver.unregisterFilters();
	}
	
	public void startFoundDevices(){
		stopCommucanition();
		
		eventsBluetoothReceiver.showProgress();
		bluetoothManager.getBluetoothAdapter().startDiscovery();
	}
	
	public void startClient(BluetoothDevice bluetoothDevice){
		BluetoothClientTask bluetoothClientTask = new BluetoothClientTask(context, this);
		bluetoothClientTask.execute(bluetoothDevice);
	}
	
	public void startServer(){
		BluetoothServiceTask bluetoothServiceTask = new BluetoothServiceTask(context, this);
		bluetoothServiceTask.execute(bluetoothManager.getBluetoothAdapter());
	}
	
	public void starCommunication(BluetoothSocket bluetoothSocket){
		bluetoothComunication = new BluetoothComunication(context, handler);
		bluetoothComunication.setBluetoothSocket(bluetoothSocket);
		bluetoothComunication.start();
	}
	
	public void stopCommucanition(){
		if(bluetoothComunication != null){
			bluetoothComunication.stopComunication();
		}
	}
	
	public boolean sendMessage(String message){
		if(bluetoothComunication != null){
			return bluetoothComunication.sendMessageByBluetooth(message);
		}else{
			return false;
		}
	}
	
	public BluetoothManager getBluetoothManager(){
		return bluetoothManager;
	}

	@Override
	public void onBluetoothDeviceSelected(BluetoothDevice bluetoothDevice) {
		startClient(bluetoothDevice);
	}
	
	@Override
	public void onConnectionBluetooth(BluetoothSocket bluetoothSocket) {
		starCommunication(bluetoothSocket);
	}

	@Override
	public void onSearchBluetooth(List<BluetoothDevice> devicesFound) {
		alertDialogDevicesFound.settingsAlertDialog(devicesFound);
	}
}