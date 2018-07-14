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
//      Log.d("a8ResPlugin", "初始化！");
            logUtil.info("a8ResPlugin", "初始化！");
        }
        try {
            Runnable rb = new Runnable() {

                @Override
                public void run() {
                    bindDeviceService(activity.getApplicationContext());

                    try {

                        switch (action) {
                            case "printSalesSmallSummary":
                                printerMain.printSalesSmallSummary(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSales":
                                printerMain.printSales(activity.getApplicationContext(), args, callbackContext);
                                break;
                            case "printSalesSummary":
                                printerMain.printSalesSummary(activity.getApplicationContext(), args, callbackContext);
                                break;
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

                    } finally {
                        unbindDeviceService();
                    }
                }
            };

            cordova.getThreadPool().execute(rb);
            return true;

        } catch (Exception e) {
            logUtil.debug("JsInterface (Exception)", e.getLocalizedMessage());
        }

        return false;

    /*switch (action) {
      case "coolMethod":

//             日志测试
//                JSONObject params = args.getJSONObject(0);
//                System.out.println("JSONArray---1-" + params);
        this.cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
//                        printerMain.printSales(cordova.getActivity(), args, callbackContext);
            try {
            } catch (Exception e) {
              e.printStackTrace();
              logUtil.error("JsInterface (Exception)", e.getLocalizedMessage());
            }
          }
        });
        return true;
      case "printSalesSmallSummary":
        this.cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            printerMain.printSalesSmallSummary(activity.getApplicationContext(), args, callbackContext);
          }
        });
        return true;
      case "printSales":
        this.cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
          }
        });
        return true;
      case "printSalesSummary":
        this.cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            printerMain.printSalesSummary(activity.getApplicationContext(), args, callbackContext);
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
    }*/
//    return false;
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
