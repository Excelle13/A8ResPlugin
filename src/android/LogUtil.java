package com.ttebd.a8ResPlugin;

import android.os.Environment;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LogUtil {

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 5;
    private static final String DEFAULT_LOG_DIR = Environment.getExternalStorageDirectory()
            + File.separator + "myc" + File.separator + "log"
            + File.separator;
    private static final String DEFAULT_LOG_FILE_NAME = "MyApp.log";
    private static final String TAG = "Log4jConfigure";
    // 对应AndroidManifest文件中的package
    private static final String PACKAGE_NAME = "com.king.logtest";
    private static final Logger logger = Logger.getLogger("dfghj");
    final Logger root = Logger.getRootLogger(); // 获取跟日志级别

    final LogConfigurator logConfigurator = new LogConfigurator();
    boolean isTody = false;
    Date nowTime;
    SimpleDateFormat timeCompareTimeSdf = new SimpleDateFormat("yyyy-MM-dd");


    public void init() {

        logConfigure();

        Logger log = Logger.getLogger("这是测试log Tag");
        log.info("this is info log");
        log.debug("this is debug log");
        log.error("this is err log");

        System.out.println("log-level--" + Logger.getRootLogger());

        //与原来的日志文件比较，判断是否存在当天的日志


    }

    public boolean checkLogFile() {
        nowTime = new Date();
        try {
            File sceneFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "myc" + File.separator + "log");

            System.out.println("与文件名比较的格式化时间:" + timeCompareTimeSdf.format(nowTime));

            File[] files = sceneFile.listFiles();

            if (null != files) {

                for (int i = 0; i < files.length; i++) {

                    System.out.println("文件夹下的文件：" + files[i].getName());
                    System.out.println("比较结果：" + files[i].getName().indexOf(timeCompareTimeSdf.format(nowTime)));
                    if (files[i].getName().indexOf(timeCompareTimeSdf.format(nowTime)) != -1) {
                        isTody = true;  // 设置找到当天日志的 flag
                        return isTody;
//            System.out.println("找到与当天匹配的文件，文件名为:" + files[i].getName());
                    } else {
//            System.out.println("没有找到当天的log");
                        isTody = false; // 设置没有找到当天日志的 flag
                    }
                }
            }
//      boolean isTody = compare("", timeCompareTimeSdf.format(nowtime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isTody;
    }

    public boolean compare(String time1, String time2) throws ParseException {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //将字符串形式的时间转化为Date类型的时间
        Date a = sdf.parse(time1);
        System.out.println("a---" + a);
        Date b = sdf.parse(time2);
        System.out.println("b---" + b);
        //Date类的一个方法，如果a早于b返回true，否则返回false
        if (a.before(b))
            return true;
        else
            return false;
        /*
         * 如果你不喜欢用上面这个太流氓的方法，也可以根据将Date转换成毫秒
        if(a.getTime()-b.getTime()<0)
            return true;
        else
            return false;
        */
    }

    public void testLog() {


        Logger log = Logger.getLogger("这是测试的log");
        //写 info 日志
        log.info("不知道呀就是测试一下啊");
        log.info("不知道呀就是测试一下啊");
        log.error("不知道呀就是测试一下啊");
        log.info("不知道呀就是测试一下啊");
        log.debug("不知道呀就是测试一下啊");
        log.info("不知道呀就是测试一下啊");
        log.info("不知道呀就helwleojskdl测试一下啊");
        log.info("不知道呀就helwleojskdl测试一下啊");
        log.info("不知道呀就helwleojskdl测试一下啊");
        log.info("不知道呀就helwleojskdl测试一下啊");
    }

    public void logConfigure() {
        nowTime = new Date();
        SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd");
        String needWriteMessage = myLogSdf.format(nowTime);
        //日志文件路径地址:SD卡下myc文件夹log文件夹的test文件
        String fileName = DEFAULT_LOG_DIR + needWriteMessage + ".txt";
        //设置文件名
        logConfigurator.setFileName(fileName);
        //设置root日志输出级别 默认为DEBUG
        logConfigurator.setRootLevel(Level.DEBUG);
        // 设置日志输出级别
        logConfigurator.setLevel("org.apache", Level.INFO);
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        //设置输出到控制台的文字格式 默认%m%n
        logConfigurator.setLogCatPattern("%m%n");
        //设置总文件大小
        logConfigurator.setMaxFileSize(MAX_FILE_SIZE);
        //设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(1);
        //设置所有消息是否被立刻输出 默认为true,false 不输出
        logConfigurator.setImmediateFlush(true);
        //是否本地控制台打印输出 默认为true ，false不输出
        logConfigurator.setUseLogCatAppender(true);
        //设置是否启用文件附加,默认为true。false为覆盖文件
        logConfigurator.setUseFileAppender(true);
        //设置是否重置配置文件，默认为true
        logConfigurator.setResetConfiguration(true);
        //是否显示内部初始化日志,默认为false
        logConfigurator.setInternalDebugging(false);

        logConfigurator.configure();

    }

    public void info(String prefix, String message) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.info(message);
    }

    public void info(String prefix, String message, Throwable exception) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.info(message, exception);
    }

    public void debug(String prefix, String message, Throwable exception) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.info(message, exception);
    }

    public void debug(String prefix, String message) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.debug(message);
    }

    public void warn(String prefix, String message, Throwable exception) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.warn(message, exception);
    }

    public void warn(String prefix, String message) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.warn(message);
    }

    public void error(String prefix, String message, Throwable exception) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.error(message, exception);
    }

    public void error(String prefix, String message) {
        logConfigure();
        Logger log = Logger.getLogger(prefix);
        log.error(message);
    }
}
