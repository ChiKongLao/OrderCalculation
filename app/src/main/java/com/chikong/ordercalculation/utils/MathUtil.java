package com.chikong.ordercalculation.utils;
import com.chikong.ordercalculation.model.Product;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ChiKong on 2016/4/8.
 */
public class MathUtil {

	private static final boolean isDebug = false;



	/**
	 * 转化数字，保留一位小数
	 * @param a
	 * @return
	 */
	public static float keepDecimal(float a) {
		return (float)(Math.round(a*100))/100;
	}

	/***
	 * 是否已经存在
	 * @param list		父List
	 * @param subList	想添加到父List的子List
	 * @return	是否存在
	 */
	private static boolean isExist(List<List<List<Product>>> list,List<List<Product>> subList){
		for (List<List<Product>> tmpList : list) {
			if (tmpList.containsAll(subList)) return true;
		}
		return false;
	}


	/**
	 * 获取组合集合
	 * @param maxChildCount 子集最大个数
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<List<Product>>> getCombinations(int maxChildCount,List<Product> list) {
		return getCombinations(0,maxChildCount, list);
	}

	/**
	 * 获取组合集合
	 * @param minValue 组合的和必须大于minValue
	 * @param maxChildCount 子集最大个数
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<List<Product>>> getCombinations(float minValue, int maxChildCount,List<Product> list) {
		List<List<List<Product>>> resultList = new ArrayList<>();
		if (list.size() == 2) {
			List<List<Product>> tmpList;
			// [[1],[2]]
			if (list.get(0).getPrice() >= minValue && list.get(1).getPrice() >= minValue) {
				tmpList = new ArrayList<>();
				tmpList.add(list.subList(0,1));
				tmpList.add(list.subList(1,2));
				resultList.add(tmpList);
			}

			// [[1,2]]
			if ((list.get(0).getPrice() + list.get(1).getPrice()) >= minValue) {
				tmpList = new ArrayList<>();
				tmpList.add(list);
				resultList.add(tmpList);
			}
			return resultList;
		} else {
			List<List<Product>> groups = getCombinationWithoutNullAndRemoveDuplicate(list);
			for (int i = 0; i < groups.size(); i++) {
				List<Product> list1 = groups.get(i); // 正常List
				List<Product> differenceList = getDifferenceList(list, list1); // list-list1
				// 组合的和大于最小值
				if (sum(list1) < minValue || (differenceList.size() == 1 && sum(differenceList) < minValue)){
					continue;
				}
				List<List<List<Product>>> subList = new ArrayList<>();
				// differenceList最多只有一个时，直接添加
				if (differenceList.size() == 0 || differenceList.size() == 1) {
					List<List<Product>> tmpList = new ArrayList<>();
					// [[1,2,3,4]]
					tmpList.add(list1);
					// [[1,2,3],[4]]
					if (differenceList.size() == 1) tmpList.add(differenceList);
					if (!isExist(resultList, tmpList)) subList.add(tmpList);
				} else {
					subList = getCombinations(minValue, maxChildCount, differenceList);
					for (int j = 0; j < subList.size(); ) {
						List<List<Product>> list3 = subList.get(j);
						list3.add(0, list1);
						if (isExist(resultList, list3)) {
							subList.remove(j);
						} else {
							j++;
						}
					}
				}
				resultList.addAll(subList);
			}

		}

		return resultList;
	}


	/**
	 * 获取list的差集，但不重复删除
	 * @param list1 被减数组
	 * @param list2 减数组
	 * @return
	 */
	public static List<Product> getDifferenceList(List<Product> list1,List<Product> list2){
		List<Product> tmpList1 = new ArrayList<>(list1);
		List<Product> tmpList2 = new ArrayList<>(list2);
		for (int i = 0; i < tmpList1.size() && tmpList2.size() > 0; i++) {
			for (int j = 0; j < tmpList2.size(); j++) {
				if (tmpList1.get(i).equals(tmpList2.get(j))) {
					tmpList1.remove(i--);
					tmpList2.remove(j);
					break;
				}
			}
		}
		return tmpList1;
	}



