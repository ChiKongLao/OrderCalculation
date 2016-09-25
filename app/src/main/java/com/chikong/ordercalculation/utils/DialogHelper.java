package com.chikong.ordercalculation.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.MainApplication;
import com.chikong.ordercalculation.R;
import com.chikong.ordercalculation.listener.OnDialogClickListener;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;

import java.util.Timer;
import java.util.TimerTask;

public class DialogHelper {
    // 单例模式
    private static DialogHelper mHelp = new DialogHelper();
    // 进度条对话框
    private ProgressDialog mProgressDialog;

    public static DialogHelper getInstance() {
        return mHelp;
    }

    /**
     * 对话框，必须在UI线程调用，避免产生其它错误
     *
     * @param activity
     * @param content
     */
    public void showWaitDialog(final Activity activity,final String content) {
        showWaitDialog(activity, "", content, true);
    }

    /**
     * 对话框，必须在UI线程调用，避免产生其它错误
     *
     * @param activity
     * @param title
     * @param content
     */
    public void showWaitDialog(final Activity activity, final String title, final String content) {
        showWaitDialog(activity, title, content, true);
    }
    /**
     * 对话框，必须在UI线程调用，避免产生其它错误
     *
     * @param activity
     * @param content
     * @param cancelable
     */
    public void showWaitDialog(final Activity activity,final String content, final boolean cancelable) {
        showWaitDialog(activity,"",content,cancelable);
    }

