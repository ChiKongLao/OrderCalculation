package com.chikong.ordercalculation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.chikong.ordercalculation.MainApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SettingUtils {

	// 启动相关
	/** 是否首次安装 */
	public static final String FIRST_INSTALL = "FIRST_INSTALL";
	/** 最后启动的版本号 */
	public static final String LAST_LAUNCH_VERSION = "LAST_LAUNCH_VERSION";

	// 界面数据相关
	/** 最小起送价 */
	public static final String MIN_TOTAL_PRICE = "MIN_TOTAL_PRICE";
	/** 运费 */
	public static final String FREIGHT_PRICE = "FREIGHT_PRICE";
	/** 最大拆单数;  0为无限制 */
	public static final String ORDER_COUNT = "ORDER_COUNT";
	/** 每人最大红包使用数 */
	public static final String REDPACKET_COUNT = "REDPACKET_COUNT";
	/** 其它优惠 */
	public static final String OTHER_CUT = "OTHER_CUT";
	/** 使用红包 */
	public static final String IS_USE_REDPACKETS = "IS_USE_REDPACKETS";
	/** 添加包装费到满减计算 */
	public static final String IS_ADD_PACKINGFEE_2_CAL = "IS_ADD_PACKINGFEE_2_CAL";
	/** 添加运费到满减计算 */
	public static final String IS_ADD_FREIGHT_2_CAL = "IS_ADD_FREIGHT_2_CAL";
	/** 计算优惠类型 */
	public static final String CUT_TYPE = "CUT_TYPE";

//	/**每单限特价商品个数*/
//	private static int mMaxSpecialPriceCount = 2;

	public static String getSetting(String name,
			String defaultValue) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
		String value = prefs.getString(name, defaultValue);
		return value;
	}

	public static boolean setSetting(String name, String value) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		Editor editor = prefs.edit();
		editor.putString(name, value);
		return editor.commit();
	}
	
	public static boolean setSetting(String name, long value) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		Editor editor = prefs.edit();
		editor.putLong(name, value);
		return editor.commit();
	}
	
	public static long getSetting(String name,
			long defaultValue) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		long value = prefs.getLong(name, defaultValue);
		return value;
	}


	public static boolean getSetting(String name,
			boolean defaultValue) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		boolean value = prefs.getBoolean(name, defaultValue);
		return value;
	}

	public static boolean setSetting(String name, boolean value) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit();
	}

	public static int getSetting(String name, int defaultValue) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		int value = prefs.getInt(name, defaultValue);
		return value;
	}

	public static boolean setSetting(String name, int value) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		Editor editor = prefs.edit();
		editor.putInt(name, value);
		return editor.commit();
	}
	
	/**
     * desc:保存对象
     * @param key
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     * modified:	
     */
    public static void saveObject(String key ,Object obj){
        try {
            // 保存对象
        	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
            Editor sharedata = prefs.edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = MyBase64.encodeToString(bos.toByteArray());
            //保存该16进制数组
            sharedata.putString(key, bytesToHexString);
            sharedata.commit();
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.trace("保存obj失败");
        }
    }
 
    /**
     * desc:获取保存的Object对象
     * @param key
     * @return
     * modified:	
     */
    public static Object readObject(String key){
        try {
        	final SharedPreferences sharedata = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
            if (sharedata.contains(key)) {
                 String string = sharedata.getString(key, "");
                 if(TextUtils.isEmpty(string)){
                     return null;
                 }else{
                     //将16进制的数据转为数组，准备反序列化
                     byte[] stringToBytes = MyBase64.decode(string);
                       ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
                       ObjectInputStream is=new ObjectInputStream(bis);
                       //返回反序列化得到的对象
                       Object readObject = is.readObject();
                       return readObject;
                 }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;
        
    }

    public static double getSetting(String name, float defaultValue) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		float value = prefs.getFloat(name, defaultValue);
		return value;
	}

	public static boolean setSetting(String name, float value) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainApplication.getContext());
		Editor editor = prefs.edit();
		editor.putFloat(name, value);
		return editor.commit();
	}
	
	

}
