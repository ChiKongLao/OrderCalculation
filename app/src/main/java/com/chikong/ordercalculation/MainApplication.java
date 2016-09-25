package com.chikong.ordercalculation;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.SyncStateContract;
import android.telephony.TelephonyManager;

import com.chikong.ordercalculation.utils.MobclickAgentUtils;

import java.io.File;

public class MainApplication extends Application {
	
	private static Context context;
	private static MainApplication instance;

	public MainApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		
//		initImageLoader(getApplicationContext());
		
//		CrashHandler crashHandler = CrashHandler.getInstance();  
//        crashHandler.init(getApplicationContext());

		// 初始化友盟
		MobclickAgentUtils.init(this);

	}
	
	public static Context getContext() {
		return context;
	}

	public static MainApplication getInstance() {
		return instance;
	}
	
	public String getAppVersionName() {
		PackageManager manager = this.getPackageManager();
		try { 
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void initImageLoader(Context context) {
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//				context).threadPriority(Thread.NORM_PRIORITY - 2)
//				.denyCacheImageMultipleSizesInMemory()
//				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.build();
//		ImageLoader.getInstance().init(config);
//	}

//	public String getUniqueId() {
//		String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//    	WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        imei = imei+"_"+info.getMacAddress();
//        return imei;
//	}

}
