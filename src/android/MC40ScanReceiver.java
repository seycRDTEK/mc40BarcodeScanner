package com.eyc.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author vkodari
 * 
 */
public class MC40ScanReceiver extends BroadcastReceiver {

	// Tag used for logging errors
	private static final String TAG = "BarcodeScanner";

	// Let's define some intent strings
	// This intent string contains the source of the data as a string
	private static final String SOURCE_TAG = "com.motorolasolutions.emdk.datawedge.source";
	// This intent string contains the barcode symbology as a string
	private static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";
	
	private static final String INTENT_FILTER_PACKAGE ="com.eyc.plugins.barcodescanner.RECVR";

	// This intent string contains the captured data as a string
	// (in the case of MSR this data string contains a concatenation of the
	// track data)
	private static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";

	private CallbackContext callback_receive;
	private boolean isReceiving = true;

	// This broadcast boolean is used to continue or not the message broadcast
	// to the other BroadcastReceivers waiting for Scanning results
	private boolean broadcast = false;

	@Override
	public void onReceive(Context ctx, Intent intent) {

		// Get Scanner results map from Intent
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (this.isReceiving && this.callback_receive != null) {
				String formattedMsg = handleDecodeData(intent);

				PluginResult result = new PluginResult(PluginResult.Status.OK,
						formattedMsg);
				result.setKeepCallback(true);
				callback_receive.sendPluginResult(result);
				
				Intent sendIntent = BarcodeScanner.getBroadCastIntent(PluginResult.Status.OK, formattedMsg, true);
				LocalBroadcastManager.getInstance(ctx.getApplicationContext()).sendBroadcast(sendIntent);
			}

			// If the plugin is active and we don't want to broadcast to other
			// receivers
			try{
				if (this.isReceiving && !broadcast) {
					this.abortBroadcast();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param v
	 */
	public void broadcast(boolean v) {
		this.broadcast = v;
	}

	/**
	 * @param ctx
	 */
	public void startReceiving(CallbackContext ctx) {
		this.callback_receive = ctx;
		this.isReceiving = true;
	}

	/**
	 * 
	 */
	public void stopReceiving() {
		this.callback_receive = null;
		this.isReceiving = false;
	}

	// This function is responsible for getting the data from the intent
	// formatting it and adding it to the end of the edit box
	private String handleDecodeData(Intent i) {
		// check the intent action is for us
		if (i.getAction().contentEquals(INTENT_FILTER_PACKAGE)) {
			// define a string that will hold our output
			String out = "";
			// get the source of the data
			String source = i.getStringExtra(SOURCE_TAG);
			// save it to use later
			if (source == null)
				source = "scanner";
			// get the data from the intent
			String data = i.getStringExtra(DATA_STRING_TAG);
			// let's define a variable for the data length
			Integer data_len = 0;
			// and set it to the length of the data
			if (data != null)
				data_len = data.length();

			// check if the data has come from the barcode scanner
			if (source.equalsIgnoreCase("scanner")) {
				// check if there is anything in the data
				if (data != null && data.length() > 0) {
					// we have some data, so let's get it's symbology
					String sLabelType = i.getStringExtra(LABEL_TYPE_TAG);
					// check if the string is empty
					if (sLabelType != null && sLabelType.length() > 0) {
						// format of the label type string is
						// LABEL-TYPE-SYMBOLOGY
						// so let's skip the LABEL-TYPE- portion to get just the
						// symbology
						sLabelType = sLabelType.substring(11);
					} else {
						// the string was empty so let's set it to "Unknown"
						sLabelType = "Unknown";
					}
					// let's construct the beginning of our output string
					/*
					 * out = "Source: Scanner, " + "Symbology: " + sLabelType +
					 * ", Length: " + data_len.toString() + ", Data: ...\r\n";
					 */
					out = data;
				}
			}

			// check if the data has come from the MSR
			if (source.equalsIgnoreCase("msr")) {
				// construct the beginning of our output string
				// out = "Source: MSR, Length: " + data_len.toString()
				// + ", Data: ...\r\n";

				out = data_len.toString();
			}

			return out;
		}

		return null;
	}

}
