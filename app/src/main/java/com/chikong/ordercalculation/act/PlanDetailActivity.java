package com.chikong.ordercalculation.act;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.R;
import com.chikong.ordercalculation.act.base.BaseSwipeAppCompatActivity;
import com.chikong.ordercalculation.listener.OnRecyclerViewItemClickListener;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.GetMore;
import com.chikong.ordercalculation.model.Plan;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;
import com.chikong.ordercalculation.utils.LogHelper;
import com.chikong.ordercalculation.utils.MathUtil;
import com.chikong.ordercalculation.utils.MobclickAgentUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanDetailActivity extends BaseSwipeAppCompatActivity implements View.OnClickListener{

    private LinearLayout detailLayout;
    private LinearLayout getMoreLayout;

    /**价格列表*/
    private ArrayList<Product> mProductList;
    /**满减列表*/
    private ArrayList<FullCut> mFullCutList;
    /**红包列表*/
    private ArrayList<RedPacket> mRedPacketsList;
    /**运费*/
    private static float mFreight = 0;
    /**其它优惠*/
    private static float mOtherCut = 0;
    /** 包装费添加到满减计算 */
    private static boolean isAddPackingFee2Cal;
    /** 配送费添加到满减计算 */
    private static boolean isAddFreight2Cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_plan_detail);

