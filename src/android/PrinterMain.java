package com.ttebd.a8ResPlugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.landicorp.android.eptapi.device.Printer;
import com.landicorp.android.eptapi.device.Printer.Format;
import com.landicorp.android.eptapi.exception.RequestException;
import com.landicorp.android.eptapi.utils.ImageTransformer;
import com.landicorp.android.eptapi.utils.QrCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import static com.landicorp.android.eptapi.device.Printer.Format.HZ_DOT24x24;
import static com.landicorp.android.eptapi.utils.QrCode.ECLEVEL_Q;

public class PrinterMain extends com.ttebd.a8ResPlugin.DeviceBase {

    private com.landicorp.android.eptapi.device.Printer.Progress progress;
    private List<com.landicorp.android.eptapi.device.Printer.Step> stepList;
    private Context context;

    public com.ttebd.a8ResPlugin.DeviceBase deviceBase;

    public int getPrinterStatus() {
        try {
            int status = Printer.getInstance().getStatus();
            return status;
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void init() {
        stepList = new ArrayList<com.landicorp.android.eptapi.device.Printer.Step>();
    }

    public boolean addText() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                printer.setAutoTrunc(false);
                Format format = new Format();

                printer.printText("百分号打印测试：%%12\n");
                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(HZ_DOT24x24);
                printer.setFormat(format);
                printer.printMid("福建联迪商用设备有限公司\n");

                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT16x8);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(Format.HZ_DOT16x16);
                printer.setFormat(format);
                Printer.Alignment alignment = Printer.Alignment.LEFT;
                printer.printText(alignment, "福建联迪商用设备有限公司\n");
                printer.printText(alignment, "www.landicorp.com\n");

                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(HZ_DOT24x24);
                printer.setFormat(format);
                alignment = Printer.Alignment.CENTER;
                printer.printText(alignment, "福建联迪商用设备有限公司\n");
                printer.printText(alignment, "www.landicorp.com\n");

                format.setAscScale(Format.ASC_SC2x2);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC2x2);
                format.setHzSize(HZ_DOT24x24);
                printer.setFormat(format);
                alignment = Printer.Alignment.RIGHT;
                printer.printText(alignment, "福建联迪\n");
                printer.printText(alignment, "landicorp\n");

                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT16x8);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(Format.HZ_DOT16x16);
                printer.printMixText(format, "有电子支付的");
                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(HZ_DOT24x24);
                printer.printMixText(format, "地方就有");
                format.setAscScale(Format.ASC_SC2x2);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC2x2);
                format.setHzSize(HZ_DOT24x24);
                printer.printMixText(format, "联迪商用\n");

                format.setAscScale(Format.ASC_SC1x1);
                format.setAscSize(Format.ASC_DOT24x12);
                format.setHzScale(Format.HZ_SC1x1);
                format.setHzSize(HZ_DOT24x24);
                printer.printText("有电子支付的地方就有\u0007联迪商用\u0008\n");
            }
        });
        return true;
    }


    public boolean addBitmap() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                InputStream inputStream = context.getAssets().open("test3.bmp");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap.getWidth() > Printer.getInstance().getValidWidth()) {
                    bitmap = scaleBitmap(bitmap, 0);
                    if (bitmap == null) {
                        return;
                    }
                }
                ByteArrayOutputStream outputStream = ImageTransformer.convert1BitBmp(bitmap);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                printer.printImage(com.landicorp.android.eptapi.device.Printer.Alignment.LEFT, inputStream);
                // 若是打印大位图，需使用printer.printMonochromeBmp接口
//                printer.printMonochromeBmp(0, outputStream.toByteArray());
                inputStream.close();
                outputStream.close();
            }
        });
        return true;
    }

    private Bitmap scaleBitmap(Bitmap bm, int offset) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        final int MAX_WIDTH = Printer.getInstance().getValidWidth();
        int newWidth = MAX_WIDTH - offset;
        if (newWidth <= 0) {
            return null;
        }
        int newHeight = height;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbmp = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    public boolean addBarcode() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                printer.printBarCode("1234567890");
            }
        });
        return true;
    }

    public boolean addQRcode() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                printer.printQrCode(com.landicorp.android.eptapi.device.Printer.Alignment.CENTER,
                        new QrCode("福建联迪商用设备有限公司", ECLEVEL_Q),
                        200);
            }
        });
        return true;
    }

    public boolean feedLine(final int line) {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                printer.feedLine(line);
            }
        });
        return true;
    }

    public boolean cutPaper() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return false;
        }
        stepList.add(new com.landicorp.android.eptapi.device.Printer.Step() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                printer.cutPaper();
            }
        });
        return true;
    }

    public void startPrint() {
        if (stepList == null) {
            Log.e("printer", "printer has not inited!");
            return;
        }
        progress = new com.landicorp.android.eptapi.device.Printer.Progress() {
            @Override
            public void doPrint(com.landicorp.android.eptapi.device.Printer printer) throws Exception {
                // never call
            }

            @Override
            public void onFinish(int error) {
                stepList.clear();
                if (error == com.landicorp.android.eptapi.device.Printer.ERROR_NONE) {
                    Log.e("printer", "print success");
                } else {
                    String errorDes = getDescribe(error);
                    Log.e("printer", "print failed：" + errorDes);
                }
            }

            @Override
            public void onCrash() {
                stepList.clear();
                unbindDeviceService();
            }
        };
        for (com.landicorp.android.eptapi.device.Printer.Step step : stepList) {
            progress.addStep(step);
        }
        try {
            progress.start();
        } catch (RequestException e) {
            e.printStackTrace();
            Log.e("printer", "request exception has ocurred");
        }
    }

    public String getDescribe(int error) {
        switch (error) {
            case com.landicorp.android.eptapi.device.Printer.ERROR_BMBLACK:
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
}
