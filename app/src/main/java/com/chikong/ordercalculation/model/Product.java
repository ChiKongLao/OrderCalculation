package com.chikong.ordercalculation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by ChiKong on 2016/4/8.
 *商品项
 */
@Table(name = "product_list")
public class Product implements Parcelable,Serializable,Comparable<Product> {
    public static final String ID = "id";
    public static final String PRICE = "price";
    public static final String COUNT = "count";
    public static final String CUT = "cut";
    public static final String PACKINGFEE = "packingFee";
    public static final String ISNEEDCUT = "isNeedCut";
    public static final String ISUSE = "isUse";

    @Id(column = "id")
    private int id;
    /** 价格 */
    @Column(column = "price")
    private float price;
    /** 数量 */
    @Column(column = "count")
    private int count;
    /** 减 */
    @Column(column = "cut")
    private float cut;
    /**包装费*/
    @Column(column = "packingFee")
    private float packingFee;

    /** 是否计算优惠 */
    @Column(column = "isNeedCut")
    private boolean isNeedCut = true;
    /** UI项, 是否使用  */
    @Column(column = "isUse")
    private boolean isUse = true;
    
    public Product() {}

    /**
     * 创建商品类
     * @param price 价格
     */
    public Product(float price) {
        this.price = price;
        this.count = 1;
    }

    /**
     * 创建商品类
     * @param price 价格
     * @param count 数量
     */
    public Product(float price,int count) {
        this(price);
        this.count = count;
    }
    /**
     * 创建商品类
     * @param price 价格
     * @param count 数量
     * @param packingFee 包装费
     */
    public Product(float price,int count,float packingFee) {
        this(price, count);
        this.packingFee = packingFee;
    }
    /**
     * 创建商品类
     * @param price 价格
     * @param count 数量
     * @param isNeedCut 是否计算优惠
     */
    public Product(float price,int count,boolean isNeedCut) {
    	this(price,count);
        this.isNeedCut = isNeedCut;
    }
    /**
     * 创建商品类
     * @param price 价格
     * @param count 数量
     * @param packingFee 包装费
     * @param isNeedCut 是否计算优惠
     */
    public Product(float price,int count,float packingFee,boolean isNeedCut) {
    	this(price,count,packingFee);
        this.isNeedCut = isNeedCut;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getCut() {
        return cut;
    }

    public void setCut(float cut) {
        this.cut = cut;
    }

    public boolean isNeedCut() {
        return isNeedCut;
    }

    public void setNeedCut(boolean needCut) {
        isNeedCut = needCut;
    }

    public float getPackingFee() {
        return packingFee;
    }

    public void setPackingFee(float packingFee) {
        this.packingFee = packingFee;
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

    public String print(){
       return "价格= " + getPrice() + " 数量= " + getCount()+" 优惠= "+getCut()+" 包装费= "+getPackingFee();

    }

    @Override
    public boolean equals(Object object) {
        return this.price == ((Product)object).getPrice()
                && this.packingFee == ((Product)object).getPackingFee();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Product product = new Product();
        product.setPrice(this.price);
        product.setCount(this.count);
        product.setCut(this.cut);
        product.setPackingFee(this.packingFee);
        product.setNeedCut(this.isNeedCut);
        product.setUse(this.isUse);
        return product;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeFloat(this.price);
        dest.writeInt(this.count);
        dest.writeFloat(this.cut);
        dest.writeFloat(this.packingFee);
        dest.writeByte(this.isNeedCut ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUse ? (byte) 1 : (byte) 0);
    }

    protected Product(Parcel in) {
        this.id = in.readInt();
        this.price = in.readFloat();
        this.count = in.readInt();
        this.cut = in.readFloat();
        this.packingFee = in.readFloat();
        this.isNeedCut = in.readByte() != 0;
        this.isUse = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int compareTo(Product another) {
        if (this.price != another.price)
            return Float.valueOf(this.price).compareTo(another.price);
        if (this.packingFee != another.packingFee)
            return Float.valueOf(this.packingFee).compareTo(another.packingFee);
        return 0;

    }
}
