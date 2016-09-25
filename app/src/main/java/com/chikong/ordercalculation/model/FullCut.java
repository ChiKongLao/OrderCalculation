package com.chikong.ordercalculation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by ChiKong on 2016/4/7.
 * 满减项
 */

@Table(name = "fullcut_list")
public class FullCut implements Comparable<FullCut>, Parcelable {

    public static final String ID = "id";
    public static final String FULL = "full";
    public static final String CUT = "cut";
    public static final String ISUSE = "isUse";


    @Id(column = "id")
    private int id;
    /**  满 */
    @Column(column = "full")
    private Float full = 0f;
    /** 减 */
    @Column(column = "cut")
    private Float cut = 0f;
    /** UI项, 是否使用  */
    @Column(column = "isUse")
    private boolean isUse = true;

    public FullCut() {
    }

    public FullCut(float full, float cut) {
        this.full = full;
        this.cut = cut;
//        this.count = 1;
    }

    public Float getFull() {
        return full;
    }

    public void setFull(float full) {
        this.full = full;
    }

    public Float getCut() {
        return cut;
    }

    public void setCut(float cut) {
        this.cut = cut;
    }


    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String print() {
        return "满减=(" + getFull() + "-" + getCut()+")";

    }

    @Override
    public int compareTo(FullCut another) {
        if (another.getCut().compareTo(this.getCut()) != 0)
            return another.getCut().compareTo(this.getCut());
        return this.getFull().compareTo(another.getFull());

    }

    @Override
    public boolean equals(Object obj) {
        return this.getFull() == ((FullCut)obj).getFull() && this.getCut() == ((FullCut)obj).getCut();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeValue(this.full);
        dest.writeValue(this.cut);
        dest.writeByte(this.isUse ? (byte) 1 : (byte) 0);
    }

    protected FullCut(Parcel in) {
        this.id = in.readInt();
        this.full = (Float) in.readValue(Float.class.getClassLoader());
        this.cut = (Float) in.readValue(Float.class.getClassLoader());
        this.isUse = in.readByte() != 0;
    }

    public static final Creator<FullCut> CREATOR = new Creator<FullCut>() {
        @Override
        public FullCut createFromParcel(Parcel source) {
            return new FullCut(source);
        }

        @Override
        public FullCut[] newArray(int size) {
            return new FullCut[size];
        }
    };
}
