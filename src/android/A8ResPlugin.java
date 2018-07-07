package com.ttebd.a8ResPlugin;

import android.os.Environment;

import com.landicorp.android.eptapi.device.Beeper;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class A8ResPlugin extends CordovaPlugin {
    com.ttebd.a8ResPlugin.LogUtil logUtil = new com.ttebd.a8ResPlugin.LogUtil();
    boolean falg = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {
            case "coolMethod":
//                Log.e(TAG, "execute coolMethod");
                System.out.println("JSONArray---1-"+args.getString(0));
                System.out.println("JSONArray---2-"+args.getString(1));
                this.cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        new com.ttebd.a8ResPlugin.DeviceBase().bindDeviceService(cordova.getActivity());
                        Beeper.startBeep(100);
                        logUtil.info("TestInfo", "测试日志打印");
                        logUtil.error("TestInfo", "测试日志打印");
                        logUtil.debug("TestInfo", "测试日志打印");
                        logUtil.warn("TestInfo", "测试日志打印");
//        this.testLog();


                        new com.ttebd.a8ResPlugin.DeviceBase().unbindDeviceService();

                        try {
                            coolMethod(args.getString(0), callbackContext);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            case "logInfo":
                logUtil.info(args.getString(0), "");
                return true;
            case "logDebug":
//                this.getExtras(callbackContext);
                return true;
            case "logWarn":
//                this.getExtras(callbackContext);
                return true;
            case "logError":
//                this.getExtras(callbackContext);
                return true;
            case "doPrint":
                this.doPrint(args, callbackContext);
                return true;
        }
        return false;
    }

    // 测试方法
    public void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    //    打印
    private void doPrint(JSONArray message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }


}
