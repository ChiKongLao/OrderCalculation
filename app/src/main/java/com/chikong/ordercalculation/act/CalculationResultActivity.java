package com.chikong.ordercalculation.act;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.R;
import com.chikong.ordercalculation.act.base.BaseSwipeAppCompatActivity;
import com.chikong.ordercalculation.listener.OnRecyclerViewItemClickListener;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.Plan;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;
import com.chikong.ordercalculation.utils.DialogHelper;
import com.chikong.ordercalculation.utils.LogHelper;
import com.chikong.ordercalculation.utils.MathUtil;
import com.chikong.ordercalculation.utils.MobclickAgentUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CalculationResultActivity extends BaseSwipeAppCompatActivity implements View.OnClickListener
        , OnRecyclerViewItemClickListener {

    private final int LOAD_COUNT = 5;
    @Bind(R.id.content_tv)
    TextView contentTv;
    @Bind(R.id.numberPicker)
    NumberPicker numberPicker;
    @Bind(R.id.title_layout)
    LinearLayout titleLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;

    private MyTask mTask;

    /**
     * 价格列表
     */
    private List<Plan> mOriginPlanList = new ArrayList<>();
    /**
     * 筛选价格列表
     */
    private List<Plan> mNewPlanList = new ArrayList<>();
    /**
     * 价格列表
     */
    private ArrayList<Product> mProductList;
    /**
     * 满减列表
     */
    private ArrayList<FullCut> mFullCutList;
    /**
     * 红包列表
     */
    private ArrayList<RedPacket> mRedPacketsList;
    /**
     * 运费
     */
    private static float mFreight;
    /**
     * 起送费
     */
    private static float mMinTotalPrice;
    /**
     * 其它优惠
     */
    private static float mOtherCut;
    /**
     * 最大拆单数;  0为无限制
     */
    private static int mMaxOrderCount;
    /**
     * 每人红包最大使用数
     */
    private static int mMaxRedPacketUseCount;
    /**
     * 包装费添加到满减计算
     */
    private static boolean isAddPackingFee2Cal;
    /**
     * 配送费添加到满减计算
     */
    private static boolean isAddFreight2Cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_calculation_result);

        ButterKnife.bind(this);

        mProductList = getIntent().getParcelableArrayListExtra("productList");
        mFullCutList = getIntent().getParcelableArrayListExtra("fullCutList");
        mRedPacketsList = getIntent().getParcelableArrayListExtra("redPacketsList");
        mMinTotalPrice = getIntent().getFloatExtra("minTotalPrice", 0);
        mFreight = getIntent().getFloatExtra("freight", 0);
        mOtherCut = getIntent().getFloatExtra("otherCut", 0);
        mMaxOrderCount = getIntent().getIntExtra("maxOrderCount", 0);
        mMaxRedPacketUseCount = getIntent().getIntExtra("maxRedPacketCount", 2);
        isAddPackingFee2Cal = getIntent().getBooleanExtra("isAddPackingFee2Cal", false);
        isAddFreight2Cal = getIntent().getBooleanExtra("isAddFreight2Cal", false);

        initView();
        initControl();
        loadData();

    }


    private void initView() {

        Toolbar toolbar = ButterKnife.findById(this,R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.trace("***OnClick***");
                finish();
            }
        });

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisiblePosition = manager.findLastVisibleItemPosition();
                    // 滑动到底部
                    if (lastVisiblePosition >= manager.getItemCount() - 1) {
                        mNewPlanList.addAll(getPlanList(numberPicker.getValue(), mNewPlanList.size()));
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewPlanList.clear();
                mNewPlanList.addAll(getPlanList(newVal, 0));
                adapter.notifyDataSetChanged();

                String string = "共有 " + getPlanListWithOutLoadCount(newVal).size() + " 种方案，从最佳开始排序";
                contentTv.setText(string);
            }
        });


    }

    private void initControl() {
        adapter = new RecyclerViewAdapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void loadData() {
        mTask = new MyTask();
        mTask.execute();

    }

    // <Params, Progress, Result>
    private class MyTask extends AsyncTask<Void, Integer, List<Plan>> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            DialogHelper.getInstance().showWaitDialog(CalculationResultActivity.this, "方案规划中...");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected List<Plan> doInBackground(Void... params) {
            Collections.sort(mFullCutList);
            Collections.sort(mRedPacketsList);
            List<Plan> list = group(mProductList);
            calBestPlan(list);
            Collections.sort(list);
            return list;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(List<Plan> list) {
            mOriginPlanList.addAll(list);
            mNewPlanList.addAll(getPlanList(0, 0));
            adapter.notifyDataSetChanged();

            String string = "共有 " + list.size() + " 种方案，从最佳开始排序";
            LogHelper.trace(string);
            contentTv.setText(string);
            for (int i = 0; i < list.size(); i++) {
                LogHelper.trace("方案" + (i + 1) + ": " + list.get(i).printSubPlan());
            }

            if (list.size() > 0) {
                // 设置数字选择器
                List<Integer> intList = new ArrayList<>();
                for (Plan forPlan : list) {
                    intList.add(forPlan.getSubPlanList().size());
                }
                Collections.sort(intList);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(intList.get(intList.size() - 1));
                numberPicker.setValue(0);
            }


            DialogHelper.getInstance().dismissWaitDialog(CalculationResultActivity.this);

        }

    }

    /**
     * 获取方案列表
     *
     * @param childCount 过滤得到的子方案个数; 0为不过滤
     * @param startIndex 开始下标
     * @return
     */
    private List<Plan> getPlanList(int childCount, int startIndex) {
        List<Plan> list = new ArrayList<>();
        List<Plan> newList = new ArrayList<>();
        for (int i = 0; i < mOriginPlanList.size(); i++) {
            Plan forPlan = mOriginPlanList.get(i);
            if (forPlan.getSubPlanList().size() == childCount || childCount == 0) {
                newList.add(forPlan);
            }
        }
        // 一次加载限制个数
        for (int i = startIndex; i < newList.size() && list.size() < LOAD_COUNT; i++) {
            Plan forPlan = newList.get(i);
            list.add(forPlan);
        }

        return list;
    }

    /**
     * 获取方案列表
     *
     * @param childCount 过滤得到的子方案个数; 0为不过滤
     * @return
     */
    private List<Plan> getPlanListWithOutLoadCount(int childCount) {
        List<Plan> list = new ArrayList<>();
        // 一次加载限制个数
        for (int i = 0; i < mOriginPlanList.size(); i++) {
            Plan forPlan = mOriginPlanList.get(i);
            if (forPlan.getSubPlanList().size() == childCount || childCount == 0) {
                list.add(forPlan);
            }
        }

        return list;
    }

    /**
     * 添加方案项
     *
     * @param layout 父布局
     * @param plan   方案
     */
    private void addPlanLayout(LinearLayout layout, Plan plan) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_plan_detail, null);
        TextView numberTv = (TextView) view.findViewById(R.id.num_tv);
        TextView originalPriceTv = (TextView) view.findViewById(R.id.originalPrice_tv);
        TextView feeTv = (TextView) view.findViewById(R.id.fee_tv);
        TextView cutTv = (TextView) view.findViewById(R.id.cut_tv);
        TextView priceTv = (TextView) view.findViewById(R.id.price_tv);

        numberTv.setText(layout.getChildCount() + 1 + "号袋");
        originalPriceTv.setText(plan.getOriginalPrice() + " (" + plan.getProductPriceList() + ")");
        feeTv.setText((plan.getFreight() + plan.getPackingFee()) + " (" + plan.getFreight() + "+" + plan.getPackingFee()
                + "), " + "配送费= " + plan.getFreight() + " 包装费= " + plan.getPackingFee());
        cutTv.setText(plan.getTotalCut() + " (" + plan.getFullCut().getCut() + "+"
                + plan.getRedPackets().getCut() + "+" + plan.getOtherCut() + ") \n" + plan.printFullCut()
                + "\n" + plan.printOtherCut());
        priceTv.setText(plan.getPrice() + " (" + plan.getOriginalPrice() + "-" + plan.getTotalCut()
                + "+" + (plan.getFreight() + plan.getPackingFee()) + ")");

        layout.addView(view);

    }

    /**
     * 计算最佳满减方案
     *
     * @param planList 所有分组方案
     * @return
     */
    private void calBestPlan(List<Plan> planList) {
        List<FullCut> tmpFullCutList = new ArrayList<>();
        tmpFullCutList.addAll(mFullCutList);

        // 计算最佳满减方案
        for (int i = 0; i < planList.size(); i++) {
            Plan plan = planList.get(i);
            for (int j = 0; j < plan.getSubPlanList().size(); j++) {
                Plan subPlan = plan.getSubPlanList().get(j);
                for (int k = 0; k < tmpFullCutList.size(); k++) {
                    FullCut fullCut = tmpFullCutList.get(k);
                    float price = subPlan.getOriginalPrice();
                    // 是否将包装费,配送费算入满减计算  ,要分别处理
                    if (isAddPackingFee2Cal) price -= subPlan.getPackingFee();
                    if (isAddFreight2Cal) price -= subPlan.getFreight();
                    if (price >= fullCut.getFull()) {
                        subPlan.setFullCut(fullCut);
                        break;
                    }
                }
            }
        }
        // 不使用红包
        if (!getIntent().getBooleanExtra("isUseRedPackets", false)) return;

        // 计算最佳红包方案
        for (int i = 0; i < planList.size(); i++) {
            Plan plan = planList.get(i);
            List<RedPacket> tmpRedPacketsList = resetRedPacketListUse();
            for (int j = 0; j < plan.getSubPlanList().size(); j++) {
                Plan subPlan = plan.getSubPlanList().get(j);
                for (int k = 0; k < tmpRedPacketsList.size(); k++) {
                    RedPacket redPacket = tmpRedPacketsList.get(k);
                    float price = subPlan.getOriginalPrice() - subPlan.getFullCut().getCut()
                            + subPlan.getPackingFee() + subPlan.getFreight();
                    // 价格满足红包条件 && 红包没使用
                    if (price >= redPacket.getFull() && !redPacket.isHasUse()) {
                        redPacket.setHasUse(true);
                        subPlan.setRedPackets(redPacket);
                        tmpRedPacketsList.remove(k);
                        break;
                    }
                }
            }
            int df = 0;
        }

        // 计算方案时添加限制, 每人每天最多使用两个红包.
        // 没想到更好的计算方案, 暂时按照原有的最好的计算方案情况下:
        // 去除第三个最小的红包 , 替换为第二人的红包.  有更好的方法后再修复
        for (Plan plan : planList) {
            List<RedPacket> tmpRedPacketsList = setRedPacketListUse(plan);
            // 红包类型个数
            int[] type = new int[5];
            for (Plan subPlan : plan.getSubPlanList()) {
                if (subPlan.getRedPackets().getType() != 0) {
                    type[subPlan.getRedPackets().getType() - 1]++;
                }
            }
            for (int i = 0; i < type.length; i++) {
                // 超过每人每天使用红包数
                while (type[i] > mMaxRedPacketUseCount) {
                    // 获取最小的方案的红包
                    Plan tmpPlan = null;
                    for (Plan subPlan : plan.getSubPlanList()) {
                        if (subPlan.getRedPackets().getType() == i + 1) {
                            if (tmpPlan == null || subPlan.getRedPackets().getCut() < tmpPlan.getRedPackets().getCut()) {
                                tmpPlan = subPlan;
                            }
                        }
                    }
                    // 如果已经是最后一个人,则取消使用最小的红包
                    if (tmpPlan != null) {
                        tmpPlan.setRedPackets(new RedPacket());
                        for (RedPacket forRedPacket : tmpRedPacketsList) {
//                            float price = tmpPlan.getOriginalPrice() - tmpPlan.getFullCut().getCut();
                            float price = tmpPlan.getPrice();
                            // 价格满足红包条件 && 红包没使用 && 红包不属于同一个人
                            if (price >= forRedPacket.getFull() && !forRedPacket.isHasUse() && forRedPacket.getType() != i + 1) {
                                tmpPlan.setRedPackets(forRedPacket);
                                break;
                            }
                        }
                    }

                    type[i]--;
                }
            }

        }

    }

    /**
     * 商品分组方案
     *
     * @param productList 商品列
     * @return 返回所有可能的分组
     */
    private List<Plan> group(List<Product> productList) {
        ;
        List<Plan> planList = new ArrayList<>();
        // 获取所有组合
        // 添加所有价格到productNewList
        List<Product> productNewList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            // 同一商品项可能有多个
            for (int j = 0; j < product.getCount(); j++) {
                try {
                    Product tmpProduct = (Product) product.clone();
                    tmpProduct.setCount(1);
                    productNewList.add(tmpProduct);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        List<List<List<Product>>> groupList = MathUtil.getCombinations(mMinTotalPrice, mMaxOrderCount, productNewList);
        for (int i = 0; i < groupList.size(); i++) {
            String string = "index = " + (i + 1) + ": ";
            for (int j = 0; j < groupList.get(i).size(); j++) {
                string = string + "[";
                for (int k = 0; k < groupList.get(i).get(j).size(); k++) {
                    string = string + groupList.get(i).get(j).get(k).getPrice();
                    if (k != groupList.get(i).get(j).size() - 1) string = string + ",";
                }
                string = string + "]";
            }
            LogHelper.trace(string);
        }

        // 对所有子集分组合
        for (int i = 0; i < groupList.size(); i++) {
            // 超过最大拆单数的跳过
            if (mMaxOrderCount != 0 && groupList.get(i).size() > mMaxOrderCount) continue;
            Plan plan = new Plan();
            for (int j = 0; j < groupList.get(i).size(); j++) {
                List<Product> subList = groupList.get(i).get(j);
                Plan subPlan = new Plan(subList, mFreight);
                subPlan.setOtherCut(mOtherCut);
                plan.addSubPlan(subPlan);
                plan.setFreight(plan.getFreight() + mFreight);
            }
            planList.add(plan);
        }
        return planList;

    }

    /**
     * 设置已使用的红包的状态
     *
     * @param plan 使用红包的方案
     * @return
     */
    private ArrayList<RedPacket> setRedPacketListUse(Plan plan) {
        resetRedPacketListUse();
        ArrayList<RedPacket> list = new ArrayList<>(mRedPacketsList);

        for (Plan subPlan : plan.getSubPlanList()) {
            int index = list.indexOf(subPlan.getRedPackets());
            if (index != -1) list.get(index).setHasUse(true);
        }
        return list;
    }

    /**
     * 重置红包列表的状态
     */
    private List<RedPacket> resetRedPacketListUse() {
        List<RedPacket> list = new ArrayList<>(mRedPacketsList);
        for (RedPacket forRedPacket : list) {
            forRedPacket.setHasUse(false);
        }
        return list;
    }


    /**
     * 获取红包类型个数
     *
     * @return 红包类型个数
     */
    private int getRedPacketTypeCount() {
        List<Integer> list = new ArrayList<>();
        for (RedPacket redPacket : mRedPacketsList) {
            if (list.indexOf(redPacket.getType()) == -1) {
                list.add(redPacket.getType());
            }
        }
        return list.size();
    }

    /**
     * 启动Activity
     */
    private void startActivity(int position) {

        Intent intent = new Intent(this, PlanDetailActivity.class);
        intent.putExtra("plan", mNewPlanList.get(position));
        intent.putParcelableArrayListExtra("productList", mProductList);
        intent.putParcelableArrayListExtra("fullCutList", mFullCutList);
        intent.putParcelableArrayListExtra("redPacketsList", setRedPacketListUse(mNewPlanList.get(position)));
        intent.putExtra("isAddPackingFee2Cal", isAddPackingFee2Cal);
        intent.putExtra("isAddFreight2Cal", isAddFreight2Cal);
        intent.putExtra("freight", mFreight);
        intent.putExtra("otherCut", mOtherCut);
        intent.putExtra("cutType", getIntent().getIntExtra("cutType", Constants.TYPE_CUT_PERCENT));
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        LogHelper.trace("***OnClick***");
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position < 0 || position > mNewPlanList.size() - 1) position = 0;
        startActivity(position);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentUtils.onPageStart("计算页面");
        MobclickAgentUtils.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgentUtils.onPageEnd("计算页面");
        MobclickAgentUtils.onPause(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        private Context context;
        private OnRecyclerViewItemClickListener onItemClickListener;

        public RecyclerViewAdapter(Context context) {
            this.context = context;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_cal_result, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Plan plan = mNewPlanList.get(position);
            holder.priceTv.setText(plan.printSummary());
            holder.detailLayout.removeAllViews();
            for (Plan subPlan : plan.getSubPlanList()) {
                addPlanLayout(holder.detailLayout, subPlan);
            }
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(v, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mNewPlanList.size();
        }

        public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.detail_layout)
            LinearLayout detailLayout;
            @Bind(R.id.price_tv)
            TextView priceTv;
            @Bind(R.id.item_layout)
            CardView itemLayout;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }


}
