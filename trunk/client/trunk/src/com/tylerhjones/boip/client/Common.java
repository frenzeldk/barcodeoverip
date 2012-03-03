/*
 * 
 * BarcodeOverIP (Android < v3.2) Version 0.9.2
 * Copyright (C) 2012, Tyler H. Jones (me@tylerjones.me)
 * http://boip.tylerjones.me/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Filename: Common.java
 * Package Name: com.tylerhjones.boip.client
 * Created By: Tyler H. Jones on Feb 1, 2012 at 2:28:24 PM
 * 
 * Description: Common functions and variable declarations (constants)
 */


package com.tylerhjones.boip.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class Common {
//-----------------------------------------------------------------------------------------
//--- Constant variable declarations -------------------------------------------------------

	/** Application constants ************************************************************ */
	public static final String APP_AUTHOR = "@string/author";
	public static final String APP_VERSION = "@string/versionnum";	
	
	/** Database constants *************************************************************** */
	public static final String DB_NAME = "servers";
	public static final int DB_VERSION = 1;
	public static final String TABLE_SERVERS = "servers";
	public static final String S_FIELD_INDEX = "idx";
	public static final String S_FIELD_NAME = "name";
	public static final String S_FIELD_HOST = "host";
	public static final String S_FIELD_PORT = "port";
	public static final String S_FIELD_PASS = "pass";
	public static final String S_INDEX = "idx";
	public static final String S_NAME = "name";
	public static final String S_HOST = "host";
	public static final String S_PORT = "port";
	public static final String S_PASS = "pass";
	
	// Our one and only preference, set after the first run of the application
	public static final String PREFS = "boip_client";
	public static final String PREF_VERSION = "version";

	/** Default value constants *********************************************************** */
	public static final int DEFAULT_PORT = 41788;
	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final String DEFAULT_PASS = "none";
	public static final String DEFAULT_NAME = "Untitled";
	
	/** Network communication message constants ******************************************** */
	public static final String OK = "OK"; // Server's client/user authorization pass message
	public static final String THANKS = "THANKS"; // Server's 'all ok' reponse message
	public static final String DTERM = ";"; // Data string terminator
	public static final String DSEP = "||"; // Data and values separator
	public static final String CHECK = "CHECK"; // Command to ask the server to authenticate us
	public static final String ERR = "ERR"; // Prefix for errors returned by the server
	
	/** Constants for keeping track of activities ****************************************** */
	public static final int ADD_SREQ = 91;
	public static final int EDIT_SREQ = 105;
	public static final int BARCODE_SREQ = 11;

//-----------------------------------------------------------------------------------------
//--- Server return error codes and descriptions ------------------------------------------
	
	public static Hashtable<String, String> errorCodes() {
		Hashtable<String, String> errors = new Hashtable<String, String>(13);
		errors.put("ERR1", "Invalid data format and/or syntax!");
		errors.put("ERR2", "No data was sent!");
		errors.put("ERR3", "Invalid Command Sent!");
		errors.put("ERR4", "Missing/Empty Command Argument(s) Recvd.");
		errors.put("ERR5", "Invalid command syntax!");
		errors.put("ERR6", "Invalid Auth Syntax!");
		errors.put("ERR7", "Access Denied!");
		errors.put("ERR8", "Server Timeout, Too Busy to Handle Request!");
		errors.put("ERR11", "Invalid Auth.");
		errors.put("ERR14", "Invalid Login Command Syntax.");
		errors.put("ERR19", "Unknown Auth Error");
		errors.put("ERR99", "Unknown exception occured.");
		errors.put("ERR100", "Invalid Host/IP.");
		errors.put("ERR101", "Cannont connect to server.");
		return errors;
	}
	
	public static void showMsgBox(Context c, String title, String msg) {
		AlertDialog ad = new AlertDialog.Builder(c).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(msg);
		ad.setTitle(title);
			ad.setButton(OK, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		ad.show();
	}
	
	public static String getAppVersion(Context c, @SuppressWarnings("rawtypes") Class cls) {
		try {
			ComponentName comp = new ComponentName(c, cls);
			PackageInfo pinfo = c.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			return pinfo.versionName;
		}
		catch (android.content.pm.PackageManager.NameNotFoundException e) {
			return null;
		}
	}
	
// -----------------------------------------------------------------------------------------
// --- Validate settings values functions --------------------------------------------------
	
	// Check that the barcode string contains only letters and numbers
	public static boolean isValidBarcode(String s) {
		if (!s.matches("^[a-zA-Z0-9]+$") || s.length() < 1) { return false; }
		return true;
	}
	
	// Check that the host/ip string is valid
	public static boolean isValidHost(String s) {
		// if (!s.matches("^[a-zA-Z0-9]+$") || s.length() < 1) { return false; }
		return true;
	}
	
	public static void isValidIP(String ip) throws Exception {
		try {
			String[] octets = ip.split("\\.");
			for (String s : octets) {
				int i = Integer.parseInt(s);
				if (i > 255 || i < 0) { throw new NumberFormatException(); }
			}
		} catch (NumberFormatException e) {
			throw new Exception("Invalid IP address! '" + ip + "'");
		}
	}
	
	public static void isValidPort(String port) throws Exception {
		try {
			int p = Integer.parseInt(port);
			if(p < 1 || p > 65535 ) { throw new NumberFormatException(); }
		} catch (NumberFormatException e) {
			throw new Exception("Invalid Port Number! '" + port + "'");
		}
	}
	
	public static void showAbout(Context c) {
		final TextView message = new TextView(c);
		final SpannableString s = new SpannableString(c.getText(R.string.about_msg_body));
		Linkify.addLinks(s, Linkify.WEB_URLS);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());
		
		AlertDialog adialog = new AlertDialog.Builder(c).setTitle(R.string.about_msg_title).setCancelable(true)
								.setIcon(android.R.drawable.ic_dialog_info).setPositiveButton(R.string.close, null).setView(message).create();
		adialog.show();
		((TextView) message).setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public static boolean isNetworked(Context c) { // Check if we are on a network
		ConnectivityManager mManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo current = mManager.getActiveNetworkInfo();
		if (current == null) { return false; }
		return (current.getState() == NetworkInfo.State.CONNECTED);
	}


//-----------------------------------------------------------------------------------------
//--- Make SHA1 Hash for transmitting passwords -------------------------------------------
	
	public static String convertToHex_better(byte[] data) { // This one may work better than the one below
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) { 
	        int halfbyte = (data[i] >>> 4) & 0x0F;
	        int two_halfs = 0;
	        do { 
	            if ((0 <= halfbyte) && (halfbyte <= 9)) 
	                buf.append((char) ('0' + halfbyte));
	            else 
	                buf.append((char) ('a' + (halfbyte - 10)));
	            halfbyte = data[i] & 0x0F;
	        } while(two_halfs++ < 1);
	    } 
	    return buf.toString();
	} 
	
	public static String convertToHex(byte[] data) { 
	    StringBuffer buf = new StringBuffer();
	    int length = data.length;
	    for(int i = 0; i < length; ++i) { 
	        int halfbyte = (data[i] >>> 4) & 0x0F;
	        int two_halfs = 0;
	        do { 
	            if((0 <= halfbyte) && (halfbyte <= 9)) 
	                buf.append((char) ('0' + halfbyte));
	            else 
	                buf.append((char) ('a' + (halfbyte - 10)));
	            halfbyte = data[i] & 0x0F;
	        }
	        while(++two_halfs < 1);
	    } 
	    return buf.toString();
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException {	 
	    MessageDigest md = MessageDigest.getInstance("SHA-1");
	    md.update(text.getBytes());
	
	    byte byteData[] = md.digest();
	
	    //convert the byte to hex format method 1
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	    	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }
	
	    //System.out.println("Hex format : " + sb.toString());
	
	    //convert the byte to hex format method 2
	    StringBuffer hexString = new StringBuffer();
		for (int i=0;i<byteData.length;i++) {
			String hex=Integer.toHexString(0xff & byteData[i]);
		     	if(hex.length()==1) hexString.append('0');
		     	hexString.append(hex);
		}
		return hexString.toString();
	}
	
}