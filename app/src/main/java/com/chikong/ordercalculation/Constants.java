package com.chikong.ordercalculation;


/**常量
 **  Created by ChiKong on 2016/4/6.
 */
public class Constants {
    /**是否在测试模式*/
    public static final boolean isTest = true;

	/**商品项*/
    public static final int TYPE_PRODUCT = 0;
	/**满减项*/
    public static final int TYPE_FULL_CUT = 1;
	/**红包项*/
    public static final int TYPE_RED_PACKETS = 2;;
	/**数量项*/
    public static final int TYPE_COUNT = 3;
	/**包装费*/
    public static final int TYPE_PACKINGFEE = 4;

	/**满多少*/
    public static final int TYPE_FULL = 0;
	/**减多少*/
    public static final int TYPE_CUT = 1;

    /**平均减*/
    public static final int TYPE_CUT_AVERAGE = 0;
	/**按百分比减*/
    public static final int TYPE_CUT_PERCENT = 1;
    /** fragment类型，满减 */
    public static final int FRAGMENT_TYPE_FULLCUT = 0;
    /** fragment类型，红包 */
    public static final int FRAGMENT_TYPE_REDPACKET = 1;


    // 友盟统计
    public static String UMENG_APPKEY = "570f3da1e0f55adc3300266e";
    public static String UMENG_CHANNEL = "ORDERCALCULATION_PLATFORM";


    public static int DB_VERSION = 2;

}
