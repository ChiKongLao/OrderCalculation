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
import android.widget.LinearLayout;
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
import com.chikong.ordercalculation.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FullCutListFragment extends Fragment implements OnClickListener
        , OnRecyclerViewItemClickListener {

    @Bind(R.id.red_packets_cb)
    CheckBox redPacketsCb;
    @Bind(R.id.cancel_btn)
    Button cancelBtn;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.add_layout)
    CardView addLayout;

    /**
     * fragment类型
     */
    private int mType;

    private View mRootView;
    private RecyclerViewAdapter adapter;

    /**
     * 满减列表
     */
    private List<FullCut> mFullCutList = new ArrayList<>();
    /**
     * 红包列表
     */
    private List<RedPacket> mRedPacketList = new ArrayList<>();


    public static FullCutListFragment newInstance(int type) {
        FullCutListFragment fragment = new FullCutListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullcut_list, container, false);
        ButterKnife.bind(this, mRootView);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt("type", Constants.FRAGMENT_TYPE_FULLCUT);
        }

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

        redPacketsCb.setChecked(SettingUtils.getSetting(SettingUtils.IS_USE_REDPACKETS, true));
        redPacketsCb.setVisibility(mType == Constants.FRAGMENT_TYPE_FULLCUT ? View.GONE : View.VISIBLE);
        redPacketsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                recyclerView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        cancelBtn.setOnClickListener(this);
        addLayout.setOnClickListener(this);

    }

    private void initControl() {

        adapter = new RecyclerViewAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    private void loadData() {


    }


    /**
     * 添加满减项 , 默认保存到数据库
     *
     * @param fullCut 满减项
     */
    private void addFullCut(FullCut fullCut) {
        addFullCut(fullCut, true);
    }

    /**
     * 添加满减项
     *
     * @param fullCut 满减项
     * @param isSave  是否保存到数据库
     */
    private void addFullCut(FullCut fullCut, boolean isSave) {
        addFullCutOrRedPacket(Constants.TYPE_FULL_CUT, fullCut, null, isSave);
    }


    /**
     * 添加红包项 , 默认保存到数据库
     *
     * @param redPacket 红包项
     */
    private void addRedPacket(RedPacket redPacket) {
        addRedPacket(redPacket, true);
    }

    /**
     * 添加红包项
     *
     * @param redPacket 红包项
     * @param isSave    是否保存到数据库
     */
    private void addRedPacket(RedPacket redPacket, boolean isSave) {
        addFullCutOrRedPacket(Constants.TYPE_RED_PACKETS, null, redPacket, isSave);
    }

    /**
     * 添加满减项
     *
     * @param type      满减还是红包
     * @param fullCut   满减项
     * @param redPacket 红包项
     * @param isSave    是否保存到数据库
     */
    private void addFullCutOrRedPacket(final int type, final FullCut fullCut, final RedPacket redPacket, boolean isSave) {
        int index = 0;
        final float full;
        float cut;
        // 根据不同分类设置不同信息
        if (type == Constants.TYPE_FULL_CUT) {
            full = fullCut.getFull();
            cut = fullCut.getCut();
            index = mFullCutList.size();
            for (int i = 0; i < mFullCutList.size(); i++) {
                if (full < mFullCutList.get(i).getFull()
                        || (full == mFullCutList.get(i).getFull() && cut < mFullCutList.get(i).getCut())) {
                    index = i;
                    break;
                }
            }
            mFullCutList.add(index, fullCut);
            if (isSave) DataBaseHelper.addFullCut(fullCut);
            LogHelper.trace("add fullCut: " + fullCut.print());
        } else {
            full = redPacket.getFull();
            cut = redPacket.getCut();
            index = mRedPacketList.size();
            for (int i = 0; i < mRedPacketList.size(); i++) {
                if (full < mRedPacketList.get(i).getFull()
                        || (full == mRedPacketList.get(i).getFull() && cut < mRedPacketList.get(i).getCut())
                        || (full == mRedPacketList.get(i).getFull() && cut == mRedPacketList.get(i).getCut())
                             && redPacket.getType() < mRedPacketList.get(i).getType()) {
                    index = i;
                    break;
                }
            }
            mRedPacketList.add(index, redPacket);
            if (isSave) DataBaseHelper.addRedPacket(redPacket);
            LogHelper.trace("add redPackets: " + redPacket.print());
        }

        adapter.notifyDataSetChanged();

    }


    @Override
    public void onClick(View v) {
        LogHelper.trace("*** onClick ***");

        if (v.getId() == R.id.add_layout) {
            DialogHelper.showAddFullCutDialog(getActivity(), new OnDialogClickListener() {
                @Override
                public void onPositiveButtonClick(DialogInterface dialog, int which, Object object) {
                    addFullCut((FullCut) object);
                }

                @Override
                public void onNegativeButtonClick(DialogInterface dialog, int which, Object object) {
                }

                @Override
                public void onNeutralButtonClick(DialogInterface dialog, int which, Object object) {
                    addLayout.performClick();
                }
            });
        } else if (v.getId() == R.id.cancel_btn) {
            // 全不选
            for (int i = 0; i < getDataList().size(); i++) {
                FullCut fullCut = (FullCut) getDataList().get(i);
                fullCut.setUse(false);

            }
            adapter.notifyDataSetChanged();

        }
    }


    @Override
    public void onItemClick(View view, final int position) {
        LogHelper.trace("*** onItemClick *** position = " + position);
        if (position < 0 || position > getDataList().size() - 1) {
            LogHelper.trace("OutOfIndex --> index = " + position);
            return;
        }
        final FullCut fullCut = mFullCutList.size() > 0 ? mFullCutList.get(position):null;
        final RedPacket redPacket = mRedPacketList.size() > 0 ? mRedPacketList.get(position):null;
        DialogHelper.showAddFullCutOrRedPacketDialog(getActivity(), fullCut, redPacket, new OnDialogClickListener() {
            @Override
            public void onPositiveButtonClick(DialogInterface dialog, int which, Object object) {
                if (mType == Constants.TYPE_FULL_CUT) {
                    DataBaseHelper.updateFullCut(((FullCut) object));
                    LogHelper.trace("update fullCut: " + ((FullCut) object).print());
                } else {
                    DataBaseHelper.updateRedPacket(((RedPacket) object));
                    LogHelper.trace("update redPackets: " + ((RedPacket) object).print());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNegativeButtonClick(DialogInterface dialog, int which, Object object) {
            }

            @Override
            public void onNeutralButtonClick(DialogInterface dialog, int which, Object object) {
                if (mType == Constants.TYPE_FULL_CUT) {
                    addLayout.performClick();
                }
            }
        }, mType);
    }

    public void notifyDataSetChanged(){
        if (adapter!=null) adapter.notifyDataSetChanged();
    }

    /**
     * 根据type获取数据
     */
    private List<?> getDataList() {
        return mType == Constants.FRAGMENT_TYPE_FULLCUT ? mFullCutList : mRedPacketList;
    }

    public List<FullCut> getFullCutList() {
        return mFullCutList;
    }

    public void setFullCutList(List<FullCut> fullCutList) {
        this.mFullCutList = fullCutList;
    }

    public List<RedPacket> getRedPacketList() {
        return mRedPacketList;
    }

    public void setRedPacketList(List<RedPacket> redPacketList) {
        this.mRedPacketList = redPacketList;
    }

    public boolean isUseRedPacket(){
        SettingUtils.setSetting(SettingUtils.IS_USE_REDPACKETS, redPacketsCb.isChecked());
        return redPacketsCb.isChecked();
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
            View view = LayoutInflater.from(context).inflate(R.layout.item_full_cut, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final FullCut fullCut = mFullCutList.size() > 0 ? mFullCutList.get(position):null;
            final RedPacket redPacket = mRedPacketList.size() > 0 ? mRedPacketList.get(position):null;
            float full;
            float cut;


            if (mType == Constants.FRAGMENT_TYPE_FULLCUT) {
                holder.typeTv.setVisibility(View.INVISIBLE);
                full = fullCut.getFull();
                cut = fullCut.getCut();
            }else{
                holder.typeTv.setText("所属: " + redPacket.getType());
                holder.typeTv.setVisibility(View.VISIBLE);
                full = redPacket.getFull();
                cut = redPacket.getCut();
            }

            holder.fullTv.setText("满: " + full);
            holder.cutTv.setText("减: " + cut);


            holder.clearBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mType == Constants.FRAGMENT_TYPE_FULLCUT) {
                        mFullCutList.remove(position);
                        DataBaseHelper.delFullCut(fullCut);
                    } else {
                        mRedPacketList.remove(position);
                        DataBaseHelper.delRedPacket(redPacket);
                    }

                }
            });

            holder.checkBox.setChecked(mType == Constants.FRAGMENT_TYPE_FULLCUT?fullCut.isUse():redPacket.isUse());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (mType == Constants.FRAGMENT_TYPE_FULLCUT) {
                        fullCut.setUse(isChecked);
                        DataBaseHelper.updateFullCut(fullCut);
                    } else {
                        redPacket.setUse(isChecked);
                        DataBaseHelper.updateRedPacket(redPacket);
                    }
                     }
            });


            holder.layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) onItemClickListener.onItemClick(v, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return getDataList().size();
        }

        public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.layout)
            LinearLayout layout;
            @Bind(R.id.checkBox)
            CheckBox checkBox;
            @Bind(R.id.full_tv)
            TextView fullTv;
            @Bind(R.id.cut_tv)
            TextView cutTv;
            @Bind(R.id.type_tv)
            TextView typeTv;
            @Bind(R.id.button)
            Button clearBtn;

            private MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

    }

}
