package com.chikong.ordercalculation.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 打印Log日志带总开关的封装类
 */
public class LogHelper {
	/**是否打开调试模式*/
	public static boolean isOpen = true;
	static String MyTag = "OrderCalculation";
	static final String CLASS_METHOD_LINE_FORMAT = "%s.%s()  Line:%d";

	private static String MYLOG_PATH_SDCARD_DIR = "/sdcard/ordercalculation/logs";// 日志文件在sdcard中的路径
	private static Boolean TRACE_REQUEST_TO_FILE = true;// 日志写入文件开关
	private static String MY_LOG_REQUEST = "request_trace_log";// 本类输出的日志文件名称

	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 日志的输出格式

	// 可跟踪的打印
	public static void trace(Object msg) {
		if (isOpen == true) {
			StackTraceElement traceElement = Thread.currentThread()
					.getStackTrace()[3];
			String logText = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getClassName(), traceElement.getMethodName(),
					traceElement.getLineNumber());
			LogHelper.i("信息来自:" + logText);
			LogHelper.i(String.valueOf(msg));
			LogHelper.i(" ");
		}
	}

	// 黑色
	public static void v(String msg) {
		if (isOpen == true) {
			Log.v(MyTag, msg);
		}
	}

	// 蓝色，debug
	public static void d(String msg) {
		if (isOpen == true) {
			Log.d(MyTag, msg);
		}
	}

	// 绿色，information
	public static void i(String msg) {
		if (isOpen == true) {
			Log.i(MyTag, msg);
		}
	}

	// 橙色，warning
	public static void w(String msg) {
		if (isOpen == true) {
			Log.w(MyTag, msg);
		}
	}

	// 红色，error
	public static void e(String msg) {
		if (isOpen == true) {
			Log.e(MyTag, msg);
		}
	}

	public static void traceRequest(String str) {
		if (isOpen) {
			StackTraceElement traceElement = Thread.currentThread()
					.getStackTrace()[3];//从堆栈信息中获取当前被调用的方法信息
			String logText = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getClassName(), traceElement.getMethodName(),
					traceElement.getLineNumber(), traceElement.getFileName());
			Log.i(MyTag, logText + "->" + str);//打印Log
			if (TRACE_REQUEST_TO_FILE && android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				writeLogtoFile(MY_LOG_REQUEST, MyTag, logText + "->" + str);
			}
		}
	}

	/**
	 * 打开日志文件并写入日志
	 *
	 * @return
	 **/
	private static void writeLogtoFile(String filename, String tag, String text) {// 新建或打开日志文件
		File filePath = new File(MYLOG_PATH_SDCARD_DIR);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		Date nowtime = new Date();
		String needWriteFiel = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    " + tag + "    " + text;
		File file = new File(MYLOG_PATH_SDCARD_DIR, filename + needWriteFiel + ".log");
		try {
			FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 仅作测试使用
	 *
	 * @param str
	 */
	public static void test(String str) {
		Log.w("test", str);
	}
}

