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
        if (action.indexOf("log") == 0) {
            try {
                Runnable logRb = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (action) {
                                case "logInfo":
                                    JSONObject infoMessage = args.getJSONObject(0);
                                    logUtil.info(infoMessage.optString("tag"), infoMessage.optString("message"));
                                    break;
                                case "logDebug":
                                    JSONObject debugMessage = args.getJSONObject(0);
                                    logUtil.debug(debugMessage.optString("tag"), debugMessage.optString("message"));
                                    break;
                                case "logWarn":
                                    JSONObject warnMessage = args.getJSONObject(0);
                                    logUtil.warn(warnMessage.optString("tag"), warnMessage.optString("message"));
                                    break;
                                case "logError":
                                    JSONObject errorMessage = args.getJSONObject(0);
                                    logUtil.error(errorMessage.optString("tag"), errorMessage.optString("message"));
                                    break;
                                default:
                                    logUtil.error("log调用", "没有找到此方法");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                cordova.getThreadPool().execute(logRb);
//                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 打印调用

        if (action.indexOf("print") == 0) {
            try {
                Runnable rb = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (action) {
                                case "printReturnGood":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printReturnGood(activity.getApplicationContext(), args, callbackContext);
                                    break;
                                case "printSalesReport":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printSalesReport(activity.getApplicationContext(), args, callbackContext);
                                    break;
                                case "printSalesSlip":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printSalesSlip(activity.getApplicationContext(), args, callbackContext);
                                    break;
                                case "printSalesSmallSummary":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printSalesSmallSummary(activity.getApplicationContext(), args, callbackContext);
                                    break;
                                case "printSales":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printSales(activity.getApplicationContext(), args, callbackContext);
//                                unbindDeviceService();
                                    break;
                                case "printSalesSummary":
                                    bindDeviceService(activity.getApplicationContext());
                                    printerMain.printSalesSummary(activity.getApplicationContext(), args, callbackContext);
                                    break;
                                case "printResRelease":
                                    unbindDeviceService();
                                    break;
                                default:
                                    logUtil.error("打印调用", "没有找到此方法");

                            }
                        } catch (Exception e) {
                            unbindDeviceService();
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