	/**
	 * 获取集合(去除空集和重复集)
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<Product>> getCombinationWithoutNullAndRemoveDuplicate(
			List<Product> list) {
		List<List<Product>> resultList = getCombinationRemoveDuplicate(list);
		resultList.remove(0);
		return resultList;
	}


	/**
	 * 获取集合(去除重复集)
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<Product>> getCombinationRemoveDuplicate(List<Product> list) {
		List<List<Product>> resultList = getCombination(list);
		for (int i = 0; i < resultList.size() - 1; i++) {
			for (int j = resultList.size() - 1; j > i; j--) {
				if (resultList.get(j).equals(resultList.get(i))) {
					resultList.remove(j);
				}
			}
		}
		return resultList;
	}

	/**
	 * 获取集合(去除空集)
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<Product>> getCombinationsWithoutNull(List<Product> list) {
		List<List<Product>> resultList = getCombination(list);
		resultList.remove(0);
		return resultList;
	}

	/**
	 * 获取集合
	 * @param list 原始数组
	 * @return 包含所有组合数组的数组
	 */
	public static List<List<Product>> getCombination(List<Product> list) {
		List<List<Product>> result = new ArrayList<>();
		long n = (long) Math.pow(2, list.size());
		List<Product> combine;
		for (long l = 0L; l < n; l++) {
			combine = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				if ((l >>> i & 1) == 1)
					combine.add(list.get(i));
			}
			result.add(combine);
		}
		return result;
	}
	/**
	 * 把数值转化为字符串
	 * @param list
	 * @return
	 */
	public static String toString(List<Product> list){
		String string = "[";
		for (Product product : list) {
			// 12.0&1 ; 价格&包装费
			string = string + product.getPrice() +"&"+product.getPackingFee()+ ",";
		}
		if (list.size() != 0) string = string.substring(0,string.length()-1);
		string = string +"]";
		return  string;
	}


	/**
	 * 把数值转化为字符串
	 * @param list
	 * @return
	 */
	public static String toString2(List<List<Product>> list){
		String string ="";
		if (isDebug) {
			for (int i = 0; i < list.size(); i++) {
				string = string + MathUtil.toString(list.get(i))+ " // ";
			}
		}
//		if (list.size() != 0) string = string.substring(0,string.length()-4);
		return  string;
	}
	/**
	 * 把数值转化为字符串
	 * @param list
	 * @return
	 */
	public static String toString3(List<List<List<Product>>> list){
		String string ="{";
		for (int i = 0; i < list.size(); i++) {
			string = string + MathUtil.toString2(list.get(i));
		}
		string = string + "}";
		return  string;
	}

