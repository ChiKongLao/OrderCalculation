package com.chikong.ordercalculation.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.R;
import com.chikong.ordercalculation.listener.OnDialogClickListener;
import com.chikong.ordercalculation.listener.OnRecyclerViewItemClickListener;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;
import com.chikong.ordercalculation.utils.DataBaseHelper;
import com.chikong.ordercalculation.utils.DialogHelper;
import com.chikong.ordercalculation.utils.LogHelper;
import com.chikong.ordercalculation.utils.MobclickAgentUtils;
import com.chikong.ordercalculation.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductListFragment extends Fragment implements OnClickListener
        , OnRecyclerViewItemClickListener {

    @Bind(R.id.min_total_price_et)
    EditText minTotalPriceEt;
    @Bind(R.id.freight_price_et)
    EditText freightPriceEt;
    @Bind(R.id.other_cut_et)
    EditText otherCutEt;
    @Bind(R.id.red_packets_count_et)
    EditText redPacketsCountEt;
    @Bind(R.id.order_count_et)
    EditText orderCountEt;
    @Bind(R.id.average_rb)
    RadioButton averageRb;
    @Bind(R.id.percent_rb)
    RadioButton percentRb;
    @Bind(R.id.freight_cb)
    CheckBox freightCb;
    @Bind(R.id.packing_fee_cb)
    CheckBox packingFeeCb;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.add_layout)
    CardView addLayout;

    private View mRootView;

    private RecyclerViewAdapter adapter;

    /** 价格列表 */
    private List<Product> mDataList = new ArrayList<>();

    public static ProductListFragment newInstance() {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
//        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, mRootView);

        initView();
        initControl();

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        loadData();
    }


    private void initView() {

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        addLayout.setOnClickListener(this);

        int  cutType = SettingUtils.getSetting(SettingUtils.CUT_TYPE, Constants.TYPE_CUT_AVERAGE);
        if (cutType == Constants.TYPE_CUT_AVERAGE) {
            averageRb.setChecked(true);
        } else if (cutType == Constants.TYPE_CUT_PERCENT) {
            percentRb.setChecked(true);
        }
        packingFeeCb.setChecked(SettingUtils.getSetting(SettingUtils.IS_ADD_PACKINGFEE_2_CAL, false));
        freightCb.setChecked(SettingUtils.getSetting(SettingUtils.IS_ADD_FREIGHT_2_CAL, false));
        minTotalPriceEt.setText(SettingUtils.getSetting(SettingUtils.MIN_TOTAL_PRICE, 0f)+"");
        freightPriceEt.setText(SettingUtils.getSetting(SettingUtils.FREIGHT_PRICE, 0f)+"");
        orderCountEt.setText(SettingUtils.getSetting(SettingUtils.ORDER_COUNT, 0)+"");
        redPacketsCountEt.setText(SettingUtils.getSetting(SettingUtils.REDPACKET_COUNT, 2)+"");
        otherCutEt.setText(SettingUtils.getSetting(SettingUtils.OTHER_CUT, 0f)+"");
    }

    private void initControl() {

        adapter = new RecyclerViewAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    private void loadData() {


    }


    /**
     * 添加商品项 , 默认保存到数据库
     * @param product 商品项
     */
    private void addProduct(final Product product) {
        addProduct(product, true);
    }

    /**
     * 添加商品项
     *
     * @param product 商品项
     * @param isSave  是否保存到数据库
     */
    private void addProduct(final Product product, boolean isSave) {
        boolean isExist = false;
        int position = mDataList.size();
        for (int i = 0; i < mDataList.size(); i++) {
            Product forProduct = mDataList.get(i);
            if (product.getPrice() < forProduct.getPrice()) {
                position = i;
                break;
                // 如果该商品已存在, 则+count
            } else if (product.equals(forProduct)) {
                forProduct.setCount(forProduct.getCount()+product.getCount());
                isExist = true;
                position = i;
                break;
            }
        }
        // 已经存在，刚插入
        if (isExist){
            if (isSave) DataBaseHelper.updateProduct(mDataList.get(position));
        }else {
            mDataList.add(position,product);
            if (isSave) DataBaseHelper.addProduct(product);
        }

        Collections.sort(mDataList);
        adapter.notifyDataSetChanged();

        LogHelper.trace("add product: " + product.print());


    }

    @Override
    public void onClick(View v) {
        LogHelper.trace("*** onClick ***");
        LogHelper.trace("***OnClick***");
        // 删除焦点
        minTotalPriceEt.clearFocus();
        freightPriceEt.clearFocus();
        otherCutEt.clearFocus();
        redPacketsCountEt.clearFocus();
        orderCountEt.clearFocus();

        if (v.getId() == R.id.add_layout) {
            DialogHelper.showAddProductDialog(getActivity(), new OnDialogClickListener() {
                @Override
                public void onPositiveButtonClick(DialogInterface dialog, int which, Object object) {
                    addProduct((Product) object);

                }

                @Override
                public void onNegativeButtonClick(DialogInterface dialog, int which, Object object) {
                }

                @Override
                public void onNeutralButtonClick(DialogInterface dialog, int which, Object object) {
                    addLayout.performClick();
                }
            });

        }
    }
//    }

    @Override
    public void onItemClick(View view, final int position) {
        LogHelper.trace("*** onItemClick *** position = " + position);
		if (position < 0 || position > mDataList.size() - 1) {
			LogHelper.trace("OutOfIndex --> index = "+position);
			return;
		}
        Product product = mDataList.get(position);
        DialogHelper.showAddProductDialog(getActivity(), product, new OnDialogClickListener() {
            @Override
            public void onPositiveButtonClick(DialogInterface dialog, int which, Object object) {
                Product returnProduct = (Product) object;
                mDataList.set(position,returnProduct);
                Collections.sort(mDataList);
                adapter.notifyDataSetChanged();
                DataBaseHelper.updateProduct(returnProduct);

            }

            @Override
            public void onNegativeButtonClick(DialogInterface dialog, int which, Object object) {
            }

            @Override
            public void onNeutralButtonClick(DialogInterface dialog, int which, Object object) {
                ButterKnife.findById(getActivity(),R.id.add_layout).performClick();
            }
        });
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public void setProductList(List<Product> dataList) {
        this.mDataList = dataList;
    }
    public List<Product> getProductList(){return mDataList;}

    public int getCutType(){
        int cutType = averageRb.isChecked()?
                Constants.TYPE_CUT_AVERAGE:Constants.TYPE_CUT_PERCENT;
        SettingUtils.setSetting(SettingUtils.CUT_TYPE, cutType);
        return cutType;
    }
    public boolean isAddPackingFee2Cal(){
        SettingUtils.setSetting(SettingUtils.IS_ADD_PACKINGFEE_2_CAL, packingFeeCb.isChecked());
        return packingFeeCb.isChecked();
    }
    public boolean isAddFreight2Cal(){
        SettingUtils.setSetting(SettingUtils.IS_ADD_FREIGHT_2_CAL, freightCb.isChecked());
        return freightCb.isChecked();
    }
    public float getMinTotalPrice(){
        String string = minTotalPriceEt.getText().toString();
        float value = "".equals(string) ? 0 : Float.valueOf(string);
        SettingUtils.setSetting(SettingUtils.MIN_TOTAL_PRICE, value);
        return value;
    }
    public float getFreight(){
        String string = freightPriceEt.getText().toString();
        float value = "".equals(string) ? 0 : Float.valueOf(string);
        SettingUtils.setSetting(SettingUtils.FREIGHT_PRICE, value);
        return value;
    }
    public float getOtherCut(){
        String string = otherCutEt.getText().toString();
        float value = "".equals(string) ? 0 : Float.valueOf(string);
        SettingUtils.setSetting(SettingUtils.OTHER_CUT, value);
        return value;
    }
    public int getMaxOrderCount(){
        String string = orderCountEt.getText().toString();
        int value = "".equals(string) ? 0 : Integer.valueOf(string);
        SettingUtils.setSetting(SettingUtils.ORDER_COUNT, value);
        return value;
    }
    public int getMaxRedPacketCount(){
        String string = redPacketsCountEt.getText().toString();
        int value = "".equals(string) ? 0 : Integer.valueOf(string);
        SettingUtils.setSetting(SettingUtils.REDPACKET_COUNT, value);
        return value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
            private Context context;
            private OnRecyclerViewItemClickListener onItemClickListener;

            public RecyclerViewAdapter(Context context) {
                this.context = context;

            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
                MyViewHolder viewHolder = new MyViewHolder(view);
                return viewHolder;
            }
            @Override
            public void onBindViewHolder(MyViewHolder holder, final int position) {
                final Product product = mDataList.get(position);
                holder.productTv.setText("单价: "+product.getPrice());
                holder.countTv.setText("数量: x"+product.getCount());
                holder.packingFeeTv.setText("包装费: "+product.getPackingFee());
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        product.setUse(isChecked);
                        DataBaseHelper.updateProduct(product);
                    }
                });
                holder.checkBox.setChecked(product.isUse());
                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataList.remove(product);
                        DataBaseHelper.delProduct(product);
                        recyclerView.removeViewAt(position);
                    }
                });
                holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) onItemClickListener.onItemClick(v,position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mDataList.size();
            }

            public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
                this.onItemClickListener = onItemClickListener;
            }

            class MyViewHolder extends RecyclerView.ViewHolder{
                @Bind(R.id.checkBox)
                CheckBox checkBox;
                @Bind(R.id.product_tv)
                TextView productTv;
                @Bind(R.id.count_tv)
                TextView countTv;
                @Bind(R.id.packing_fee_tv)
                TextView packingFeeTv;
                @Bind(R.id.button)
                Button delBtn;
                @Bind(R.id.layout)
                LinearLayout itemLayout;

                private MyViewHolder(View view) {
                    super(view);
                    ButterKnife.bind(this, view);
                }
            }
        }

}
