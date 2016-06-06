package cn.otra.db4j.api.ctx;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.builder.FromBuilder;
import cn.otra.db4j.api.builder.OrderByBuilder;
import cn.otra.db4j.api.builder.SetBuilder;
import cn.otra.db4j.api.builder.SortTypeBuilder;
import cn.otra.db4j.api.builder.TableBuilder;
import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.params.DbParams;
import cn.otra.db4j.api.po.BasePo;
import cn.otra.db4j.api.table.SelectField;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.transaction.EcTransaction;

public interface DB {
	
	OO4DB oo4db();
	
	DataSource getDataSource();
	
	EcTransaction getTransaction();

	FromBuilder select(SelectField<?>... fields);

	FromBuilder selectDistinct(SelectField<?>... fields);

	TableBuilder selectDistinctFrom(Table table);

	TableBuilder selectFrom(Table table);

	<T extends BasePo> T findByPK(Class<T> entityClass, Object pk);

	<T extends BasePo> T findObject(Class<T> entityClass, WhereCondition condition);

	<T extends BasePo> T findObject(Class<T> entityClass, Condition condition);

	<T extends BasePo> List<T> findObjectsForList(Class<T> entityClass, Condition condition);

	<T extends BasePo> List<T> findObjectsForList(Class<T> entityClass, WhereCondition condition);

	<T extends BasePo, E> Map<E, T> findObjectsForMap(Class<T> entityClass, WhereCondition condition);

	<T extends BasePo> List<T> findAllObjects(Class<T> entityClass);

	<T extends BasePo, E> Map<E, T> findObjectsForMap(Class<T> entityClass, TableField<E> field, WhereCondition condition);

	<T extends BasePo> ECPage<T> findPage(Class<T> entityClass, Integer page, Integer rowsPerPage);

	<T extends BasePo> ECPage<T> findPage(Class<T> entityClass,DbParams params, Integer page, Integer rowsPerPage);

	<T extends BasePo> ECPage<T> findPage(Class<T> entityClass,WhereCondition condition, Integer page, Integer rowsPerPage);

	// ///////////////////find by sql start////////////////////

	Map<String, Object> findOneRecord(String sql, Object... params);

	List<Map<String, Object>> findRecords(String sql, Object... params);

	<T extends BasePo, E> Map<E, T> findObjectsForMap(Class<T> entityClass, TableField<E> field, String sql,Object... params);
	
	<T extends Serializable> List<T> findObjectsForList(Class<T> entityClass,String sql, Object... params);

	// ///////////////////find by sql start////////////////////
	
	// 增加分页功能
	<T extends Serializable> ECPage<T> findPageBySql(Class<T> entityClass,String sql,Integer page, Integer rowsPerPage, Object... params);

	<T extends BasePo> void save(T t);

	<T extends BasePo> int update(T t);

	<T extends BasePo> int delete(T t);

	<T extends BasePo> int deleteByPK(Class<T> entityClass, Object pk);

	int delete(Table table, WhereCondition condition);

	<T extends BasePo> int delete(Class<T> entityClass,WhereCondition condition);

	OrderByBuilder where(Condition condition);

	SortTypeBuilder orderBy(TableField<?> field);

	SetBuilder updateTable(Table table);

	SetBuilder updateTable(Table... tables);

	int update(String sql);

}
