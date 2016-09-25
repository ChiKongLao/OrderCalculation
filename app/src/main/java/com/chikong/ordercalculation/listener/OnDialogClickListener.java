package com.chikong.ordercalculation.listener;

import android.content.DialogInterface;
import android.view.View;

/**
 * 点击Dialog按钮时回调
 * Created by Administrator on 16/02/08.
 */
public interface OnDialogClickListener {


    void onPositiveButtonClick(DialogInterface dialog, int which,Object object);
    void onNegativeButtonClick(DialogInterface dialog, int which,Object object);
    void onNeutralButtonClick(DialogInterface dialog, int which,Object object);
}
