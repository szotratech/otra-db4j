package cn.otra.db4j.core.ctx;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.annotation.Entity;
import cn.otra.db4j.api.builder.FromBuilder;
import cn.otra.db4j.api.builder.OrderByBuilder;
import cn.otra.db4j.api.builder.SetBuilder;
import cn.otra.db4j.api.builder.SortTypeBuilder;
import cn.otra.db4j.api.builder.TableBuilder;
import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.ctx.DB;
import cn.otra.db4j.api.ctx.OO4DB;
import cn.otra.db4j.api.params.DbParams;
import cn.otra.db4j.api.params.ParamKeyword;
import cn.otra.db4j.api.po.BasePo;
import cn.otra.db4j.api.sql.Dialect;
import cn.otra.db4j.api.table.SelectField;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.table.Tables;
import cn.otra.db4j.api.transaction.EcTransaction;
import cn.otra.db4j.api.util.TableUtils;
import cn.otra.db4j.core.transaction.EcTransactionImpl;

public class DBImpl implements DB {
	transient private JdbcTemplate jdbcTemplate;
	private Dialect dialect = Dialect.MySql;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public DataSource getDataSource() {
		return jdbcTemplate.getDataSource();
	}
	
	@Override
	public EcTransaction getTransaction() {
		return new EcTransactionImpl(this);
	}

	/**
	 * TODO 后续会对OO4DB使用缓存
	 * 
	 * @return
	 */
	private final BuilderContext ctx() {
		return new BuilderContextImpl(jdbcTemplate, dialect);
	}

	public final OO4DB oo4db() {
		return new OO4DBImpl(jdbcTemplate, dialect);
	}

	public final FromBuilder select(SelectField<?>... fields) {
		return ctx().select(fields);
	}

	public final FromBuilder selectDistinct(SelectField<?>... fields) {
		return ctx().selectDistinct(fields);
	}

	public final TableBuilder selectDistinctFrom(Table table) {
		return ctx().selectDistinctFrom(table);
	}

	public final TableBuilder selectFrom(Table table) {
		return ctx().selectFrom(table);
	}

	public final <T extends BasePo> T findByPK(Class<T> entityClass,
			Object pk) {
		return oo4db().findByPK(entityClass, pk);
	}

	public final <T extends BasePo> T findObject(Class<T> entityClass,
			WhereCondition condition) {
		return oo4db().findObject(entityClass, condition);
	}

	//
	// public final <T extends Serializable> T findObject(Class<T>
	// entityClass,Condition condition) {
	// return oo4db().findObject(entityClass, this.where(condition));
	// }

	// public final <T extends Serializable> List<T> findObjectsForList(Class<T>
	// entityClass,Condition condition) {
	// return oo4db().findObjectsForList(entityClass, this.where(condition));
	// }

	@Override
	public <T extends Serializable> ECPage<T> findPageBySql(
			Class<T> entityClass, String sql, Integer page,
			Integer rowsPerPage, Object... params) {
		return oo4db().findPageBySql(entityClass, sql, page, rowsPerPage, params);
	}
	
	public final <T extends BasePo> List<T> findObjectsForList(
			Class<T> entityClass, WhereCondition condition) {
		return oo4db().findObjectsForList(entityClass, condition);
	}

	public final <T extends BasePo, E> Map<E, T> findObjectsForMap(
			Class<T> entityClass, WhereCondition condition) {
		return oo4db().findObjectsForMap(entityClass, condition);
	}

	public final <T extends BasePo> List<T> findAllObjects(
			Class<T> entityClass) {
		return oo4db().findAllObjects(entityClass);
	}

	public final <T extends BasePo, E> Map<E, T> findObjectsForMap(Class<T> entityClass, TableField<E> field, WhereCondition condition) {
		return oo4db().findObjectsForMap(entityClass, field, condition);
	}

	public final <T extends BasePo> ECPage<T> findPage(
			Class<T> entityClass, Integer page, Integer rowsPerPage) {
		return oo4db().findPage(entityClass, page, rowsPerPage);
	}

