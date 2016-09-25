package com.chikong.ordercalculation.act;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.R;
import com.chikong.ordercalculation.act.adapter.FragmentViewPagerAdapter;
import com.chikong.ordercalculation.fragment.FullCutListFragment;
import com.chikong.ordercalculation.fragment.ProductListFragment;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;
import com.chikong.ordercalculation.utils.DataBaseHelper;
import com.chikong.ordercalculation.utils.DialogHelper;
import com.chikong.ordercalculation.utils.MobclickAgentUtils;
import com.chikong.ordercalculation.utils.SettingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalculationActivity extends AppCompatActivity implements View.OnClickListener{

    //activity标识
    public static final String[] mTag = {"商品", "满减", "红包"};
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private ProductListFragment productListFragment;
    private FullCutListFragment fullCutListFragment;
    private FullCutListFragment redPacketListFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_calculation);
        ButterKnife.bind(this);


        initView();

        initControl();
        loadData();
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        productListFragment = ProductListFragment.newInstance();
        fullCutListFragment = FullCutListFragment.newInstance(Constants.FRAGMENT_TYPE_FULLCUT);
        redPacketListFragment = FullCutListFragment.newInstance(Constants.FRAGMENT_TYPE_REDPACKET);

        FragmentViewPagerAdapter viewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(productListFragment, mTag[0]);//添加Fragment
        viewPagerAdapter.addFragment(fullCutListFragment, mTag[1]);//添加Fragment
        viewPagerAdapter.addFragment(redPacketListFragment, mTag[2]);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.addTab(tabLayout.newTab().setText(mTag[0]));//给TabLayout添加Tab
        tabLayout.addTab(tabLayout.newTab().setText(mTag[1]));
        tabLayout.addTab(tabLayout.newTab().setText(mTag[2]));
