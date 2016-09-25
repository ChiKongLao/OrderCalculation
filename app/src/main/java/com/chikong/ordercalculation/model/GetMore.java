package com.chikong.ordercalculation.model;

import com.chikong.ordercalculation.utils.MathUtil;

/**
 * Created by ChiKong on 16/04/29.
 * 贪小便宜
 */
public class GetMore implements Comparable<GetMore> {
//    原价 = 44.0(13.0+15.0+16.0) 配送费 = 0.0 包装费 = 0.0 满减 = 9.0 红包 = 3.5 其它优惠 = 0.0 共减 = 12.5(12.5-0.0) 最终价格 = 31.5
//    再添加 20.0 元即可享受减多 6.3 元 ，满减方案为： 满 = 64.0 减 = 18.8(满减 = 12.0 + 红包 = 6.8)

    /**
     * 添加多少
     */
    private float add;
    /**
     * 再减多少
     */
    private float cut;

    /**
     * 对应方案
     */
    private Plan parentPlan;
    /**
     * 对应更好方案
     */
    private Plan targetPlan;
    /**
     * 对应优惠
     */
    private FullCut fullCut;

    public float getAdd() {
        return MathUtil.keepDecimal(targetPlan.getOriginalPrice() - parentPlan.getOriginalPrice());
    }

    public float getCut() {
        return cut;
    }

    public void setCut(float cut) {
        this.cut = cut;
    }

    public FullCut getFullCut() {
        return fullCut;
    }

    public void setFullCut(FullCut fullCut) {
        this.fullCut = fullCut;
    }


    public Plan getParentPlan() {
        return parentPlan;
    }

    public void setParentPlan(Plan parentPlan) {
        this.parentPlan = parentPlan;
    }

    public Plan getTargetPlan() {
        return targetPlan;
    }

    public void setTargetPlan(Plan targetPlan) {
        this.targetPlan = targetPlan;
    }

    @Override
    public int compareTo(GetMore another) {
        return (Float.valueOf(this.getAdd())).compareTo(another.getAdd());

    }

    @Override
    public boolean equals(Object another) {

        if (this.getAdd() == ((GetMore)another).getAdd()
                && this.getCut() == ((GetMore)another).getCut()
                && this.getParentPlan().getOriginalPrice() ==((GetMore)another).getParentPlan().getOriginalPrice() ){
            return  true;
        }
        return false;
    }
}