	@SuppressWarnings("rawtypes")
	public final <T extends BasePo> ECPage<T> findPage(
			Class<T> entityClass, DbParams params, Integer page, Integer rowsPerPage) {
		Condition condition = Condition.getInstance();
		Entity entity = entityClass.getAnnotation(Entity.class);
		Table table = Tables.get(entity.table());

		// eq
		processCondition(params.getEqMap(), table.getAllFields(), condition,
				ParamKeyword.eq);
		processCondition(params.getNeMap(), table.getAllFields(), condition,
				ParamKeyword.ne);

		processCondition(params.getGeMap(), table.getAllFields(), condition,
				ParamKeyword.ge);
		processCondition(params.getGtMap(), table.getAllFields(), condition,
				ParamKeyword.gt);

		processCondition(params.getLeMap(), table.getAllFields(), condition,
				ParamKeyword.le);
		processCondition(params.getLtMap(), table.getAllFields(), condition,
				ParamKeyword.lt);

		processCondition(params.getLikeMap(), table.getAllFields(), condition,
				ParamKeyword.like);
		processCondition(params.getLikeStartWithMap(), table.getAllFields(),
				condition, ParamKeyword.likeStartWith);
		processCondition(params.getLikeEndWithMap(), table.getAllFields(),
				condition, ParamKeyword.likeEndWith);
		processCondition(params.getContainMap(), table.getAllFields(),
				condition, ParamKeyword.contains);

		processCondition(params.getNotNullMap(), table.getAllFields(),
				condition, ParamKeyword.isNotNull);
		processCondition(params.getNullMap(), table.getAllFields(), condition,
				ParamKeyword.isNull);
		processCondition(params.getOrderByMap(), table.getAllFields(),
				condition, ParamKeyword.orderBy);

		OrderByBuilder whereCondition = this.where(condition);
		Map<String, Object> orderByMap = params.getOrderByMap();
		if (orderByMap != null) {
			for (Map.Entry<String, Object> en : orderByMap.entrySet()) {
				String key = en.getKey();
				Object value = en.getValue();
				for (TableField f : table.getAllFields()) {
					if (f.getJavaName().equals(key)) {
						if (value.equals("ASC")) {
							whereCondition.orderBy(f).asc();
						} else {
							whereCondition.orderBy(f).desc();
						}
					}
				}
			}
		}

		return this.findPage(entityClass, whereCondition, page, rowsPerPage);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "incomplete-switch" })
	private void processCondition(Map<String, Object> map, TableField<?>[] tfs,
			Condition condition, ParamKeyword keyword) {
		if (map == null) {
			return;
		}
		for (Map.Entry<String, Object> en : map.entrySet()) {
			String key = en.getKey();
			Object value = en.getValue();
			for (TableField f : tfs) {
				if (f.getJavaName().equals(key)) {
					switch (keyword) {
					case eq:
						condition.and(f.eq(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case ne:
						condition.and(f.ne(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case gt:
						condition.and(f.gt(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case ge:
						condition.and(f.ge(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case lt:
						condition.and(f.lt(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case le:
						condition.and(f.le(TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case isNull:
						condition.and(f.isNull());
						break;
					case isNotNull:
						condition.and(f.isNotNull());
						break;
					case like:
						condition.and(f.like((String) TableUtils.getRealValue(
								f.getJavaType(), value)));
						break;
					case likeStartWith:
						condition.and(f.likeStartWith((String) TableUtils
								.getRealValue(f.getJavaType(), value)));
						break;
					case likeEndWith:
						condition.and(f.likeEndWith((String) TableUtils
								.getRealValue(f.getJavaType(), value)));
						break;
					case contains:
						condition.and(f.likeContain((String) TableUtils
								.getRealValue(f.getJavaType(), value)));
						break;
					}

				}
			}
		}
	}

	public final <T extends BasePo> ECPage<T> findPage(
			Class<T> entityClass, WhereCondition condition, Integer page,
			Integer rowsPerPage) {
		return oo4db().findPage(entityClass, condition, page, rowsPerPage);
	}

//	public final <T extends BasePo> PageIterator<T> pageIterator(
//			Class<T> entityClass, Integer rowsPerPage) {
//		return oo4db().pageIterator(entityClass, null, rowsPerPage);
//	}
//
//	public final <T extends BasePo> PageIterator<T> pageIterator(
//			Class<T> entityClass, WhereCondition condition, Integer rowsPerPage) {
//		return oo4db().pageIterator(entityClass, condition, rowsPerPage);
//	}

	// ///////////////////find by sql start////////////////////

	public final Map<String, Object> findOneRecord(String sql, Object... params) {
		return oo4db().findOneRecord(sql, params);
	}

	public final List<Map<String, Object>> findRecords(String sql,
			Object... params) {
		return oo4db().findRecords(sql, params);
	}

	public final <T extends BasePo, E> Map<E, T> findObjectsForMap(
			Class<T> entityClass, TableField<E> field, String sql,
			Object... params) {
		return oo4db().findObjectsForMapBySql(entityClass, field, sql, params);
	}

	@Override
	public <T extends Serializable> List<T> findObjectsForList(Class<T> entityClass, String sql, Object... params) {
		return oo4db().findObjectsForListBySql(entityClass, sql, params);
	}
//	public final <T extends BasePo> List<T> findObjectsForList(
//			Class<T> entityClass, String sql, Object... params) {
//		return oo4db().findObjectsForListBySql(entityClass, sql, params);
//	}

	// ///////////////////find by sql start////////////////////

	public final <T extends BasePo> void save(T t) {
		oo4db().save(t);
	}

	public final <T extends BasePo> int update(T t) {
		return oo4db().update(t);
	}

	public final <T extends BasePo> int delete(T t) {
		return oo4db().delete(t);
	}

	public final <T extends BasePo> int deleteByPK(Class<T> entityClass,
			Object pk) {
		return oo4db().deleteByPK(entityClass, pk);
	}

	/**
	 * 删除整张表的数据
	 * @param table
	 * @return
	 */
	public final int delete(Table table) {
		return oo4db().delete(table, null);
	}
	
	public final int delete(Table table, WhereCondition condition) {
		return oo4db().delete(table, condition);
	}

	public final <T extends BasePo> int delete(Class<T> entityClass,
			WhereCondition condition) {
		return oo4db().delete(entityClass, condition);
	}

	public final OrderByBuilder where(Condition condition) {
		// OO4DBImpl ctx = (OO4DBImpl)ctx();
		return ctx().add(condition);
	}

	public final SortTypeBuilder orderBy(TableField<?> field) {
		return ctx().orderBy(field);
	}

	public final SetBuilder updateTable(Table table) {
		return ctx().updateWithCondition(table);
	}

	public final SetBuilder updateTable(Table... tables) {
		return ctx().updateWithCondition(tables);
	}

	public final int update(String sql) {
		return jdbcTemplate.update(sql);
	}

	@Override
	public <T extends BasePo> T findObject(Class<T> entityClass,
			Condition condition) {
		return oo4db().findObject(entityClass, this.where(condition));
	}

	@Override
	public <T extends BasePo> List<T> findObjectsForList(
			Class<T> entityClass, Condition condition) {
		return oo4db().findObjectsForList(entityClass, this.where(condition));
	}

	

}
