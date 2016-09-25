package com.chikong.ordercalculation.utils;

import com.chikong.ordercalculation.Constants;
import com.chikong.ordercalculation.MainApplication;
import com.chikong.ordercalculation.model.FullCut;
import com.chikong.ordercalculation.model.Product;
import com.chikong.ordercalculation.model.RedPacket;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChiKong on 16/05/25.
 * 操作数据库工具类
 */
public class DataBaseHelper {

	public static String DATA_NAME = "data.db";

	/**
	 * 添加商品
	 * @param product
	 */
	public static void addProduct(Product product){
		execAdd(DATA_NAME,product);
		Product dbProduct = getProduct(product.getPrice(),product.getPackingFee());
		if (dbProduct != null) product.setId(dbProduct.getId());
	}
	/**
	 * 添加满减
	 * @param fullCut
	 */
	public static void addFullCut(FullCut fullCut){
		execAdd(DATA_NAME,fullCut);
		FullCut dbFullCut = getFullCut(fullCut.getFull(),fullCut.getCut());
		if (dbFullCut != null) fullCut.setId(dbFullCut.getId());
	}

	/**
	 * 添加商品
	 * @param redPacket
	 */
	public static void addRedPacket(RedPacket redPacket){
		execAdd(DATA_NAME,redPacket);
		RedPacket dbRedPacket = getRedPacket(redPacket.getFull(),redPacket.getCut());
		if (dbRedPacket != null) redPacket.setId(dbRedPacket.getId());
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				execAdd(DATA_NAME,fullCut);
//				FullCut dbFullCut = getFullCut(fullCut.getFull(),fullCut.getCut());
//				if (dbFullCut != null) fullCut.setId(dbFullCut.getId());
//
//			}
//		}).start();
	}


