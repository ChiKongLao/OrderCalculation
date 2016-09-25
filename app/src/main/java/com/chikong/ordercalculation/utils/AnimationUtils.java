package com.chikong.ordercalculation.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by ChiKong on 16/05/13.
 */
public class AnimationUtils {

    /**
     *  渐扩大高度去显示
     * @param layout    对应View
     * @param addHeight  调整后的高度dp
     */
    public static void addHeight(final LinearLayout layout,float addHeight){
        final float addHeight2 = DensityUtil.getInstance().px2dip(addHeight);
        final int height = layout.getMeasuredHeight();
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
                //原始长度+高度差*（从0到1的渐变）即表现为动画效果
                params.height = height + (int)(addHeight2 * interpolatedTime) ;
                LogHelper.test("params.height = "+params.height);
                layout.setLayoutParams(params);

            }
        };
        animation.setDuration(1000);
        layout.setAnimation(animation);
    }
    /**
     *  渐缩小高度去显示
     * @param layout    对应View
     * @param cutHeight  调整后的高度
     */
    public static void cutHeight(final LinearLayout layout,float cutHeight){
        final float cutHeight2 = DensityUtil.getInstance().px2dip(cutHeight);
        LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
        final int height = params.height;
        Animation animation = new Animation() {
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
                params.height = height - (int)(cutHeight2 * (1 - interpolatedTime)) ;
                layout.setLayoutParams(params);

            }
        };
        animation.setDuration(1000);
        layout.setAnimation(animation);
    }

}
