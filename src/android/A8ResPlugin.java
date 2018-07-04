package com.ttebd.a8ResPlugin;

import android.util.Log;

import com.ttebd.a8ResPlugin.PrinterMain;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 *
 *
 */


//sourceSets {
//        main {
//        jniLibs.srcDirs = ['libs']
//        }}
public class A8ResPlugin extends CordovaPlugin {
    public static final String TAG = "A8ResPlugin";


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//        if (action.equals("coolMethod")) {
//            String message = args.getString(0);
//            this.coolMethod(message, callbackContext);
//            return true;
//        }
//        return false;

        switch (action) {
            case "coolMethod":
                Log.e(TAG, "execute coolMethod");
                this.coolMethod(args.getString(0), callbackContext);
                this.doPrint(args, callbackContext);
                return true;
            case "getExtras":
//        this.getExtras(callbackContext);
                return true;
            case "doPrint":
                this.doPrint(args, callbackContext);
                return true;
        }
        return false;
    }

    // 测试方法
    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    //    打印
    private void doPrint(JSONArray message, CallbackContext callbackContext) {

        PrinterMain.bindDeviceService(this.cordova.getActivity());


        PrinterMain printerMain = new PrinterMain();
        printerMain.init();
//    printerMain.addText();
        printerMain.addBarcode();
        printerMain.startPrint();


    }
}