    /**
     * 对话框，必须在UI线程调用，避免产生其它错误
     *
     * @param activity
     * @param title
     * @param content
     * @param cancelable
     */
    public void showWaitDialog(final Activity activity, final String title, final String content, final boolean cancelable) {
        // 初始化
        // Activity被销毁则跳过
        if (!isActivityExist(activity)) return;
        try {
            if (isWaitDialogShow()) {
                // 设置标题
                if (title.length() != 0) mProgressDialog.setTitle(title);
                mProgressDialog.setMessage(content);
            } else {
                mProgressDialog = new ProgressDialog(activity);
                // 设置风格为圆形进度条
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // 设置标题
                if (title.length() != 0) mProgressDialog.setTitle(title);
                mProgressDialog.setMessage(content);
                //Call requires API level 11 (current min is 9): android.app.ProgressDialog#setProgressNumberFormat
//                if (android.os.Build.VERSION.SDK_INT >= 11) {
//                    mProgressDialog.setProgressNumberFormat("%1d kb/%2d kb");
//                }
                // 设置进度条是否为不明确 false 就是不设置为不明确
                mProgressDialog.setIndeterminate(false);
                // 用户按back键不能消失对话框
                mProgressDialog.setCancelable(cancelable);
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示添加商品对话框
     * @param activity
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddProductDialog(final Activity activity , final OnDialogClickListener dialogClickListener){
        return showAddProductDialog(activity,null,dialogClickListener);
    }
    /**
     * 显示添加商品对话框
     * @param activity
     * @param product
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddProductDialog(final Activity activity,
                                                     final Product product,
                                                     final OnDialogClickListener dialogClickListener){
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_item_product, null);
        final EditText productEt = (EditText) view.findViewById(R.id.product_et);
        final EditText countEt = (EditText) view.findViewById(R.id.count_et);
        final EditText packingEt = (EditText) view.findViewById(R.id.packing_fee_et);
        final CheckBox needCutCb = (CheckBox)view.findViewById(R.id.checkBox);
        String title = "添加价格";
        if (product != null){
            productEt.setText(product.getPrice()+"");
            countEt.setText(product.getCount()+"");
            if (product.getPackingFee() != 0F) packingEt.setText(product.getPackingFee()+"");
            needCutCb.setChecked(product.isNeedCut());
            title = "修改价格";
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tmpWhich = which;
                // 如果是再记一笔, 先执行positive , 再执行其它操作
                if (which == DialogInterface.BUTTON_NEUTRAL){
                    which = DialogInterface.BUTTON_POSITIVE;
                }
                if (which == DialogInterface.BUTTON_POSITIVE){
                    String priceString = productEt.getText().toString();
                    String countString = countEt.getText().toString();
                    String packingString = packingEt.getText().toString();
                    float price = priceString.length() == 0 ? 0 : Float.valueOf(priceString);
                    int count = countString.length() == 0 ? 1 : Integer.valueOf(countString);
                    float packing = packingString.length() == 0 ? 0 : Float.valueOf(packingString);
                    if (price == 0){
                        ToastUtil.showToast(activity,"价格必须大于0");
                        return;
                    }
                    Product returnProduct = product;
                    if (returnProduct == null)   returnProduct = new Product();
                    returnProduct.setUse(true);
                    returnProduct.setPrice(price);
                    returnProduct.setCount(count);
                    returnProduct.setPackingFee(packing);
                    returnProduct.setNeedCut(needCutCb.isChecked());

                    dialog.dismiss();
                    dialogClickListener.onPositiveButtonClick(dialog,which,returnProduct);
                }else if (which == DialogInterface.BUTTON_NEGATIVE){
                    dialog.dismiss();
                    dialogClickListener.onNegativeButtonClick(dialog,which,null);
                }
                if (tmpWhich == DialogInterface.BUTTON_NEUTRAL){
                    dialogClickListener.onNeutralButtonClick(dialog,tmpWhich,null);

                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定",listener)
                .setNegativeButton("取消",listener)
                .setNeutralButton("再填一个", listener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        UIUtils.toggleInputMethodDelay(activity,200);
        return alertDialog;
    }

    /**
     * 显示添加满减对话框
     * @param activity
     * @param fullCut
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddFullCutDialog(final Activity activity ,
                                                   final FullCut fullCut,
                                                   final OnDialogClickListener dialogClickListener){
        return showAddFullCutOrRedPacketDialog(activity,fullCut,null,dialogClickListener,Constants.TYPE_FULL_CUT);
    }
    /**
     * 显示添加红包对话框
     * @param activity
     * @param redPacket
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddRedPacketDialog(final Activity activity ,
                                                   final RedPacket redPacket,
                                                   final OnDialogClickListener dialogClickListener){
        return showAddFullCutOrRedPacketDialog(activity,null,redPacket,dialogClickListener,Constants.TYPE_RED_PACKETS);
    }
    /**
     * 显示添加满减对话框
     * @param activity
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddFullCutDialog(final Activity activity , final OnDialogClickListener dialogClickListener ){
        return showAddFullCutOrRedPacketDialog(activity,null,null,dialogClickListener,Constants.TYPE_FULL_CUT);
    }
    /**
     * 显示添加红包对话框
     * @param activity
     * @param dialogClickListener
     * @return
     */
    public static AlertDialog showAddRedPacketDialog(final Activity activity,final OnDialogClickListener dialogClickListener ){
        return showAddFullCutOrRedPacketDialog(activity,null,null,dialogClickListener,Constants.TYPE_RED_PACKETS);
    }

    /**
     * 显示添加满减对话框
     * @param activity
     * @param dialogClickListener
     * @param fullCut
     * @param redPacket
     * @param type    满减还是红包
     * @return
     */
    public static AlertDialog showAddFullCutOrRedPacketDialog(final Activity activity , final FullCut fullCut
            ,final RedPacket redPacket, final OnDialogClickListener dialogClickListener
                    ,final int type){
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_item_full_cut, null);
        final EditText fullEt = (EditText) view.findViewById(R.id.full_et);
        final EditText cutEt = (EditText) view.findViewById(R.id.cut_et);
        final EditText typeEt = (EditText) view.findViewById(R.id.type_et);

        String title = fullCut != null || redPacket != null ? "修改":"添加";
        if (type == Constants.TYPE_FULL_CUT) {
            title = title + "满减";
             view.findViewById(R.id.type_et).setVisibility(View.INVISIBLE);
            if (fullCut != null){
                fullEt.setText(fullCut.getFull()+"");
                cutEt.setText(fullCut.getCut()+"");
            }
        } else {
            title = title + "红包";
            view.findViewById(R.id.type_et).setVisibility(View.VISIBLE);
            if (redPacket != null){
                fullEt.setText(redPacket.getFull()+"");
                cutEt.setText(redPacket.getCut()+"");
                typeEt.setText(redPacket.getType()+"");
            }
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tmpWhich = which;
                // 如果是再记一笔, 先执行positive , 再执行其它操作
                if (which == DialogInterface.BUTTON_NEUTRAL){
                    which = DialogInterface.BUTTON_POSITIVE;
                }
                if (which == DialogInterface.BUTTON_POSITIVE){
                    String fullString = fullEt.getText().toString();
                    String cutString = cutEt.getText().toString();
                    String typeString = typeEt.getText().toString();
                    float full = fullString.length() == 0 ? 0 : Float.valueOf(fullString);
                    float cut = cutString.length() == 0 ? 0 : Float.valueOf(cutString);
                    int redPacketType = typeString.length() == 0 ? 1 : Integer.valueOf(typeString);
                    if (full == 0 || cut == 0){
                        ToastUtil.showToast(activity,"满和减必须大于0");
                        return;
                    }
                    dialog.dismiss();
                    if (type == Constants.TYPE_FULL_CUT) {
                        FullCut returnFullCut = fullCut;
                        if (fullCut == null) returnFullCut = new FullCut();
                        returnFullCut.setFull(full);
                        returnFullCut.setCut(cut);
                        dialogClickListener.onPositiveButtonClick(dialog,which,returnFullCut);
                    } else {
                        RedPacket returnRedPacket = redPacket;
                        if (redPacket == null)  returnRedPacket = new RedPacket();
                        returnRedPacket.setFull(full);
                        returnRedPacket.setCut(cut);
                        returnRedPacket.setType(redPacketType);
                        dialogClickListener.onPositiveButtonClick(dialog,which,returnRedPacket);
                    }
                }else if (which == DialogInterface.BUTTON_NEGATIVE){
                    dialog.dismiss();
                    dialogClickListener.onNegativeButtonClick(dialog,which,null);

                }
                if (tmpWhich == DialogInterface.BUTTON_NEUTRAL){
                    dialogClickListener.onNeutralButtonClick(dialog,tmpWhich,null);

                }

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("确定",listener)
                .setNegativeButton("取消",listener)
                .setNeutralButton("再填一个", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        UIUtils.toggleInputMethodDelay(activity,200);
        return dialog;
    }


    /**
     * 对话框是否显示
     * @return
     */
    public boolean isWaitDialogShow() {
        if (mProgressDialog == null || mProgressDialog.getWindow() == null) {
            new IllegalArgumentException(
                    "mProgressDialog is null in function isHttpDialogShow");
            return false;
        }
        return mProgressDialog.isShowing();
    }

    /**
     * 隐藏对话框
     * @param activity
     */
    public void dismissWaitDialog(final Activity activity) {
        if (mProgressDialog == null || mProgressDialog.getWindow() == null) {
            new IllegalArgumentException(
                    "mProgressDialog is null in function dismissDialog");
            return;
        }
        try {
            if (isActivityExist(activity) && isWaitDialogShow()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Activity是否没被销毁
     * @param activity
     * @return
     */
    private boolean isActivityExist(Activity activity){
        return activity != null && !activity.isFinishing();
    }


}
