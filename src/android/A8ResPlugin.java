package com.ttebd.a8ResPlugin;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

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
import java.security.cert.TrustAnchor;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

import static com.ttebd.a8ResPlugin.DeviceBase.bindDeviceService;
import static com.ttebd.a8ResPlugin.DeviceBase.unbindDeviceService;

public class A8ResPlugin extends CordovaPlugin {
    com.ttebd.a8ResPlugin.LogUtil logUtil = new com.ttebd.a8ResPlugin.LogUtil();
    com.ttebd.a8ResPlugin.PrinterMain printerMain = new com.ttebd.a8ResPlugin.PrinterMain();
    private static Activity activity = null;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (activity == null) {
            activity = this.cordova.getActivity();
            Log.d("a8ResPlugin", "初始化！");
            logUtil.info("a8ResPlugin", "初始化！");
        }


        // 日志调用
        try {
            Runnable logRb = new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (action) {
                            case "logInfo":
                                JSONObject infoMessage = args.getJSONObject(0);
                                logUtil.info(infoMessage.getString("tag"), infoMessage.getString("message"));
                                break;
                            case "logDebug":
                                JSONObject debugMessage = args.getJSONObject(0);
                                logUtil.debug(debugMessage.getString("tag"), debugMessage.getString("message"));
                                break;
                            case "logWarn":
                                JSONObject warnMessage = args.getJSONObject(0);
                                logUtil.warn(warnMessage.getString("tag"), warnMessage.getString("message"));
                                break;
                            case "logError":
                                JSONObject errorMessage = args.getJSONObject(0);
                                logUtil.error(errorMessage.getString("tag"), errorMessage.getString("message"));
                                break;
                            default:
                                logUtil.error("插件调用", "没有找到此方法");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 打印调用
        try {
            Runnable rb = new Runnable() {
                @Override
                public void run() {
                    bindDeviceService(activity.getApplicationContext());
                    try {
                        switch (action) {
                            case "printReturnGood":
                                printerMain.printReturnGood(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSalesReport":
                                printerMain.printSalesReport(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSalesSlip":
                                printerMain.printSalesSlip(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSalesSmallSummary":
                                printerMain.printSalesSmallSummary(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSales":
                                printerMain.printSales(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSalesSummary":
                                printerMain.printSalesSummary(activity.getApplicationContext(), args, callbackContext);
                                break;
                        }
                    } catch (Exception e) {

                    } finally {
//                        unbindDeviceService();
                    }
                }
            };
            cordova.getThreadPool().execute(rb);
            return true;
        } catch (Exception e) {
            logUtil.debug("JsInterface (Exception)", e.getLocalizedMessage());
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
