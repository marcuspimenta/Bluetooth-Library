package com.android.bluetooth.library.task;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

import com.android.bluetooth.library.R;
import com.android.bluetooth.library.communication.BluetoothService;
import com.android.bluetooth.library.manager.IManager.OnConnectionBluetoothListener;
import com.android.bluetooth.library.util.ToastUtil;

/**
 * 
 * @author Marcus Pimenta
 * @email mvinicius.pimenta@gmail.com
 * @date 17:54:15 21/02/2014
 */
public class BluetoothServiceTask extends AsyncTask<BluetoothAdapter, Void, BluetoothSocket>{

	private Context context;
	private ProgressDialog progressDialog;	
	
	private ToastUtil toastUtil;
	private BluetoothService bluetoothService;
	private OnConnectionBluetoothListener onBluetoothListener;

	public BluetoothServiceTask(Context context, OnConnectionBluetoothListener onBluetoothListener){
		this.context = context;
		this.onBluetoothListener = onBluetoothListener;
	
		toastUtil = new ToastUtil(context);
		bluetoothService = new BluetoothService();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, 
											 context.getString(R.string.waiting), 
											 context.getString(R.string.msg_waiting_connection));
	}
	
	@Override
	protected BluetoothSocket doInBackground(BluetoothAdapter... bluetoothAdapter) {
		return bluetoothService.startServer(bluetoothAdapter[0]);
	}
	
	@Override
	protected void onPostExecute(BluetoothSocket bluetoothSocket) {
		super.onPostExecute(bluetoothSocket);
		
		closeDialog();
		
		if(bluetoothSocket != null){
			onBluetoothListener.onConnectionBluetooth(bluetoothSocket);
		}else{
			toastUtil.showToast(context.getString(R.string.connection_failed));
		}
	}
	
	private void closeDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}


}	