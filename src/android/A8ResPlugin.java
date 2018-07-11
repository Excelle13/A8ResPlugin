package com.ttebd.a8ResPlugin;

import android.os.Environment;

import com.landicorp.android.eptapi.device.Beeper;
import com.landicorp.android.eptapi.device.Printer;

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
    com.ttebd.a8ResPlugin.PrinterMain printerMain = new com.ttebd.a8ResPlugin.PrinterMain();
    boolean falg = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {
            case "coolMethod":
//             日志测试
                JSONObject params = args.getJSONObject(0);
                System.out.println("JSONArray---1-" + params);
                this.cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        printerMain.printSales(cordova.getActivity(), args, callbackContext);
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            case "printSalesSmallSummary":
                this.cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        printerMain.printSalesSmallSummary(cordova.getActivity(), args, callbackContext);
                    }
                });
                return true;
            case "printSales":
                this.cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        printerMain.printSales(cordova.getActivity(), args, callbackContext);
                    }
                });
                return true;
            case "printSalesSummary":
                this.cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        printerMain.printSalesSummary(cordova.getActivity(), args, callbackContext);
                    }
                });
                return true;
            case "logInfo":
                JSONObject infoMessage = args.getJSONObject(0);
                logUtil.info(infoMessage.getString("tag"), infoMessage.getString("message"));
                return true;
            case "logDebug":
                JSONObject debugMessage = args.getJSONObject(0);
                logUtil.debug(debugMessage.getString("tag"), debugMessage.getString("message"));
                return true;
            case "logWarn":
                JSONObject warnMessage = args.getJSONObject(0);
                logUtil.warn(warnMessage.getString("tag"), warnMessage.getString("message"));
                return true;
            case "logError":
                JSONObject errorMessage = args.getJSONObject(0);
                logUtil.error(errorMessage.getString("tag"), errorMessage.getString("message"));
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
