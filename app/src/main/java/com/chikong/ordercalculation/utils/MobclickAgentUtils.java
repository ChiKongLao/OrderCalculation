package com.chikong.ordercalculation.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.SyncStateContract;

import com.chikong.ordercalculation.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class MobclickAgentUtils {
	
	public static final String EVENT_OPEN = "event_open";
	public static final String EVENT_CAL = "event_cal";
	public static final String EVENT_FEEBACK = "event_feeback";

	
	public static void init(Context context){

        AnalyticsConfig.setAppkey(context, Constants.UMENG_APPKEY);
        AnalyticsConfig.setChannel(Constants.UMENG_CHANNEL);
		// 使用默认的Activity页面统计方式
		MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(context);
//		 开启友盟自带错误统计
		MobclickAgent.setCatchUncaughtExceptions(true);
		// 开户调试模式
        MobclickAgent.setDebugMode(true);
	}
	
	/**
	 * 用来统计应用时长的(也就是Session时长,当然还包括一些其他功能)
	 * @param act
	 */
	public static void onResume(Activity act) {
		MobclickAgent.onResume(act);
	}
	/**
	 * 用来统计应用时长的(也就是Session时长,当然还包括一些其他功能)
	 * @param act
	 */
	public static void onPause(Activity act) {
		MobclickAgent.onPause(act);
	}

	/**
	 * 用来统计页面跳转的
	 * @param page
	 */
	public static void onPageStart(String page) {
		LogHelper.trace("### onPageStart ### page=" + page);
		MobclickAgent.onPageStart(page);
	}

	/**
	 * 用来统计页面跳转的
	 * @param page
	 */
	public static void onPageEnd(String page) {
		LogHelper.trace("### onPageEnd ### page=" + page);
		MobclickAgent.onPageEnd(page);
	}

	/**
	 * 统计事件的发生次数(不要使用中文和特殊字符且不能使用英文句号“.”您可以使用下划线“_”)
	 * @param act
	 * @param eventId
	 */
	public static void onEvent(Activity act, String eventId) {
		MobclickAgent.onEvent(act, eventId);
	}
	/**
	 * 统计点击行为的各属性发生次数(不要使用中文和特殊字符且不能使用英文句号“.”您可以使用下划线“_”)
	 * @param act
	 * @param eventId
	 * @param map
	 */
	public static void onEvent(Activity act, String eventId, HashMap<String,String> map) {
		MobclickAgent.onEvent(act, eventId,map);
	}

	/**
	 * 提交错误报告
	 * @param context
	 * @param errorString
     */
	public static void reportError(Context context,String errorString){
		MobclickAgent.reportError(context,errorString);
	}
	
}
