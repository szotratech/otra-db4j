package cn.otra.db4j.api.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.AliasField;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;

public class DBContext {
	private static final Logger logger = Logger.getLogger(DBContext.class);
	// 静态方法
	private static final <T> AliasField<T> getAliasField(TableField<T> field) {
		return new AliasField<T>(field, field.getTable().getAliasName());
	}

	/**
	 * 平均值
	 * @param field
	 * @return
	 */
	public static final <T> AliasField<T> avg(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.avg);
		return af;
	}
	
	/**
	 * use selectDistinct or selectDistinctFrom instand.
	 * @param field
	 * @return
	 */
//	@Deprecated
	public static final <T> AliasField<T> distinct(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.distinct);
		return af;
	}

	public static final <T> AliasField<T> count(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.count);
		return af;
	}
	
	public static final <T> AliasField<T> min(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.min);
		return af;
	}
	
	public static final <T> AliasField<T> max(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.max);
		return af;
	}
	
	public static final <T> AliasField<T> countAndDistinct(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.countAndDistinct);
		return af;
	}
	
	public static final <T> AliasField<T> sum(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.sum);
		return af;
	}
	
	public static final <T> AliasField<T> sumAndDistinct(TableField<T> field) {
		AliasField<T> af = getAliasField(field);
		af.setFunction(Keyword.sumAndDistinct);
		return af;
	}
	
	private static final Map<Class<?>, Table> TABLE_MAP = new HashMap<Class<?>, Table>();
	
//	public static final Table getTable(Class<?> clazz) {
//		Table table =  TABLE_MAP.get(clazz);
//		if(table == null) {
//			throw new RuntimeException(clazz.getName()+" not found in table_map.");
//		}
//		return table;
//	}
	
	/**
	 * 多个【自定义类】可以对应一张【数据库表】，即：<br/>
	 *【自定义类】(n)----(1)【数据库表】      <br/>
	 * 两者是多对一的关系<br/>
	 * @param entityClass
	 * @param table
	 */
	public static final <T> void mapping(Class<T> entityClass,Table table) {
		if(logger.isDebugEnabled()) {
			logger.info("mapping:key=["+entityClass.getName()+"<==>"+table.getTableName()+"]");
		}
		
		TABLE_MAP.put(entityClass, table);
	}
	
}