//        ButterKnife.bind(this);

        Plan plan = getIntent().getParcelableExtra("plan");
        mProductList = getIntent().getParcelableArrayListExtra("productList");
        mFullCutList = getIntent().getParcelableArrayListExtra("fullCutList");
        mRedPacketsList = getIntent().getParcelableArrayListExtra("redPacketsList");
        mFreight = getIntent().getFloatExtra("freight",0);
        mOtherCut = getIntent().getFloatExtra("otherCut",0);
        isAddPackingFee2Cal =getIntent().getBooleanExtra("isAddPackingFee2Cal",false);
        isAddFreight2Cal =getIntent().getBooleanExtra("isAddFreight2Cal",false);

        initView(plan);
        initControl();
        loadData(plan);

    }


    private void initView(Plan plan) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.trace("***OnClick***");
                finish();
            }
        });

        detailLayout = (LinearLayout) findViewById(R.id.detail_layout);
        getMoreLayout = (LinearLayout) findViewById(R.id.getMore_layout);
        ((TextView)findViewById(R.id.price_tv)).setText(plan.printSummary());
        for (Plan subPlan : plan.getSubPlanList()) {
            addPlanLayout(detailLayout,subPlan);
        }

    }

    private void initControl() {

    }

    private void loadData(Plan plan) {
        loadDataForEachPrice(plan);
        loadDataFoGetMore(plan);


    }
    /** 打印贪小便宜 */
    private void loadDataFoGetMore(Plan plan) {

        LogHelper.trace("\n现有满减和红包的最大优惠情况: \n");
        List<Plan> mostCutPlanList = calMostCut();
        String string = "";
        for (int i = 0; i < mostCutPlanList.size(); i++) {
            Plan tmpPlan = mostCutPlanList.get(i);
            string = string + "index=" + (i + 1) + " 满 = " + tmpPlan.getOriginalPrice()
                    + " 减 = " + tmpPlan.getTotalCut()
                    +" "+tmpPlan.printFullCut() + "\n";

        }
        LogHelper.trace(string);
        LogHelper.trace("贪小便宜: \n");
        calGetMore(plan, mostCutPlanList);
        for (int i = 0; i < plan.getSubPlanList().size(); i++) {
            Plan subPlan = plan.getSubPlanList().get(i);
            if (subPlan.getGetMoreList().size() != 0) {
                addPlanLayout(getMoreLayout, subPlan, i);
                addGetMoreLayout(subPlan);
            }
        }
    }

    /** 打印每人减多少 */
    private void loadDataForEachPrice(Plan plan){
        calRespectivelyCut(plan);
        String resultString = "各商品优惠情况: \n";
        for (int i = 0; i < mProductList.size(); i++) {
            Product product = mProductList.get(i);
            resultString = resultString + "原价 = " + product.getPrice() + " 优惠 = " + product.getCut()
                    + " 最终价格 = " + MathUtil.keepDecimal((product.getPrice() - product.getCut())) ;
            if (i != mProductList.size() - 1)  resultString = resultString +"\n";
        }
        LogHelper.trace(resultString);
        ((TextView) findViewById(R.id.content_tv)).setText(resultString);

    }

    /**
     * 添加方案项
     * @param plan   方案
     */
    private void addPlanLayout(LinearLayout layout , Plan plan) {
        addPlanLayout(layout, plan,-1);
    }
    /**
     * 添加方案项
     * @param plan   方案
     */
    private void addPlanLayout(LinearLayout layout , Plan plan,int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_plan_detail, null);
        TextView numberTv = (TextView) view.findViewById(R.id.num_tv);
        TextView originalPriceTv = (TextView) view.findViewById(R.id.originalPrice_tv);
        TextView feeTv = (TextView) view.findViewById(R.id.fee_tv);
        TextView cutTv = (TextView) view.findViewById(R.id.cut_tv);
        TextView priceTv = (TextView) view.findViewById(R.id.price_tv);

        numberTv.setText((index == -1 ? layout.getChildCount() + 1 : index+1) + "号袋");
        originalPriceTv.setText(plan.getOriginalPrice() + " (" + plan.getProductPriceList() + ")");
        feeTv.setText((plan.getFreight()+ plan.getPackingFee())+" (" + plan.getFreight()+ "+" + plan.getPackingFee()
                + "), "+"配送费= " + plan.getFreight() + " 包装费= " + plan.getPackingFee());
        cutTv.setText(plan.getTotalCut() + " (" + plan.getFullCut().getCut() + "+"
                + plan.getRedPackets().getCut() + "+" + plan.getOtherCut() + ") \n" + plan.printFullCut()
                +"\n"+plan.printOtherCut());
        priceTv.setText(plan.getPrice() + " (" + plan.getOriginalPrice() + "-" + plan.getTotalCut()
                + "+" +(plan.getFreight()+ plan.getPackingFee()) + ")");

        layout.addView(view);

    }
    /**
     * 添加贪小便宜项
     * @param plan   方案
     */
    private void addGetMoreLayout(Plan plan) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_getmore, null);
        TextView originalPriceTv = (TextView) view.findViewById(R.id.content_tv);

        String string = "";
        for (GetMore getMore : plan.getGetMoreList()) {
            string = string + "加" + getMore.getAdd()
                    + "多减" + getMore.getCut()
                    + ", " + getMore.getTargetPlan().printFullCut() + "\n";
        }
        LogHelper.trace(string);
        originalPriceTv.setText(string);

        getMoreLayout.addView(view);

    }


    /**
     * 计算各商品项减多少
     *
     * @param plan 方案
     */
    private void calRespectivelyCut(Plan plan) {
        int cutType = getIntent().getIntExtra("cutType",Constants.TYPE_CUT_PERCENT) ;
        // 求总价
        float sumPrice = 0;
        // 总优惠 = 总满减 - 运费 - 包装费
        float sumCut = plan.getTotalCut() - plan.getTotalFreight() - plan.getPackingFee();
        float count = 0;
        for (Product product : mProductList ) {
            sumPrice += (product.getPrice() * product.getCount());
            if (product.isNeedCut()) count += product.getCount();
        }

        for (Product product : mProductList ) {
            if (!product.isNeedCut()) continue;
            if (cutType == Constants.TYPE_CUT_AVERAGE) {
                product.setCut(MathUtil.keepDecimal(sumCut / count));

            } else if (cutType == Constants.TYPE_CUT_PERCENT) {
                product.setCut(MathUtil.keepDecimal(sumCut * product.getPrice() / sumPrice));

            }

        }
    }

    /**
     * 计算加多少钱能达到下一个更大的优惠
     * @param bestPlanList
     * @param mostCutList
     */
    private void calGetMore(List<Plan> bestPlanList, List<Plan> mostCutList) {
        for (Plan plan : bestPlanList) {
            calGetMore(plan,mostCutList);
        }
    }

    /**
     * 计算加多少钱能达到下一个更大的优惠
     * @param plan
     * @param mostCutList
     */
    private static void calGetMore(Plan plan, List<Plan> mostCutList) {
        for (Plan subPlan : plan.getSubPlanList()) {
            GetMore getMore = null;
            for (Plan fullCutPlan : mostCutList) {
                // 该方案的总优惠小于最多优惠的方案 并且 在线满减不小于最多优惠的方案的满减
                if (subPlan.getTotalCut() < fullCutPlan.getTotalCut()
                        && subPlan.getFullCut().getCut() <= fullCutPlan.getFullCut().getCut()) {
                    // 红包不是当前方案所使用的且已经被其它方案使用的, 则跳过
                    // 使用fullCutPlan去判断是因为subPlan可能没用红包
                    if (!fullCutPlan.getRedPackets().isHasUse() || subPlan.getRedPackets().equals(fullCutPlan.getRedPackets())) {
                        getMore = new GetMore();
                        getMore.setParentPlan(subPlan);
                        getMore.setTargetPlan(fullCutPlan);
                        getMore.setCut(MathUtil.keepDecimal(fullCutPlan.getTotalCut() + mOtherCut - subPlan.getTotalCut()));
                        subPlan.addGetMore(getMore);
                    }
                } else {
                    break;
                }
            }
        }
    }


    /**
     * 计算现有满减和剩下红包的最大优惠方案
     * @return
     */
    private List<Plan> calMostCut() {
        List<Plan> list = new ArrayList<>();
        // 计算优先满减
        for (int i = 0; i < mFullCutList.size(); i++) {
            FullCut fullCut = mFullCutList.get(i);
            float price = fullCut.getFull() - fullCut.getCut();
            //TODO  未计算包装费，要分别处理
//            if (isAddPackingFee2Cal) price += packingFee;
            if (isAddFreight2Cal) price += mFreight;

            for (int j = 0; j < mRedPacketsList.size(); j++) {
                RedPacket redPacket = mRedPacketsList.get(j);
                if (price >= redPacket.getFull()) {
                    Plan plan = new Plan(new Product(fullCut.getFull()));
                    plan.setFullCut(fullCut);
                    plan.setRedPackets(redPacket);
                    plan.setOtherCut(mOtherCut);
                    list.add(plan);
                }
            }
            // 添加不用红包的方案
            Plan plan = new Plan(new Product(fullCut.getFull()));
            plan.setFullCut(fullCut);
            plan.setOtherCut(mOtherCut);
            list.add(plan);
        }

        List<FullCut> tmpFullCutList = new ArrayList<>();
        tmpFullCutList.addAll(mFullCutList);
        Collections.reverse(tmpFullCutList);
        // 计算优先红包
        for (int i = 0; i < mRedPacketsList.size(); i++) {
            RedPacket redPacket = mRedPacketsList.get(i);
            for (int j = 0; j < tmpFullCutList.size(); j++) {
                FullCut fullCut = tmpFullCutList.get(j);
                // 红包价格 + 满减优惠 - 配送费
                float price = redPacket.getFull() + fullCut.getCut();
                // 是否将包装费,配送费算入满减计算  ,要分别处理
//            if (isAddPackingFee2Cal) price += subPlan.getPackingFee();
                if (isAddFreight2Cal) price -= mFreight;
                // 如果当前红包已经扫描到最大满减,但满减依然不满足条件,则将当前红包和最大满减相加作为最佳满减
                if (fullCut.getFull() >= price
                        || (j == tmpFullCutList.size() - 1 && fullCut.getFull() < price)) {
                    Plan plan = new Plan();
                    plan.setOriginalPrice(fullCut.getFull() >= price ? fullCut.getFull() : price);
                    plan.setFullCut(fullCut);
                    plan.setRedPackets(redPacket);
                    plan.setOtherCut(mOtherCut);
                    // 存在相同满价格或者优惠 和 存在更好的方案 时
                    if (!isExistEqualsPriceAndCut(list,plan)) {
                        list.add(plan);
                    }
                }

            }
        }


        // 对方案进行最大优惠排序
        Plan tmpPlan;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getTotalCut() < list.get(j).getTotalCut()) {
                    tmpPlan = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, tmpPlan);
                }
            }
        }

        return list;
    }

    /**
     * 是否存在相同价格和优惠的方案
     *
     * @param list 方案列表
     * @param plan 方案
     * @return
     */
    private boolean isExistEqualsPriceAndCut(List<Plan> list, Plan plan) {
        for (int k = 0; k < list.size(); k++) {
            Plan tmpPlan = list.get(k);
            if (String.valueOf(tmpPlan.getOriginalPrice()).equals(String.valueOf(plan.getOriginalPrice()))
                    && String.valueOf(tmpPlan.getTotalCut()).equals(String.valueOf(plan.getTotalCut()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在相同价格的方案
     *
     * @param list 方案列表
     * @param plan 方案
     * @return 对应方案的下标, 不存在返回-1
     */
    private int isExistEqualsPrice(List<Plan> list, Plan plan) {
        for (int i = 0; i < list.size(); i++) {
            Plan tmpPlan = list.get(i);
            if (String.valueOf(tmpPlan.getOriginalPrice()).equals(String.valueOf(plan.getOriginalPrice()))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 是否存在相同优惠的方案
     *
     * @param list 方案列表
     * @param plan 方案
     * @return 对应方案的下标, 不存在返回-1
     */
    private int isExistEqualsCut(List<Plan> list, Plan plan) {
        for (int i = 0; i < list.size(); i++) {
            Plan tmpPlan = list.get(i);
            if (String.valueOf(tmpPlan.getTotalCut()).equals(String.valueOf(plan.getTotalCut()))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        LogHelper.trace("***OnClick***");
//        if (v.getId() == R.id.button) {
//        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentUtils.onPageStart("方案详情页面");
        MobclickAgentUtils.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgentUtils.onPageEnd("方案详情页面");
        MobclickAgentUtils.onPause(this);
    }

}
