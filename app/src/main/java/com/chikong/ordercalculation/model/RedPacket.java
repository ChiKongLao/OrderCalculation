package com.chikong.ordercalculation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by ChiKong on 16/05/11.
 * 红包项
 */
@Table(name = "redpacket_list")
public class RedPacket extends FullCut implements Parcelable {
    public static final String ID = "id";
    public static final String HASUSE = "hasUse";
    public static final String TYPE = "type";

    @Id(column = "id")
    private int id;
    /** 是否已经使用 */
    @Column(column = "hasUse")
    private boolean hasUse = false;
    /** 区分红包的所有者, 简单地用自然数区分 */
    @Column(column = "type")
    private int type;

    public RedPacket(){}
    /**
     * 创建红包
     * @param full 满
     * @param cut  减
     */
    public RedPacket(float full,float cut){
        setFull(full);
        setCut(cut);
        setType(1);
    }

    /**
     * 创建红包
     * @param full 满
     * @param cut  减
     * @param type  所有者
     */
    public RedPacket(float full,float cut,int type){
        this(full,cut);
        setType(type);
    }

    public boolean isHasUse() {
        return hasUse;

    }
    /** 打印红包信息 */
    public String print(){
        String string;
        if (type == 0){
            string =  "红包"+"=("+this.getFull()+"-"+this.getCut() +")";
        }else{
            string =  "红包"+type+"=("+this.getFull()+"-"+this.getCut() +")";
        }
        return string;
    }

    public void setHasUse(boolean hasUse) {
        this.hasUse = hasUse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getFull() == ((RedPacket)obj).getFull() && this.getCut() == ((RedPacket)obj).getCut()
                && this.getType() == ((RedPacket)obj).getType() && this.isHasUse() == ((RedPacket)obj).isHasUse();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeByte(this.hasUse ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected RedPacket(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.hasUse = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<RedPacket> CREATOR = new Creator<RedPacket>() {
        @Override
        public RedPacket createFromParcel(Parcel source) {
            return new RedPacket(source);
        }

        @Override
        public RedPacket[] newArray(int size) {
            return new RedPacket[size];
        }
    };
}
