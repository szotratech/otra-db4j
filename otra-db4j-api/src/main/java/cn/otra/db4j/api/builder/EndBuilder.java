package cn.otra.db4j.api.builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.table.TableField;

public interface EndBuilder extends LimitBuilder,WhereCondition,Db4jBuilder {
	
	/**
	 * find one record,return with a map.
	 * @return
	 */
	Map<String, Object> queryOneRecord();
	
	/**
	 * find records
	 * @return
	 */
	List<Map<String, Object>> queryRecords();
	
	/**
	 * find a po
	 * @param entityClass
	 * @return
	 */
	<T extends Serializable> T queryObject(Class<T> entityClass);
	
	<T extends Serializable> List<T> queryObjectsForList(Class<T> entityClass);
	
	<T extends Serializable,E> Map<E,T> queryObjectsForMap(Class<T> entityClass,TableField<E> field);
	
	<T extends Serializable> ECPage<T> queryPage(Class<T> entityClass,Integer page,Integer rowsPerPage);
	
//	<T extends Serializable> PageIterator<T> pageIterator(Class<T> entityClass,Integer rowsPerPage);
	
	int executeUpdate();
	
	void show();
	
}
