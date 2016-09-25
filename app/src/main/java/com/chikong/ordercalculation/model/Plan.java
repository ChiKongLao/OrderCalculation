package com.chikong.ordercalculation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chikong.ordercalculation.utils.MathUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ChiKong on 2016/4/8.
 * 优惠方案
 */
public class Plan implements  Comparable<Plan>,Parcelable{
	/**最终价格*/
	private float price ;
	/**原价格*/
	private float originalPrice ;
	/** 满减 */
	private FullCut fullCut = new FullCut();
	/** 红包减 */
	private RedPacket redPackets = new RedPacket();
	/**运费*/
	private float freight;
	/**包装费*/
	private float packingFee;
	/**其它优惠*/
	private float otherCut;
	/**子方案列表*/
	private List<Plan> subPlanList = new ArrayList<>();
	/**贪小便宜列表*/
	private List<GetMore> getMoreList = new ArrayList<>();
	/**商品列表*/
	private List<Product> productList = new ArrayList<>();

	public Plan() { }

	/**
	 * 创建方案
	 * @param product	商品项
	 */
	public Plan(Product product) {
		this();
		addProduct(product);
	}

	/**
	 * 创建方案
	 * @param product	商品项
	 * @param freight	运费
	 */
	public Plan(Product product, float freight) {
		this(product);
		setFreight(freight);
	}


	/**
	 * 创建方案
	 * @param list	方案中各项价格
	 */
	public Plan(List<Product> list) {
		this();
		float sum = 0;
		for (Product product : list) {
			addProduct(product);
			sum += product.getPrice();
		}
		setOriginalPrice(sum);

	}

	/**
	 * 创建方案
	 * @param list	方案中各项价格
	 * @param freight	运费
	 */
	public Plan(List<Product> list, float freight) {
		this(list);
		setFreight(freight);

	}

