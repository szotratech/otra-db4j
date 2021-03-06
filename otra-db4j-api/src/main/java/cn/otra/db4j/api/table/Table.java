package cn.otra.db4j.api.table;

import java.util.Date;

import cn.otra.db4j.api.element.SqlElement;


public interface Table extends SqlElement {
	
	/**
	 * 获取表名
	 * @return
	 */
	String getTableName();
	
//	/**
//	 * 别名
//	 * @param alias
//	 * @return
//	 */
//	Table as(String alias);
 	
 	/**
 	 * 获取主键属性
 	 * @return
 	 */
	<E> TableField<E> getPK();
	
	/**
	 * 根据属性名找到对应的表字段
	 * @param javaFieldName
	 * @return
	 */
	String getFieldName(String javaFieldName);
 	
	/**
	 * 获取所有字段
	 * @return
	 */
	TableField<?> [] getAllFields();
	
	TableField<?> [] getAllUpdateFields();
	
	boolean isAutoGeneratedPK();
	
	String getAliasName();
	
	long lastUpdateTime();
	
	TableField<?> allField();
	
	TableField<Date> getUpdateTimeField();
	
	TableField<Date> getCreateTimeField();
}
