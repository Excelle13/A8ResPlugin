package com.ttebd.a8ResPlugin;

import android.content.Context;
import android.util.Log;

import com.landicorp.android.eptapi.device.Beeper;
import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.device.Printer.Alignment;
import com.landicorp.android.eptapi.device.Printer.Format;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.QrCode;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.landicorp.android.eptapi.device.Printer.Format.HZ_DOT16x16;
import static com.landicorp.android.eptapi.device.Printer.Format.HZ_DOT24x24;
import static com.landicorp.android.eptapi.utils.QrCode.ECLEVEL_Q;

public class PrinterMain extends com.ttebd.a8ResPlugin.DeviceBase {
    private Printer.Progress progress;
    private Printer printer = Printer.getInstance();
    com.ttebd.a8ResPlugin.LogUtil logUtil = new com.ttebd.a8ResPlugin.LogUtil();


    /**
     * 退货小票
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printReturnGood(Context context, JSONArray args, CallbackContext callbackContext) {
        System.out.println(args);
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {

                printer.setAutoTrunc(false);

                // 获取小票主要信息对象
                JSONObject params = args.getJSONObject(0);
                JSONArray storeInfo = params.getJSONArray("storeInfo");
                JSONArray payment = params.getJSONArray("payment");
                JSONArray goods = params.getJSONArray("goods");
                String reprint = params.optString("reprint");
                String refundAmount = params.optString("refundAmount");
                String totalAmount = params.optString("totalAmount");
                JSONArray tips = params.getJSONArray("tips");
                JSONArray orderNo = params.getJSONArray("orderNo");
                JSONArray barCode = params.getJSONArray("barCode");

                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

// 打印图片
                printImg(context, printer);
                printer.printText("              退货             \n");
                if (reprint.length() > 0) {
                    printer.printText(Alignment.CENTER, "【" + reprint + "】\n");
                }
                samllFormatLine(format, printer);

// 店铺信息
                samllFormat(format, printer);
                for (int i = 0; i < storeInfo.length(); i++) {
                    if (i == 0) {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%18s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    } else {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    }
                }
                samllFormatLine(format, printer);

// 条形码
                for (int i = 0; i < barCode.length(); i++) {
                    JSONObject item = barCode.getJSONObject(i);
                    printer.printBarCode(Alignment.CENTER, item.optString("barCodeValue"));
                    format.setAscSize(Format.ASC_DOT7x7);
                    format.setAscScale(Format.ASC_SC1x2);
                    generalFormat(format, printer);
                    printer.printText(String.format("%28s", item.optString("barCodeTitle")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

// 商品
                generalFormat(format, printer);
                printer.printText(String.format("%-12s%3s%11s", "货号", "数量", "金额(RMB)\n"));
                samllFormat(format, printer);

                for (int i = 0; i < goods.length(); i++) {
                    JSONObject item = goods.getJSONObject(i);
                    printer.printText(
                            String.format("%-" + (24 + formatPrintText(item.optString("goodCode"))) + "s%7s%19s", item.optString("goodCode"), item.optString("goodSum"), item.optString("amount")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

                printer.printText(String.format("%-20s%27s", "合计", totalAmount + "\n"));
                printer.printText(String.format("%-18s%27s", "退款金额", refundAmount + "\n"));
                samllFormatLine(format, printer);

// 付款方式
                generalFormat(format, printer);
                printer.printText(String.format("%-15s%11s", "退款方式", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < payment.length(); i++) {
                    JSONObject item = payment.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("paymentMethod"))) + "s%7s%18s", item.optString("paymentMethod"), item.optString("paymentSum"), item.optString("paymentAmount")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

// 订单号
                generalFormat(format, printer);
//        printer.printText(String.format("%-15s%11s", "退款方式", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < orderNo.length(); i++) {
                    JSONObject item = orderNo.getJSONObject(i);
//          System.out.println("item-------"+item);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("orderInfoKey"))) + "s%s%25s", item.optString("orderInfoKey"), item.optString("paymentSum"), item.optString("orderInfoValue")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

// tips
                samllSamllFormat(format, printer);
                for (int i = 0; i < tips.length(); i++) {
                    JSONObject item = tips.getJSONObject(i);
                    printer.printText(Alignment.LEFT, item.optString("tipsId") + "." + item.optString("article") + "\n");
                }

                printer.feedLine(5);
                generalFormat(format, printer);
            }

            @Override
            public void onFinish(int i) {
                printFinish(i, callbackContext);
            }

            @Override
            public void onCrash() {

            }
        };
        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    /**
     * 销售报表
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printSalesReport(Context context, JSONArray args, CallbackContext callbackContext) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {

                printer.setAutoTrunc(false);

                // 获取小票主要信息对象
                JSONObject params = args.getJSONObject(0);

                JSONArray storeInfo = params.getJSONArray("storeInfo");
                JSONArray payment = params.getJSONArray("payment");
                String reprint = params.optString("reprint");
                String paymentTotal = params.optString("paymentTotal");

                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

                printImg(context, printer);
                printer.printText("            销售报表            \n");
                if (reprint.length() > 0) {
                    printer.printText(Alignment.CENTER, "【" + reprint + "】\n");
                }
                samllFormatLine(format, printer);
                samllFormat(format, printer);
// 店铺信息
                for (int i = 0; i < storeInfo.length(); i++) {
                    if (i == 0) {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%18s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    } else {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    }
                }
                printDate(printer);
                samllFormatLine(format, printer);
                generalFormat(format, printer);
                printer.printText(String.format("%-8s%s%11s", "付款方式", "单据数量", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < payment.length(); i++) {
                    JSONObject item = payment.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("paymentMethod"))) + "s%7s%18s", item.optString("paymentMethod"), item.optString("paymentSum"), item.optString("paymentAmount")));
                    printer.printText("\n");
                }
                printer.printText(Alignment.RIGHT, "总计：" + paymentTotal + "\n");

                printer.feedLine(3);
                generalFormat(format, printer);

            }

            @Override
            public void onFinish(int i) {
                printFinish(i, callbackContext);
            }

            @Override
            public void onCrash() {

            }
        };

        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 银行签购单
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printBankSalesSlip(Context context, JSONArray args, CallbackContext callbackContext) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {

                printer.setAutoTrunc(false);

                // 获取小票主要信息对象
                JSONObject params = args.getJSONObject(0);
                JSONArray salesSlip = params.getJSONArray("salesSlip");
                String salesSlipType = params.optString("salesSlipType");


                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

//        generalFormat(format, printer);

                printer.printText("           "+salesSlipType+"签购单         \n");
                samllFormatLine(format, printer);

                generalFormat(format, printer);
                for (int i = 0; i < salesSlip.length(); i++) {
                    JSONObject item = salesSlip.getJSONObject(i);
                    printer.printText(Alignment.LEFT, item.optString("salesSlipKey")+":"+item.optString("salesSlipValue")+"\n");
                }

//        printer.printText(Alignment.LEFT, "备注：");
//        printer.feedLine(2);
                printer.printText(Alignment.LEFT, "顾客签名：");
                printer.feedLine(1);
                samllFormat(format, printer);
                printer.printText(Alignment.CENTER, "- - - - - - x - - - - - - - - - - - x - - - - - \n");
                printer.feedLine(3);
                generalFormat(format, printer);


// 打印图片
//        printImg(context, printer);
            }

            @Override
            public void onFinish(int i) {
//        printFinish(i, callbackContext);
                Log.e("打印银行签购单", "完成打印");

                if (i == com.landicorp.android.eptapi.device.Printer.ERROR_NONE) {
                    logUtil.info("printer", "printer success");
                    Log.e("printer", "print success");
                    JSONObject successMessageObj = new JSONObject();
                    try {
                        successMessageObj.put("code", "0000");
                        successMessageObj.put("message", "打印成功");
                        callbackContext.success(successMessageObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String errMessage = printer.getErrorDescription(i);
                    Log.e("printer", "print failed：" + errMessage);
                    logUtil.info("printer", errMessage);
                    JSONObject errMessageObj = new JSONObject();
                    try {
                        errMessageObj.put("code", "0001");
                        errMessageObj.put("message", errMessage);
                        callbackContext.error(errMessageObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCrash() {

            }
        };

        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }


    /**
     * 销售小票
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printSales(Context context, JSONArray args, CallbackContext callbackContext) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.setAutoTrunc(false);

                // 获取小票主要信息对象
                JSONObject params = args.getJSONObject(0);
                JSONArray storeInfo = params.getJSONArray("storeInfo");
                JSONArray goods = params.getJSONArray("goods");
                JSONArray money = params.getJSONArray("money");
                JSONArray payment = params.getJSONArray("payment");
                JSONArray vip = params.getJSONArray("vip");
                JSONArray qrCode = params.getJSONArray("arCode");
                JSONArray barCode = params.getJSONArray("barCode");
                JSONArray tips = params.getJSONArray("tips");
                String reprint = params.optString("reprint");


                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

                printImg(context, printer);
                if (reprint.length() > 0) {
                    printer.printText(Alignment.CENTER, "【" + reprint + "】\n");
                }
                samllFormatLine(format, printer);
                samllFormat(format, printer);

// 店铺信息
                for (int i = 0; i < storeInfo.length(); i++) {
                    if (i == 0) {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%18s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    } else {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    }
                }
                printDate(printer);
// 条形码
                for (int i = 0; i < barCode.length(); i++) {
                    JSONObject item = barCode.getJSONObject(i);
                    printer.printBarCode(Alignment.CENTER, item.optString("barCodeValue"));
                    format.setAscSize(Format.ASC_DOT7x7);
                    format.setAscScale(Format.ASC_SC1x2);
                    generalFormat(format, printer);
                    printer.printText(String.format("%28s", item.optString("barCodeTitle")));
//          printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                    printer.printText("\n");
                }

                samllFormatLine(format, printer);
                generalFormat(format, printer);
                printer.printText(String.format("%-12s%3s%11s", "货号", "数量", "金额(RMB)\n"));
                samllFormat(format, printer);
// 商品
                for (int i = 0; i < goods.length(); i++) {
                    JSONObject item = goods.getJSONObject(i);
                    printer.printText(
                            String.format("%-" + (24 + formatPrintText(item.optString("goodCode"))) + "s%7s%19s", item.optString("goodCode"), item.optString("goodSum"), item.optString("amount")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

// 金钱
                for (int i = 0; i < money.length(); i++) {
                    JSONObject item = money.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("moneyType"))) + "s%7s%18s", item.optString("moneyType"), item.optString("paymentMethod"), item.optString("montySum")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);

// 付款方式
                generalFormat(format, printer);
                printer.printText(String.format("%-15s%11s", "付款方式", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < payment.length(); i++) {
                    JSONObject item = payment.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("paymentMethod"))) + "s%7s%18s", item.optString("paymentMethod"), item.optString("paymentSum"), item.optString("paymentAmount")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);
// VIP
                for (int i = 0; i < vip.length(); i++) {
                    JSONObject item = vip.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("vipInfo"))) + "s%7s%18s", item.optString("vipInfo"), item.optString("paymentMethod"), item.optString("vpiValue")));
                    printer.printText("\n");
                }
                samllFormatLine(format, printer);
//        printer.printText(Alignment.RIGHT, "总计：" + paymentTotal + "\n");


                for (int i = 0; i < qrCode.length(); i++) {
                    JSONObject item = qrCode.getJSONObject(i);
                    printer.printQrCode(40,
                            new QrCode(item.optString("qrValue"), ECLEVEL_Q),
                            300);
                    samllSamllFormat(format, printer);
                    printer.printText(Alignment.CENTER, item.optString("qrTitle") + "\n");
                }
                samllFormatLine(format, printer);
                // tips
                samllSamllFormat(format, printer);
                for (int i = 0; i < tips.length(); i++) {
                    JSONObject item = tips.getJSONObject(i);
                    printer.printText(Alignment.LEFT, item.optString("tipsId") + "." + item.optString("article") + "\n");
                }
//        printer.printQrCode(0,);
                samllFormat(format, printer);
                printer.printText(Alignment.CENTER, "- - - - - - x - - - - - - - - - - - x - - - - - \n");

                printer.feedLine(3);
                generalFormat(format, printer);
            }

            @Override
            public void onFinish(int i) {
                printFinish(i, callbackContext);
            }

            @Override
            public void onCrash() {
            }
        };

        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }


    /**
     * 销售总结
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printSalesSummary(Context context, JSONArray args, CallbackContext callbackContext) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.setAutoTrunc(false);

                // 获取小票主要信息对象
                JSONObject params = args.getJSONObject(0);

                JSONArray storeInfo = params.getJSONArray("storeInfo");
                JSONArray saleSummaries = params.getJSONArray("saleSummaries");
                String reprint = params.optString("reprint");


                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

                printImg(context, printer);
//        printer.printText(Alignment.CENTER, "============销售总结============\n");
//        printer.printText("============销售总结============\n");
                printer.printText("            销售总结            \n");
                if (reprint.length() > 0) {
                    printer.printText(Alignment.CENTER, "【" + reprint + "】\n");
                }

                samllFormat(format, printer);
                samllFormatLine(format, printer);

                for (int x = 0; x < storeInfo.length(); x++) {

                    if (x == 0) {
                        JSONObject item = storeInfo.getJSONObject(x);
                        printer.printText(String.format("%-20s%18s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    } else {
                        JSONObject item = storeInfo.getJSONObject(x);
                        printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    }
                }
                printDate(printer);


                for (int i = 0; i < saleSummaries.length(); i++) {
                    JSONObject item = saleSummaries.getJSONObject(i);
                    JSONArray bill = item.getJSONArray("bill");
                    String billMachine = item.getString("billMachine");
                    String billTotal = item.getString("billTotal");
                    JSONArray payment = item.getJSONArray("payment");
                    String paymentTotal = item.getString("paymentTotal");


                    samllFormatLine(format, printer);
                    generalFormat(format, printer);
                    printer.printText(String.format("%-12s%14s", "收银机", billMachine + "\n"));
                    printer.printText(String.format("%-8s%s%11s", "单据方式", "单据数量", "金额(RMB)\n"));
                    samllFormat(format, printer);
                    for (int y = 0; y < bill.length(); y++) {
                        JSONObject bills = bill.getJSONObject(y);
                        printer.printText(
                                String.format("%-" + (15 + formatPrintText(bills.optString("billType"))) + "s%7s%18s", bills.optString("billType"), bills.optString("billSum"), bills.optString("billAmount")));
                        printer.printText("\n");
                    }

                    printer.printText(Alignment.RIGHT, "总计：" + billTotal + "\n");

                    samllFormatLine(format, printer);

                    generalFormat(format, printer);
                    printer.printText(String.format("%-8s%s%11s", "付款方式", "单据数量", "金额(RMB)\n"));
                    samllFormat(format, printer);
                    for (int z = 0; z < payment.length(); z++) {
                        JSONObject payments = payment.getJSONObject(z);
                        printer.printText(String.format("%-" + (15 + formatPrintText(payments.optString("paymentMethod"))) + "s%7s%18s", payments.optString("paymentMethod"), payments.optString("paymentSum"), payments.optString("paymentAmount")));
                        printer.printText("\n");
                    }
                    printer.printText(Alignment.RIGHT, "总计：" + paymentTotal + "\n");
                }
                samllFormat(format, printer);
                printer.printText(Alignment.CENTER, "- - - - - - x - - - - - - - - - - - x - - - - - \n");

                printer.feedLine(3);
                generalFormat(format, printer);
            }

            @Override
            public void onFinish(int i) {
                printFinish(i, callbackContext);
            }

            @Override
            public void onCrash() {
            }
        };

        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销售小结
     *
     * @param context
     * @param args
     * @param callbackContext
     */
    public void printSalesSmallSummary(Context context, JSONArray args, CallbackContext callbackContext) {
        progress = new Printer.Progress() {
            @Override
            public void doPrint(Printer printer) throws Exception {
                printer.setAutoTrunc(false);

                // 获取小票主要信息对象

                JSONObject params = args.getJSONObject(0);

                JSONArray storeInfo = params.getJSONArray("storeInfo");
                JSONArray bill = params.getJSONArray("bill");
                String billTotal = params.getString("billTotal");
                JSONArray payment = params.getJSONArray("payment");
                String paymentTotal = params.getString("paymentTotal");
                String reprint = params.optString("reprint");


                /** 设置打印格式 */
                Format format = new Format();
                /** 西文字符打印， 此处使用 5x7点， 1倍宽&&2倍高打印签购单标题  */
                format.setAscSize(Format.ASC_DOT7x7);
                format.setAscScale(Format.ASC_SC1x2);

                printImg(context, printer);
                printer.printText("            销售小结            \n");
                if (reprint.length() > 0) {
                    printer.printText(Alignment.CENTER, "【" + reprint + "】\n");
                }
                samllFormat(format, printer);
                samllFormatLine(format, printer);
                for (int i = 0; i < storeInfo.length(); i++) {

                    if (i == 0) {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%18s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    } else {
                        JSONObject item = storeInfo.getJSONObject(i);
                        printer.printText(String.format("%-20s%24s", item.optString("storeInfoName"), item.optString("storeInfoValue")));
                        printer.printText("\n");
                    }
                }
                printDate(printer);
                samllFormatLine(format, printer);
                generalFormat(format, printer);
                printer.printText(String.format("%-8s%s%11s", "单据方式", "单据数量", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < bill.length(); i++) {
                    JSONObject item = bill.getJSONObject(i);
                    printer.printText(
                            String.format("%-" + (15 + formatPrintText(item.optString("billType"))) + "s%7s%18s", item.optString("billType"), item.optString("billSum"), item.optString("billAmount")));
                    printer.printText("\n");
                }

                printer.printText(Alignment.RIGHT, "总计：" + billTotal + "\n");

                samllFormatLine(format, printer);

                generalFormat(format, printer);
                printer.printText(String.format("%-8s%s%11s", "付款方式", "单据数量", "金额(RMB)\n"));
                samllFormat(format, printer);
                for (int i = 0; i < payment.length(); i++) {
                    JSONObject item = payment.getJSONObject(i);
                    printer.printText(String.format("%-" + (15 + formatPrintText(item.optString("paymentMethod"))) + "s%7s%18s", item.optString("paymentMethod"), item.optString("paymentSum"), item.optString("paymentAmount")));
                    printer.printText("\n");
                }
                printer.printText(Alignment.RIGHT, "总计：" + paymentTotal + "\n");

                samllFormat(format, printer);

                printer.printText(Alignment.CENTER, "- - - - - - x - - - - - - - - - - - x - - - - - \n");

                printer.feedLine(3);
                generalFormat(format, printer);
            }

            //      @Override
            public void onFinish(int i) {
//        printFinish(i, callbackContext);
            }

            @Override
            public void onCrash() {
            }
        };

        try {
            Beeper.startBeep(100);
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }


    // 打印logo
    public static void printImg(Context context, Printer printer) {
        //        头部图片
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open("mixc.bmp");
            printer.printImage(0, in);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 打印的日期
    public static void printDate(Printer printer) {

        SimpleDateFormat transactionFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat printDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String transationDate = transactionFormatter.format(curDate);
        String printDate = printDateFormatter.format(curDate);
        try {
            printer.printText(String.format("%-18s%27s", "打印日期", printDate + "\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 格式打印格式 暂时
    public static int formatPrintText(String s) {
        int i = 7 - s.length();
        return i;
    }

    // 正常字体格式
    public static void generalFormat(Format format, Printer printer) {
        format.setAscScale(Format.ASC_SC1x1);
        format.setAscSize(Format.ASC_DOT24x12);
        format.setHzScale(Format.HZ_SC1x1);
        format.setHzSize(HZ_DOT24x24);
        try {
            printer.setFormat(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 处理字体格式-小字体1*2
    public static void samllFormat(Format format, Printer printer) {
        format.setAscScale(Format.ASC_SC1x2);
        format.setAscSize(Format.ASC_DOT16x8);
        format.setHzScale(Format.HZ_SC1x2);
        format.setHzSize(HZ_DOT16x16);
        try {
            printer.setFormat(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 处理字体格式-小字体1*1
    public static void samllSamllFormat(Format format, Printer printer) {
        format.setAscScale(Format.ASC_SC1x1);
        format.setAscSize(Format.ASC_DOT16x8);
        format.setHzScale(Format.HZ_SC1x1);
        format.setHzSize(HZ_DOT16x16);
        try {
            printer.setFormat(format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 打印横线
    public static void samllFormatLine(Format format, Printer printer) {
        format.setAscScale(Format.ASC_SC1x2);
        format.setAscSize(Format.ASC_DOT16x8);
        format.setHzScale(Format.HZ_SC1x2);
        format.setHzSize(HZ_DOT16x16);
        try {
            printer.setFormat(format);
            printer.printText(Alignment.CENTER, "- - - - - - - - - - - - - - - - - - - - - - - - \n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取打印机错误代码表示的信息
    public String getDescribe(int error) {
        switch (error) {
            case com.landicorp.android.eptapi.device.Printer.ERROR_BMBLACK:
                String s = printer.getErrorDescription(error);
                return "ERROR_BMBLACK";
            case com.landicorp.android.eptapi.device.Printer.ERROR_BUFOVERFLOW:
                return "ERROR_BUFOVERFLOW";
            case com.landicorp.android.eptapi.device.Printer.ERROR_BUSY:
                return "ERROR_BUSY";
            case com.landicorp.android.eptapi.device.Printer.ERROR_COMMERR:
                return "ERROR_COMMERR";
            case com.landicorp.android.eptapi.device.Printer.ERROR_CUTPOSITIONERR:
                return "ERROR_CUTPOSITIONERR";
            case com.landicorp.android.eptapi.device.Printer.ERROR_HARDERR:
                return "ERROR_HARDERR";
            case com.landicorp.android.eptapi.device.Printer.ERROR_LIFTHEAD:
                return "ERROR_LIFTHEAD";
            case com.landicorp.android.eptapi.device.Printer.ERROR_LOWTEMP:
                return "ERROR_LOWTEMP";
            case com.landicorp.android.eptapi.device.Printer.ERROR_LOWVOL:
                return "ERROR_LOWVOL";
            case com.landicorp.android.eptapi.device.Printer.ERROR_MOTORERR:
                return "ERROR_MOTORERR";
            case com.landicorp.android.eptapi.device.Printer.ERROR_NOBM:
                return "ERROR_NOBM";
            case com.landicorp.android.eptapi.device.Printer.ERROR_NONE:
                return "ERROR_NONE";
            case com.landicorp.android.eptapi.device.Printer.ERROR_OVERHEAT:
                return "ERROR_OVERHEAT";
            case com.landicorp.android.eptapi.device.Printer.ERROR_PAPERENDED:
                return "ERROR_PAPERENDED";
            case com.landicorp.android.eptapi.device.Printer.ERROR_PAPERENDING:
                return "ERROR_PAPERENDING";
            case com.landicorp.android.eptapi.device.Printer.ERROR_PAPERJAM:
                return "ERROR_PAPERJAM";
            case com.landicorp.android.eptapi.device.Printer.ERROR_PENOFOUND:
                return "ERROR_PENOFOUND";
            case com.landicorp.android.eptapi.device.Printer.ERROR_WORKON:
                return "ERROR_WORKON";
            case Printer.ERROR_CUTCLEAN:
                return "ERROR_CUTCLEAN";
            case Printer.ERROR_CUTERROR:
                return "ERROR_CUTERROR";
            case Printer.ERROR_CUTFAULT:
                return "ERROR_CUTFAULT";
            case Printer.ERROR_OPENCOVER:
                return "ERROR_OPENCOVER";
            default:
                return "UNKNOWN ERROR";
        }
    }

    /**
     * 打印成功调用方法
     *
     * @param i
     * @param callbackContext
     */
    public void printFinish(int i, CallbackContext callbackContext) {
        if (i == com.landicorp.android.eptapi.device.Printer.ERROR_NONE) {
            logUtil.info("printer", "printer success");
            Log.e("printer", "print success");
            JSONObject successMessageObj = new JSONObject();
            try {
                successMessageObj.put("code", "0000");
                successMessageObj.put("message", "打印成功");
                callbackContext.success(successMessageObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String errMessage = printer.getErrorDescription(i);
            Log.e("printer", "print failed：" + errMessage);
            logUtil.info("printer", errMessage);
            JSONObject errMessageObj = new JSONObject();
            try {
                errMessageObj.put("code", "0001");
                errMessageObj.put("message", errMessage);
                callbackContext.error(errMessageObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