	public float getPrice() {
		setPrice(MathUtil.keepDecimal(getOriginalPrice() - getTotalCut()
				+ this.packingFee + this.freight));
		return price;
	}

//	public float getOriginalPriceWithFreightANDPackingFee(){
//		return getOriginalPrice() + getFreight() +getPackingFee();
//	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(float originalPrice) {
		this.originalPrice = MathUtil.keepDecimal(originalPrice);
		// 因为会导致死循环调用 , 所以不用getTotalCut
		setPrice(getOriginalPrice() - this.fullCut.getCut()-this.redPackets.getCut()-this.otherCut
				+ this.packingFee + this.freight);
	}
	/** 获取满减 */
	public FullCut getFullCut() {
		return this.fullCut;
	}

	/** 获取满减共减多少 */
	public float getFullCutSum() {
		float cut = this.fullCut.getCut();
		// 如果是顶部方案时,计算一次子方案的和
		if (this.subPlanList.size() > 0){
			cut = 0;
			for (int i = 0; i < this.subPlanList.size(); i++) {
				cut += subPlanList.get(i).getFullCut().getCut();
			}
		}

		return cut;
	}

	public void setFullCut(FullCut fullCut) {
		this.fullCut = fullCut;
		setPrice(getOriginalPrice() - this.fullCut.getCut()-this.redPackets.getCut()-this.otherCut
				+ this.packingFee + this.freight);
	}
	/** 获取红包 */
	public RedPacket getRedPackets() {
		return this.redPackets;
	}
	/** 获取满减共减多少 */
	public float getRedPacketsCutSum() {
		float cut = this.redPackets.getCut();
		// 如果是顶部方案时,计算一次子方案的和
		if (this.subPlanList.size() > 0) {
			cut = 0;
			for (int i = 0; i < this.subPlanList.size(); i++) {
				cut += subPlanList.get(i).getRedPackets().getCut();
			}
		}
		return cut;
	}

	public void setRedPackets(RedPacket redPackets) {
		this.redPackets = redPackets;
		setPrice(getOriginalPrice() - this.fullCut.getCut()-this.redPackets.getCut()-this.otherCut
				+ this.packingFee + this.freight);
	}

	public float getOtherCut() {
		float cut = this.otherCut;
		// 如果是顶部方案时,计算一次子方案的和
		if (this.subPlanList.size() > 0) {
			cut = 0;
			for (int i = 0; i < this.subPlanList.size(); i++) {
				cut += subPlanList.get(i).getOtherCut();
			}
		}
		setOtherCut(cut);
		return this.otherCut;
	}
	public void setOtherCut(float otherCut) {
		this.otherCut = otherCut;
		setPrice(getOriginalPrice() - this.fullCut.getCut()-this.redPackets.getCut()-this.otherCut
				+ this.packingFee + this.freight);
	}


	/**
	 * 共减多少（满减+红包）
	 * @return
	 */
	public float getTotalCut() {
		return getFullCutSum()+getRedPacketsCutSum()+getOtherCut();
	}

	/**
	 * 运费共多少
	 * @return
	 */
	public float getTotalFreight() {
		float sum = 0;
		for (int i = 0; i < this.subPlanList.size(); i++) {
			sum += subPlanList.get(i).getFreight();
		}
		return sum;
	}

	public List<Plan> getSubPlanList() {
		return subPlanList;
	}

	public void setSubPlanList(List<Plan> list) {
		this.subPlanList = list;
		float sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).getOriginalPrice();
		}
		setOriginalPrice(sum);
	}

	public void addSubPlan(Plan plan) {
		this.subPlanList.add(plan);
		setOriginalPrice(this.originalPrice + plan.getOriginalPrice());
		setPackingFee(this.packingFee + plan.getPackingFee());

	}
	public void addProduct(Product product) {
		this.productList.add(product);
		setOriginalPrice(this.originalPrice + product.getPrice());
		setPackingFee(this.packingFee + product.getPackingFee());

	}

	public float getFreight() {
		return freight;
	}
	public void setFreight(float freight) {
		this.freight = freight;
	}
	public float getPackingFee() {
		float packingFee = this.packingFee;
		// 如果是顶部方案时,计算一次子方案的和
		if (this.subPlanList.size() > 0){
			packingFee = 0;
			for (int i = 0; i < this.subPlanList.size(); i++) {
				packingFee += subPlanList.get(i).getPackingFee();
			}
			setPackingFee(packingFee);
		}

		return packingFee;
	}
	public void setPackingFee(float packingFee) {
		this.packingFee = packingFee;
	}

	/**
	 * 获取最下级的子方案的最终价格
	 * @return
	 */
	public String getSubPlanPriceList(){
		String string = "";
		for (Plan plan : subPlanList) {
			string = string + plan.getPrice()+"+";
		}
		if (string.length() != 0) return string.substring(0, string.length() - 1);
		return  string;
	}
	/**
	 * 获取商品项的最终价格
	 * @return
	 */
	public String getProductPriceList(){
		String string = "";
		for (Product product : productList) {
			string = string + product.getPrice()+"+";
		}
		if (string.length() != 0) return string.substring(0, string.length() - 1);
		return  string;
	}



	public void addGetMore(GetMore getMore){
		// 不重复才添加
		if (getMoreList.indexOf(getMore) == -1) getMoreList.add(getMore);
		Collections.sort(getMoreList);
	}
	public List<GetMore> getGetMoreList(){ return getMoreList;}

	/**
	 * 拼接方案结果字符串
	 * @return
	 */
	public String printSubPlan() {
		String string = "";
		float totalPrice = 0;
		totalPrice += getPackingFee();
		for (int i = 0; i < subPlanList.size(); i++) {
			Plan plan = subPlanList.get(i);
			totalPrice = MathUtil.keepDecimal(totalPrice + plan.getPrice());
			string = string+ plan.print();
			string = string + "\t";
		}
//		string =  string + "\t合计最终价格 = "+getPrice()+
//				"("+getSubPlanPriceList()+"), "+
//				"(含配送费 = "+getTotalFreight()+","+
//				"包装费 = "+getPackingFee()+"), "+
//				"合计共减 = "+MathUtil.keepDecimal((getTotalCut()-getFreight()-getPackingFee()))+"\n";
		string =  string + printSummary();

		return string;

	}


	/**打印信息*/
	public String print(){
		String string =" 原价 = "+this.getOriginalPrice()+
				"("+this.getProductPriceList()+
				"), 配送费 = "+this.getFreight()+
				", 包装费 = "+this.getPackingFee()+
				", "+this.printFullCut()+
				", 其它优惠 = "+this.getOtherCut()+
				", 共减 = "+MathUtil.keepDecimal((this.getTotalCut()-this.getFreight()-this.getPackingFee()))+
				"("+this.getTotalCut()+"-"+this.getFreight()+"-"+this.getPackingFee()+")"+
				"; 最终价格 = "+this.getPrice()+"\n";
		return  string;
	}
	/**打印满减信息*/
	public String printFullCut(){
		String string =this.getFullCut().print()+ ","+this.getRedPackets().print();
		return  string;
	}
	/**打印其它优惠信息*/
	public String printOtherCut(){
		String string = "其它优惠="+this.getOtherCut();
		return  string;
	}
	/**打印方案总结信息*/
	public String printSummary(){
		String string = "合计价格: "+getPrice()+" ("+getSubPlanPriceList()+")\n配送费= "+getTotalFreight()+"," +" 包装费= "
				+getPackingFee()+"\n合计优惠= "+MathUtil.keepDecimal((getTotalCut()-getFreight()-getPackingFee()));
		return  string;
	}

	@Override
	public int compareTo(Plan another) {
		if ((Float.valueOf(this.getPrice())).compareTo(another.getPrice()) != 0){
			return (Float.valueOf(this.getPrice())).compareTo(another.getPrice());
		}
		if (this.subPlanList.size() != another.subPlanList.size()){
			return (Float.valueOf(this.subPlanList.size()))
					.compareTo(Float.valueOf(another.subPlanList.size()));
		}
		return (Float.valueOf(this.getOriginalPrice())).compareTo(another.getOriginalPrice());

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(this.price);
		dest.writeFloat(this.originalPrice);
		dest.writeParcelable(this.fullCut, flags);
		dest.writeParcelable(this.redPackets, flags);
		dest.writeFloat(this.freight);
		dest.writeFloat(this.packingFee);
		dest.writeFloat(this.otherCut);
		dest.writeTypedList(this.subPlanList);
		dest.writeList(this.getMoreList);
		dest.writeTypedList(this.productList);
	}

	protected Plan(Parcel in) {
		this.price = in.readFloat();
		this.originalPrice = in.readFloat();
		this.fullCut = in.readParcelable(FullCut.class.getClassLoader());
		this.redPackets = in.readParcelable(RedPacket.class.getClassLoader());
		this.freight = in.readFloat();
		this.packingFee = in.readFloat();
		this.otherCut = in.readFloat();
		this.subPlanList = in.createTypedArrayList(Plan.CREATOR);
		this.getMoreList = new ArrayList<GetMore>();
		in.readList(this.getMoreList, GetMore.class.getClassLoader());
		this.productList = in.createTypedArrayList(Product.CREATOR);
	}

	public static final Creator<Plan> CREATOR = new Creator<Plan>() {
		@Override
		public Plan createFromParcel(Parcel source) {
			return new Plan(source);
		}

		@Override
		public Plan[] newArray(int size) {
			return new Plan[size];
		}
	};
}
