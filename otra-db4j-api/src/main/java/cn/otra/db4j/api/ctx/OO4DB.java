package cn.otra.db4j.api.ctx;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.po.BasePo;
import cn.otra.db4j.api.sql.Dialect;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;

/**
 * 提供以面向对象的方式操作DB
 * @author satuo20
 *
 */
public interface OO4DB {

	<T extends BasePo> T findByPK(Class<T> entityClass,Object pk);
	
	<T extends BasePo> T findObject(Class<T> entityClass,WhereCondition condition);
	
	<T extends BasePo> List<T> findObjectsForList(Class<T> entityClass,WhereCondition condition);
	
	<T extends BasePo,E> Map<E,T> findObjectsForMap(Class<T> entityClass,WhereCondition condition);
	
	<T extends BasePo> List<T> findAllObjects(Class<T> entityClass);
	
	<T extends BasePo,E> Map<E,T> findObjectsForMap(Class<T> entityClass,TableField<E> field,WhereCondition condition);
	
	<T extends BasePo> ECPage<T> findPage(Class<T> entityClass,Integer page,Integer rowsPerPage);

	<T extends BasePo> ECPage<T> findPage(Class<T> entityClass,WhereCondition condition,Integer page,Integer rowsPerPage);
	
	/////////////////////find by sql start////////////////////
	
	Map<String, Object> findOneRecord(String sql,Object ...params);
	
	List<Map<String, Object>> findRecords(String sql,Object ...params);
	
	<T extends BasePo,E> Map<E, T> findObjectsForMapBySql(Class<T> entityClass,TableField<E> field,String sql,Object ...params);
	
	<T extends Serializable> List<T> findObjectsForListBySql(Class<T> entityClass,String sql,Object ...params);
	
	<T extends Serializable> ECPage<T> findPageBySql(Class<T> entityClass,String sql,Integer page,Integer rowsPerPage,Object ...params);
	
	/////////////////////find by sql end////////////////////
	
	
	<T extends BasePo> void save(T t);
	
	<T extends BasePo> int update(T t);
	
	<T extends BasePo> int delete(T t);
	
	int delete(Table table,WhereCondition condition);
	
	<T extends BasePo> int deleteByPK(Class<T> entityClass,Object pk);
	
	<T extends BasePo> int delete(Class<T> entityClass,WhereCondition condition);
	
	Dialect getDialect();
}