//	/***
//	 * 是否已经存在
//	 * @param list		父List
//	 * @param subList	想添加到父List的子List
//	 * @return
//	 */
//	private static boolean isExist(List<List<List<Float>>> list,List<List<Float>> subList){
//		for (int i = 0; i < list.size(); i++) {
//			List<List<Float>> tmpList = list.get(i);
//			boolean isExist = true;
//			for (int j = 0; j < subList.size(); j++) {
//				if (tmpList.indexOf(subList.get(j)) == -1) {
//					isExist = false;
//					break;
//				}
//			}
//			if (isExist) return true;
//
//		}
//		return false;
//	}
//
//
//	/**
//	 * 获取组合集合
//	 * @param maxChildCount 子集最大个数
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<List<Float>>> getCombinations(int maxChildCount,List<Float> list) {
//		return getCombinations(0,maxChildCount, list);
//	}
//
//	/**
//	 * 获取组合集合
//	 * @param minValue 组合的和必须大于minValue
//	 * @param maxChildCount 子集最大个数
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<List<Float>>> getCombinations(float minValue, int maxChildCount,List<Float> list) {
//		if (isDebug) System.out.println("handleList = " + list);
//		List<List<List<Float>>> resultList = new ArrayList<>();
//		if (list.size() == 2) {
//			List<List<Float>> tmpList = new ArrayList<>();
//			List<Float> subList = new ArrayList<>();
//			// [[1],[2]]
//			if (list.get(0) >= minValue && list.get(1) >= minValue) {
//				subList.add(list.get(0));
//				tmpList.add(subList);
//				subList = new ArrayList<>();
//				subList.add(list.get(1));
//				tmpList.add(subList);
//				resultList.add(tmpList);
//			}
//
//			// [[1,2]]
//			if ((list.get(0) + list.get(1)) >= minValue) {
//				subList = new ArrayList<>();
//				subList.add(list.get(0));
//				subList.add(list.get(1));
//				tmpList = new ArrayList<>();
//				tmpList.add(subList);
//				resultList.add(tmpList);
//			}
//
//			if (isDebug) System.out.println("return : " + resultList);
//			return resultList;
//		} else {
//			List<List<Float>> groups = getCombinationWithoutNullAndRemoveDuplicate(list);
//			for (int i = 0; i < groups.size(); i++) {
//				List<Float> list1 = groups.get(i); // 正常List
//				List<Float> differenceList = getDifferenceList(list, list1); // list-list1
//				if (isDebug) System.out.println("list = " + list1.toString() + "  DifferenceList = " + differenceList.toString());
//
//				// 组合的和大于最小值
//				if (sum(list1) < minValue || (differenceList.size() == 1 && sum(differenceList) < minValue)){
//					if (isDebug) System.out.println(" < "+minValue+" continue");
//					continue;
//				}
//				List<List<List<Float>>> subList = new ArrayList<>();
//				String key = list1.toString() +differenceList.toString();
//				// 获取缓存
//				key = list1.toString() + differenceList.toString();
//				List<List<List<Float>>> value = mMap.get(key);
//				if (value != null){
//					List<List<List<Float>>> newList = deepCopy(value);
//					if (newList != null) {
//						subList.addAll(newList);
//						if (isDebug) System.out.println("get(key)==>  key =" + key + " && value = " + newList.toString());
//					}
//				}else {
//					// differenceList最多只有一个时，直接添加
//					if (differenceList.size() == 0 || differenceList.size() == 1) {
//						List<List<Float>> tmpList = new ArrayList<>();
//						// [[1,2,3,4]]
//						tmpList.add(list1);
//						// [[1,2,3],[4]]
//						if (differenceList.size() == 1) tmpList.add(differenceList);
//						if (!isExist(resultList, tmpList)) subList.add(tmpList);
//					} else {
//
//						subList = getCombinations(minValue, maxChildCount, differenceList);
//						for (int j = 0; j < subList.size(); ) {
//							List<List<Float>> list3 = subList.get(j);
//							list3.add(0, list1);
//							if (isExist(resultList, list3)) {
//								subList.remove(j);
//							} else {
//								j++;
//							}
//						}
//						if (subList.size() > 0) {
//							List<List<List<Float>>> newList = deepCopy(subList);
//							if (newList != null) {
//								mMap.put(key, newList);
//								if (isDebug) System.out.println("put(key)==>  key =" + key + " && value = " + newList.toString());
//							}
//						}
//					}
//				}
//
//				if (isDebug) System.out.println("addList = " + subList);
//				resultList.addAll(subList);
//				if (isDebug) System.out.println("resultList = " + resultList);
//			}
//
//		}
//
//		return resultList;
//	}
//
//
//	/**
//	 * 获取list的差集，但不重复删除
//	 * @param list1 被减数组
//	 * @param list2 减数组
//	 * @return
//	 */
//	public static List<Float> getDifferenceList(List<Float> list1,List<Float> list2){
//		List<Float> tmpList1 = new ArrayList<>(list1);
//		List<Float> tmpList2 = new ArrayList<>(list2);
//		for (int i = 0; i < tmpList1.size() && tmpList2.size() > 0; i++) {
//			float num1 = tmpList1.get(i);
//			for (int j = 0; j < tmpList2.size(); j++) {
//				float num2 = tmpList2.get(j);
//				if (num1 == num2) {
//					tmpList1.remove(i--);
//					tmpList2.remove(j);
//					break;
//				}
//			}
//		}
////		System.out.println("subGroupList = "+tmpList1);
////		System.out.println("differenceGroupList = "+tmpList2);
//		return tmpList1;
//	}
//
//
//
//	/**
//	 * 获取集合(去除空集和重复集)
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<Float>> getCombinationWithoutNullAndRemoveDuplicate(
//			List<Float> list) {
//		List<List<Float>> resultList = getCombinationRemoveDuplicate(list);
//		resultList.remove(0);
//		return resultList;
//	}
//
//
//	/**
//	 * 获取集合(去除重复集)
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<Float>> getCombinationRemoveDuplicate(
//			List<Float> list) {
//		List<List<Float>> resultList = getCombination(list);
//		for (int i = 0; i < resultList.size() - 1; i++) {
//			for (int j = resultList.size() - 1; j > i; j--) {
//				if (resultList.get(j).equals(resultList.get(i))) {
//					resultList.remove(j);
//				}
//			}
//		}
//		return resultList;
//	}
//
//	/**
//	 * 获取集合(去除空集)
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<Float>> getCombinationsWithoutNull(List<Float> list) {
//		List<List<Float>> resultList = getCombination(list);
//		resultList.remove(0);
//		return resultList;
//	}
//
//	/**
//	 * 获取集合
//	 * @param list 原始数组
//	 * @return 包含所有组合数组的数组
//	 */
//	public static List<List<Float>> getCombination(List<Float> list) {
//		List<List<Float>> result = new ArrayList<>();
//		long n = (long) Math.pow(2, list.size());
//		List<Float> combine;
//		for (long l = 0L; l < n; l++) {
//			combine = new ArrayList<>();
//			for (int i = 0; i < list.size(); i++) {
//				if ((l >>> i & 1) == 1)
//					combine.add(list.get(i));
//			}
//			result.add(combine);
//		}
//		return result;
//	}

	/**
	 * 排列
	 *
	 * @param m
	 * @param n
	 * @return
	 */
	public static int Amn(int m, int n) {
		return getFactorialSum(n) / getFactorialSum(n - m);
	}

	/**
	 * 组合
	 *
	 * @param m
	 * @param n
	 * @return
	 */
	public static int Cmn(int m, int n) {
		return Amn(m, n) / getFactorialSum(m);
	}

	/**
	 * 通过递归实现阶乘
	 *
	 * @param n
	 * @return
	 */
	public static int getFactorialSum(int n) {
		if (n == 1 || n == 0) {
			return 1;
		} else {
			return getFactorialSum(n - 1) * n;
		}
	}


	/**
	 * 求和
	 * @param list
	 * @return
	 */
	public static float sum(List<Product> list){
		float sum = 0;
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).getPrice();
		}
		return sum;
	}


}
