package com.eyc.plugins;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

import android.content.Intent;
import android.content.IntentFilter;

/*
 * To receive data via intents from DataWedge, the DataWedge intent plug-in will need to be configured.
 * The following steps will help you get started...
 * 1. Launch DataWedge
 * 2. Create a new profile and give it a name such as "eycApp"
 * 3. Edit the profile
 * 4. Go into Associated apps, tap the menu button, and add a new app/activity
 * 5. For the application select com.motorolasolutions.emdk.sample.dwdemosample
 * 6. For the activity select com.motorolasolutions.emdk.sample.dwdemosample.MainActivty
 * com.motorolasolutions.emdk.sample.dwdemosample.RECVR
 * 7. Go back and disable the keystroke output plug-in
 * 8. Enable the intent output plug-in
 * 9. For the intentf action enter com.eyc.plugins.barcodescanner.RECVR
 * 10. For the intent category enter android.intent.category.DEFAULT
 * 
 * Now when you run this activity and scan a barcode you should see the barcode data
 * preceded with additional info (source, symbology and length); see handleDecodeData below.
 */
public class MC40BarcodeScanner extends CordovaPlugin {
	public static final String TAG = "BarcodeScanner";

	public static Intent gPendingIntent;

	public CallbackContext onBarcodeCallback;

	public final String ACTION_RECEIVE_BARCODE = "startDWReception";
	public final String ACTION_STOP_RECEIVE_BARCODE = "stopDWReception";

	private CallbackContext callback_receive;
	private MC40ScanReceiver scanReceiver = null;
	private boolean isReceiving = false;

	public MC40BarcodeScanner() {
		super();
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView){
		
		super.initialize(cordova, webView);
	}
	
	@Override
	public boolean execute(String action, JSONArray arg1,
			final CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "execute : "+action);
		if (action.equals(ACTION_RECEIVE_BARCODE)) {

			// if already receiving (this case can happen if the startReception
			// is called
			// several times
			if (this.isReceiving) {
				// close the already opened callback ...
				PluginResult pluginResult = new PluginResult(
						PluginResult.Status.NO_RESULT);
				pluginResult.setKeepCallback(false);
				this.callback_receive.sendPluginResult(pluginResult);

				// ... before registering a new one to the sms receiver
			}
			this.isReceiving = true;

			if (this.scanReceiver == null) {
				this.scanReceiver = new MC40ScanReceiver();
				IntentFilter fp = new IntentFilter(
						"com.eyc.plugins.barcodescanner.RECVR");
				//fp.setPriority(1000);
				 fp.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
				this.cordova.getActivity().registerReceiver(this.scanReceiver,
						fp);
			}

			this.scanReceiver.startReceiving(callbackContext);

			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
			this.callback_receive = callbackContext;

			return true;
		} else if (action.equals(ACTION_STOP_RECEIVE_BARCODE)) {

			if (this.scanReceiver != null) {
				scanReceiver.stopReceiving();
			}

			this.isReceiving = false;

			// 1. Stop the receiving context
			PluginResult pluginResult = new PluginResult(
					PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(false);
			this.callback_receive.sendPluginResult(pluginResult);

			// 2. Send result for the current context
			pluginResult = new PluginResult(PluginResult.Status.OK);
			callbackContext.sendPluginResult(pluginResult);

			return true;
		}

		return false;
	}

}
