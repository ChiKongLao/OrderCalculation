package com.chikong.ordercalculation.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ChiKong on 16/04/18.
 */
public class UIUtils {

    /**
     * view用动画显示，调节高度的方式
     * @param view          对应view
     * @param duration      动画时长
     */
    public static void showAnimation(final View view,long duration){
        final int h = view.getHeight();
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                //原始长度+高度差*（从0到1的渐变）即表现为动画效果
                params.height = (int)(h * interpolatedTime) ;
                view.setLayoutParams(params);

            }
        };

        animation.setDuration(duration);
        view.startAnimation(animation);
    }

     /**
     * view用动画隐藏，调节高度的方式
     * @param view          对应view
     * @param duration      动画时长
     */
    public static void hideAnimation(final View view,long duration){
        final int h = DensityUtil.getInstance().dip2px(100);
//        final int h = view.getHeight();
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                //原始长度+高度差*（从0到1的渐变）即表现为动画效果
                params.height = (int)(h * (1 - interpolatedTime)) ;
                view.setLayoutParams(params);

            }
        };

        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * 切换输入法显示或隐藏
     * @param act
     */
    public static void toggleInputMethod(Activity act){
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * 切换输入法显示或隐藏
     * @param act
     * @param delay ms
     */
    public static void toggleInputMethodDelay(final Activity act,long delay){
        // 自动弹出输入法
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               toggleInputMethod(act);
                           }

                       },
                delay);
    }


}
