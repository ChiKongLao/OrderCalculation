package com.chikong.ordercalculation.utils;

import android.util.DisplayMetrics;

import com.chikong.ordercalculation.MainApplication;

public class DensityUtil {

	private static final String TAG = DensityUtil.class.getSimpleName();

	// 当前屏幕的densityDpi
	private static float dmDensityDpi = 0.0f;
	private static DisplayMetrics dm;
	private static float scale = 0.0f;
	
	private static DensityUtil instance;
	
	public synchronized static DensityUtil getInstance() {
		if(instance == null)  {
			instance = new DensityUtil();
		}
		return instance;
	}

	/**
	 * 
	 * 根据构造函数获得当前手机的屏幕系数
	 * 
	 * */
	private DensityUtil() {
		// 获取当前屏幕
		dm = new DisplayMetrics();
		dm = MainApplication.getContext().getResources().getDisplayMetrics();
		// 设置DensityDpi
		setDmDensityDpi(dm.densityDpi);
		// 密度因子
		scale = getDmDensityDpi() / 160;
	}

	/**
	 * 当前屏幕的density因子
	 *
	 * @retrun DmDensity Getter
	 * */
	public float getDmDensityDpi() {
		return dmDensityDpi;
	}

	/**
	 * 当前屏幕的density因子
	 *
	 * @retrun DmDensity Setter
	 * */
	public void setDmDensityDpi(float dmDensityDpi) {
		DensityUtil.dmDensityDpi = dmDensityDpi;
	}

	/**
	 * 密度转换像素
	 * */
	public int dip2px(float dipValue) {

		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * 像素转换密度
	 * */
	public int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}

	@Override
	public String toString() {
		return " dmDensityDpi:" + dmDensityDpi;
	}
}