//		给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initControl() {


    }




    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MyTask().execute();
            }
        }, 100);


    }

    private void testData() {

        List<Product> list1 = new ArrayList<>();
        List<FullCut> list2 = new ArrayList<>();
        List<RedPacket> list3 = new ArrayList<>();

//        addProduct(new Product(2, 1,false));
//        addProduct(new Product(11, 1,1f));
        list1.add(new Product(12, 1, 1f));
        list1.add(new Product(13, 3, 1f));
//        list1.add(new Product(14, 3, 1f));
//        list1.add(new Product(15, 1,1f));
        list1.add(new Product(16, 1, 1f));
        list1.add(new Product(18, 1, 1f));

        list2.add(new FullCut(20, 7));
//        list2.add(new FullCut(30, 10));
        list2.add(new FullCut(40, 9));
        list2.add(new FullCut(60, 15));

        list3.add(new RedPacket(1, 0.1f,1));
        list3.add(new RedPacket(11, 1.1f,1));
        list3.add(new RedPacket(11, 1.1f,1));
        list3.add(new RedPacket(14, 1.4f,2));
        list3.add(new RedPacket(15, 1.2f,1));
        list3.add(new RedPacket(16, 1.1f,1));
        list3.add(new RedPacket(16, 1.6f,1));
        list3.add(new RedPacket(17, 1.2f,1));
        list3.add(new RedPacket(17, 1.7f,1));
        list3.add(new RedPacket(19, 1.3f,1));
        list3.add(new RedPacket(19, 1.5f,1));
        list3.add(new RedPacket(19, 1.5f,1));
        list3.add(new RedPacket(19, 2.2f,1));
        list3.add(new RedPacket(20, 2.0f,1));
        list3.add(new RedPacket(21, 1.7f,1));
        list3.add(new RedPacket(21, 2.1f,1));
        list3.add(new RedPacket(22, 2.2f,1));
        list3.add(new RedPacket(23, 1.6f,1));
        list3.add(new RedPacket(23, 1.8f, 1));
        list3.add(new RedPacket(24, 2.4f,1));
        list3.add(new RedPacket(24, 3.3f,1));
        list3.add(new RedPacket(25, 2.5f,1));
        list3.add(new RedPacket(25, 3.5f,1));
        list3.add(new RedPacket(26, 1.8f,1));
        list3.add(new RedPacket(26, 1.8f,1));
        list3.add(new RedPacket(26,2.6f,2));
        list3.add(new RedPacket(26,2.6f,1));
        list3.add(new RedPacket(26,2.6f,2));
        list3.add(new RedPacket(26, 3.4f,1));
        list3.add(new RedPacket(26, 3.6f,1));
//        list3.add(new RedPacket(27, 2.8f,1));
//        list3.add(new RedPacket(27, 3.5f,1));
//        list3.add(new RedPacket(28, 2.8f,1));
//        list3.add(new RedPacket(28, 3.7f,1));
//        list3.add(new RedPacket(29, 2.9f,1));
//        list3.add(new RedPacket(30, 2.4f, 1));
//        list3.add(new RedPacket(30, 3.0f,2));
//        list3.add(new RedPacket(30, 5.0f,1));
//        list3.add(new RedPacket(31, 3.1f,1));
//        list3.add(new RedPacket(32, 3.2f,1));
//        list3.add(new RedPacket(32, 4.1f,1));
//        list3.add(new RedPacket(33, 2.6f, 2));
//        list3.add(new RedPacket(33, 3.3f,2));
//        list3.add(new RedPacket(34, 2.4f,1));
        list3.add(new RedPacket(34, 4.4f,1));
        list3.add(new RedPacket(35, 2.8f,1));
        list3.add(new RedPacket(35, 3.5f,1));
        list3.add(new RedPacket(36, 3.6f,2));
        list3.add(new RedPacket(37, 2.6f,1));
        list3.add(new RedPacket(37, 3.7f,1));
        list3.add(new RedPacket(37, 3.7f,2));
        list3.add(new RedPacket(38, 3.8f,1));
        list3.add(new RedPacket(38, 4.9f,1));
        list3.add(new RedPacket(39, 2.7f,1));
        list3.add(new RedPacket(39, 3.1f, 2));
        list3.add(new RedPacket(39, 3.9f,1));
        list3.add(new RedPacket(39, 3.9f,1));
        list3.add(new RedPacket(39, 4.1f,1));
        list3.add(new RedPacket(40, 3.2f,1));
        list3.add(new RedPacket(40, 4,1));
        list3.add(new RedPacket(41, 4.1f,1));
        list3.add(new RedPacket(43, 3.4f, 1));
        list3.add(new RedPacket(43, 4.3f,1));
        list3.add(new RedPacket(44, 4.4f,1));
        list3.add(new RedPacket(44, 4.6f,1));
        list3.add(new RedPacket(45, 3.6f, 1));
        list3.add(new RedPacket(45, 6.1f,1));
        list3.add(new RedPacket(45, 4.5f,1));
        list3.add(new RedPacket(46, 4.6f,1));
        list3.add(new RedPacket(46, 4.8f,1));
        list3.add(new RedPacket(46, 6f,1));
        list3.add(new RedPacket(47, 4.7f,1));
        list3.add(new RedPacket(47, 4.7f,1));
        list3.add(new RedPacket(48, 4.8f,2));
        list3.add(new RedPacket(48, 6.2f,1));
        list3.add(new RedPacket(49, 3.9f, 2));
        list3.add(new RedPacket(50, 5f,1));
        list3.add(new RedPacket(51, 6.6f,1));
        list3.add(new RedPacket(52, 6.8f,1));
        list3.add(new RedPacket(52, 7.0f,1));
        list3.add(new RedPacket(53, 7.2f,1));
        list3.add(new RedPacket(56, 3.9f,1));
        list3.add(new RedPacket(59, 3.9f,1));
        list3.add(new RedPacket(59, 3.9f,1));
        list3.add(new RedPacket(60, 2f,1));
        list3.add(new RedPacket(60, 4.8f,1));
        list3.add(new RedPacket(63, 4.4f,1));
//        list3.add(new RedPacket(64, 6.4f,1));
//        list3.add(new RedPacket(66, 5.3f, 1));
//        list3.add(new RedPacket(69, 4.8f,1));
//        list3.add(new RedPacket(80, 5f,1));
//        list3.add(new RedPacket(85,8.5f,1));


        productListFragment.setProductList(list1);
        fullCutListFragment.setFullCutList(list2);
        redPacketListFragment.setRedPacketList(list3);

        productListFragment.notifyDataSetChanged();
        fullCutListFragment.notifyDataSetChanged();
        redPacketListFragment.notifyDataSetChanged();

    }

    /**
     * 测试数据, 有一个商品单个点更好 , 用于测特价商品
     */
    private void testData2() {
        ((EditText) findViewById(R.id.min_total_price_et)).setText("20");
        ((EditText) findViewById(R.id.freight_price_et)).setText("");
        ((EditText) findViewById(R.id.order_count_et)).setText("");
        ((EditText) findViewById(R.id.other_cut_et)).setText("");

//        list1.add(new Product(12, 1));
//        list1.add(new Product(15, 5));
//        list1.add(new Product(16, 1));
//        list1.add(new Product(17, 1));
//        list1.add(new Product(23, 1));
//
//        list2.add(new FullCut(20, 6));
//        list2.add(new FullCut(30, 8));
//        list2.add(new FullCut(40, 10));
//        list2.add(new FullCut(50, 12));
//
//        list3.add(new RedPacket(17, 1.2f));
//        list3.add(new RedPacket(26, 1.8f));
//        list3.add(new RedPacket(28, 2.8f));
//        list3.add(new RedPacket(29, 2.9f));
//        list3.add(new RedPacket(30, 3.0f));
//        list3.add(new RedPacket(31, 3.1f));
//        list3.add(new RedPacket(37, 2.6f));
//        list3.add(new RedPacket(39, 2.7f));
//        list3.add(new RedPacket(47, 4.7f));
//        list3.add(new RedPacket(47, 4.7f));
//        list3.add(new RedPacket(59, 3.9f));
//        list3.add(new RedPacket(69, 4.8f));
    }


    // <Params, Progress, Result>
    private class MyTask extends AsyncTask<Void, Integer, Void> {

        List<Product> productList;
        List<FullCut> fullCutList;
        List<RedPacket> redPacketList;
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            DialogHelper.getInstance().showWaitDialog(CalculationActivity.this, "加载数据中...");
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Void doInBackground(Void... params) {

            productList = DataBaseHelper.getProductList();
            fullCutList = DataBaseHelper.getFullCutList();
            redPacketList = DataBaseHelper.getRedPacketList();

            return null;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果

        @Override
        protected void onPostExecute(Void aVoid) {

            productListFragment.setProductList(productList);
            fullCutListFragment.setFullCutList(fullCutList);
            redPacketListFragment.setRedPacketList(redPacketList);

            productListFragment.notifyDataSetChanged();
            fullCutListFragment.notifyDataSetChanged();
            redPacketListFragment.notifyDataSetChanged();

//            testData();

            DialogHelper.getInstance().dismissWaitDialog(CalculationActivity.this);
        }


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            startActivity();

            HashMap<String, String> map = new HashMap<>();
            map.put("status", "cal");
            MobclickAgentUtils.onEvent(this, MobclickAgentUtils.EVENT_CAL, map);

        }
    }

    /**
     * 跳转到计算结果页面
     */
    private void startActivity() {
        // 添加选择的项
        ArrayList<Product> productList = new ArrayList<>();
        for (int i = 0; i < productListFragment.getProductList().size(); i++) {
            if (productListFragment.getProductList().get(i).isUse())
                productList.add(productListFragment.getProductList().get(i));
        }
        ArrayList<FullCut> fullCutList = new ArrayList<>();
        for (int i = 0; i < fullCutListFragment.getFullCutList().size(); i++) {
            if (fullCutListFragment.getFullCutList().get(i).isUse())
                fullCutList.add(fullCutListFragment.getFullCutList().get(i));
        }
        ArrayList<RedPacket> redPacketList = new ArrayList<>();
        if (redPacketListFragment.isUseRedPacket()) {
            for (int i = 0; i < redPacketListFragment.getRedPacketList().size(); i++) {
                if (redPacketListFragment.getRedPacketList().get(i).isUse())
                    redPacketList.add(redPacketListFragment.getRedPacketList().get(i));
            }
        }

        Intent intent = new Intent(this, CalculationResultActivity.class);
        intent.putParcelableArrayListExtra("productList", productList);
        intent.putParcelableArrayListExtra("fullCutList", fullCutList);
//        if (redPacketListFragment.isUseRedPacket())
            intent.putParcelableArrayListExtra("redPacketsList", redPacketList);
        intent.putExtra("isUseRedPackets", redPacketListFragment.isUseRedPacket());
        intent.putExtra("isAddPackingFee2Cal", productListFragment.isAddPackingFee2Cal());
        intent.putExtra("isAddFreight2Cal", productListFragment.isAddFreight2Cal());
        intent.putExtra("minTotalPrice", productListFragment.getMinTotalPrice());
        intent.putExtra("freight", productListFragment.getFreight());
        intent.putExtra("otherCut", productListFragment.getOtherCut());
        intent.putExtra("maxOrderCount", productListFragment.getMaxOrderCount());
        intent.putExtra("maxRedPacketCount", productListFragment.getMaxRedPacketCount());
        intent.putExtra("cutType", productListFragment.getCutType());

        startActivity(intent);

    }



    @Override
    public void onResume() {
        super.onResume();
        MobclickAgentUtils.onPageStart("输入页面");
        MobclickAgentUtils.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgentUtils.onPageEnd("输入页面");
        MobclickAgentUtils.onPause(this);
    }

    @Override
    public void finish() {
        super.finish();
        System.exit(0);
    }
}