	/**
	 * 获取商品
	 * @param price		价格
	 * @param packing	包装费
     * @return
     */
	public static Product getProduct(float price, float packing){
		Product product = null;
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			product = dbUtils.findFirst(Selector.from(Product.class).where(Product.PRICE,"=",price)
				.and(Product.PACKINGFEE,"=",packing));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return product;
	}
	/**
	 * 获取满减
	 * @param full	满
	 * @param cut	减
	 * @return
	 */
	public static FullCut getFullCut(float full, float cut){
		FullCut fullCut = null;
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			fullCut = dbUtils.findFirst(Selector.from(FullCut.class).where(FullCut.FULL,"=",full)
					.and(FullCut.CUT,"=",cut));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return fullCut;
	}
	/**
	 * 获取红包
	 * @param full	满
	 * @param cut	减
	 * @return
	 */
	public static RedPacket getRedPacket(float full, float cut){
		RedPacket fullCut = null;
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			fullCut = dbUtils.findFirst(Selector.from(RedPacket.class).where(RedPacket.FULL,"=",full)
					.and(RedPacket.CUT,"=",cut));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return fullCut;
	}
	/**
	 * 获取商品列表
	 * @return 商品列表
	 */
	@SuppressWarnings("unchecked")
	public static List<Product> getProductList(){
		List<Product> list = new ArrayList<>();
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			List<Product> tmp = dbUtils.findAll(Product.class);
			if (tmp != null) list.addAll(tmp);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取满减列表
	 * @return 满减列表
	 */
	@SuppressWarnings("unchecked")
	public static List<FullCut> getFullCutList(){
		List<FullCut> list = new ArrayList<>();
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			List<FullCut> tmp = dbUtils.findAll(FullCut.class);
			if (tmp != null) list.addAll(tmp);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取红包列表
	 * @return 红包列表
	 */
	@SuppressWarnings("unchecked")
	public static List<RedPacket> getRedPacketList(){
		List<RedPacket> list = new ArrayList<>();
		DbUtils dbUtils = openDB(DATA_NAME);
		try {
			List<RedPacket> tmp = dbUtils.findAll(RedPacket.class);
			if (tmp != null) list.addAll(tmp);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 删除商品
	 * @param product
     */
	@SuppressWarnings("unchecked")
	public static void delProduct(final Product product){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execDelete(DATA_NAME,product);

			}
		}).start();

	}
	/**
	 * 删除满减
	 * @param fullCut
     */
	@SuppressWarnings("unchecked")
	public static void delFullCut(final FullCut fullCut){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execDelete(DATA_NAME,fullCut);

			}
		}).start();

	}
	/**
	 * 删除红包
	 * @param redPacket
     */
	@SuppressWarnings("unchecked")
	public static void delRedPacket(final RedPacket redPacket){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execDelete(DATA_NAME,redPacket);

			}
		}).start();

	}
	/**
	 * 删除商品列表
	 */
	@SuppressWarnings("unchecked")
	public static void delProductList(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execDeleteAll(DATA_NAME,Product.class);

			}
		}).start();
	}
	/**
	 * 更新商品
	 * @param product
	 */
	public static void updateProduct(final Product product){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execSaveOrUpdate(DATA_NAME,product);
			}
		}).start();
	}
	/**
	 * 更新满减
	 * @param fullCut
	 */
	public static void updateFullCut(final FullCut fullCut){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execSaveOrUpdate(DATA_NAME,fullCut);
			}
		}).start();
	}
	/**
	 * 更新红包
	 * @param redPacket
	 */
	public static void updateRedPacket(final RedPacket redPacket){
		new Thread(new Runnable() {
			@Override
			public void run() {
				execSaveOrUpdate(DATA_NAME,redPacket);
			}
		}).start();
	}

	/**
	 * 添加
	 * @param dbName
	 * @param list
	 */
	public static void execAdd(String dbName,List<?> list){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.saveAll(list);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 添加
	 * @param dbName
	 * @param entity
	 */
	public static void execAdd(String dbName,Object entity){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.save(entity);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 保存，存在则更新，根据ID
	 * @param dbName
	 * @param entity
	 */
	public static void execSaveOrUpdate(String dbName,Object entity){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			dbUtils.saveOrUpdate(entity);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存，存在则更新，根据ID
	 * @param dbName
	 * @param list
	 */
	public static void execSaveOrUpdate(String dbName,List<?> list){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			dbUtils.saveOrUpdateAll(list);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新
	 * @param dbName
	 * @param entity
	 * @param builder
	 * @param updateColumnNames
	 */
	public static void execUpdate(String dbName,Object entity,WhereBuilder
					builder,String... updateColumnNames){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			dbUtils.update(entity, builder, updateColumnNames);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除
	 * @param dbName
	 * @param entity
	 * @param builder
	 */
	public static void execDelete(String dbName,
								  Object entity,
								  WhereBuilder builder){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.delete(entity.getClass(),builder);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除
	 * @param dbName
	 * @param entity
	 */
	public static void execDelete(String dbName,
								  Object entity){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.delete(entity);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除
	 * @param dbName
	 * @param clazz
	 */
	public static void execDelete(String dbName,
								  Class clazz){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.delete(clazz);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除全部
	 * @param dbName
	 * @param clazz
	 */
	public static void execDeleteAll(String dbName,
								  Class clazz){
		DbUtils dbUtils = openDB(dbName);
		try {
			dbUtils.deleteAll(clazz);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除表
	 * @param dbName
	 * @param clazz
	 */
	public static void dropTable(String dbName,Class<?> clazz){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			dbUtils.dropTable(clazz);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 执行非查询语句
	 * @param dbName
	 * @param sql
	 */
	public static void execNonQuery(String dbName,String sql){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			dbUtils.execNonQuery(sql);
		} catch (DbException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * 执行非查询语句
	 * @param dbName
	 * @param clazz
	 */
	public static boolean tableIsExist(String dbName,Class<?> clazz){
		DbUtils dbUtils = openDB(dbName);
		
		try {
			return dbUtils.tableIsExist(clazz);
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public static DbUtils openDB(String dbName){
		DbUtils dbUtils = DbUtils.create(MainApplication.getContext(), dbName,
				Constants.DB_VERSION, new DbUtils.DbUpgradeListener() {

					@Override
					public void onUpgrade(DbUtils db, int oldVersion,
							int newVersion) {
						try {
							db.dropDb();
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				});

		dbUtils.configAllowTransaction(true);
		dbUtils.configDebug(false);
		
		return dbUtils;
	}

	/**
	 *  关闭数据库
	 */
	public static void closeDB(){
		DbUtils dbUtils = openDB(DATA_NAME);
		dbUtils.close();
	}
	
}
