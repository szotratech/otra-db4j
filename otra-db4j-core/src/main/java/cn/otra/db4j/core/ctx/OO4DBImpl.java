package cn.otra.db4j.core.ctx;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.ctx.OO4DB;
import cn.otra.db4j.api.po.BasePo;
import cn.otra.db4j.api.sql.Dialect;
import cn.otra.db4j.api.sql.SqlFactory;
import cn.otra.db4j.api.sql.SqlFactory.Method;
import cn.otra.db4j.api.sql.SqlFactoryManager;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.table.Tables;
import cn.otra.db4j.api.util.TableUtils;
import cn.otra.db4j.core.utils.RowMapperUtils;

public class OO4DBImpl implements OO4DB {

	private static final Logger logger = Logger.getLogger(OO4DBImpl.class);

	protected JdbcTemplate jdbcTemplate;
	protected Dialect dialect;
	
	public OO4DBImpl(JdbcTemplate jdbcTemplate, Dialect dialect) {
		this.jdbcTemplate = jdbcTemplate;
		this.dialect = dialect;
	}

	@Override
	public <T extends BasePo> int delete(Class<T> entityClass, WhereCondition condition) {
		final Table table = getTable(entityClass);
		return delete(table, condition);

	}

	@Override
	public <T extends BasePo> int deleteByPK(Class<T> entityClass,Object pk) {
		final Table table = getTable(entityClass);
		String sql = SqlFactoryManager.getSql(dialect, table, Method.deleteByPK,null);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return jdbcTemplate.update(sql,pk);
	}
	
	@Override
	public <T extends BasePo> int delete(T t) {
		final Table table = getTable(t);
		String sql = SqlFactoryManager.getSql(dialect, table, Method.deleteByPK,null);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		//查找主键
		Field [] fs = TableUtils.getFields(t.getClass(), true);

		boolean found = false;
		for(Field f:fs) {
			if(f.getName().equals(table.getPK().getJavaName())) {
				found = true;
				try {
					return jdbcTemplate.update(sql,f.get(t));
				} catch (Exception e) {
					logger.error("",e);
					throw new RuntimeException("",e);
				}
			}
		}
		if(!found) {
			throw new RuntimeException("can not found pk in Class "+t.getClass().getName());
		}
		return 0;
	}

	// ========================================================================

	@Override
	public int delete(Table table, WhereCondition condition) {
		String sql = SqlFactoryManager.getSql(dialect, table, Method.deleteByCondition,condition);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		if(condition == null) {
			return jdbcTemplate.update(sql);
		}
		return jdbcTemplate.update(sql,condition.getParams());
	}

	@Override
	public <T extends BasePo> List<T> findAllObjects(Class<T> entityClass) {
		final Table table = getTable(entityClass);
		String sql = SqlFactoryManager.getSql(dialect, table, Method.findAllObjects, null);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		List<T> list = jdbcTemplate.query(sql, rowMapper);
		return list;
	}

	@Override
	public <T extends BasePo> T findByPK(final Class<T> entityClass, Object pk) {
		if(pk == null) {
			return null;
		}
		final Table table = getTable(entityClass);
		try {
			String sql = SqlFactoryManager.getSql(dialect, table, Method.findByPK,null);
			if(logger.isDebugEnabled()) {
				logger.debug(sql);
			}
			RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
			T t = jdbcTemplate.queryForObject(sql,rowMapper , pk);
			return t;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public <T extends BasePo> T findObject(Class<T> entityClass, WhereCondition condition) {
		final Table table = getTable(entityClass);
		String sql = SqlFactoryManager.getSql(dialect, table, Method.findObject, condition);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		T t;
		try {
			t = jdbcTemplate.queryForObject(sql,condition.getParams(), rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return t;
	}

	@Override
	public <T extends BasePo> List<T> findObjectsForList(Class<T> entityClass, WhereCondition condition) {
		if(condition == null) {
			return findAllObjects(entityClass);
		}
		final Table table = getTable(entityClass);
		String sql = SqlFactoryManager.getSql(dialect, table, Method.findObjectsForList, condition);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		List<T> list = jdbcTemplate.query(sql,condition.getParams(), rowMapper);
		return list;
	}

	@Override
	public <T extends Serializable> List<T> findObjectsForListBySql(Class<T> entityClass, String sql, Object... params) {
		final Table table = getTable(entityClass);
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		List<T> list = jdbcTemplate.query(sql, rowMapper,params);
		return list;
	}
	@SuppressWarnings("rawtypes")
	private static final List emptyList = new ArrayList();
	@Override
	public <T extends BasePo> ECPage<T> findPage(Class<T> entityClass, WhereCondition condition, Integer page, Integer rowsPerPage) {
		if(page == null) {
			page = 1;
		}
		if(rowsPerPage == null) {
			rowsPerPage = ECPage.DEFAULT_SIZE;
		}
		if(condition == null) {
			return findPage(entityClass, page, rowsPerPage);
		}
		final Table table = getTable(entityClass);
		String countSql = SqlFactoryManager.getSql(dialect, table, Method.findPageCount, condition);
		if(logger.isDebugEnabled()) {
			logger.debug(countSql);
		}
		Integer totalRows = jdbcTemplate.queryForObject(countSql,condition.getParams(), Integer.class);
		if(totalRows <= 0) {
			@SuppressWarnings("unchecked")
            ECPage<T> pageEntity = ECPage.initPage(emptyList, totalRows, rowsPerPage, page);
            return pageEntity;
		} else {
			String dataSql = SqlFactoryManager.getPageDataSql(dialect, table, Method.findPageData, condition,(page-1)*rowsPerPage,rowsPerPage);
			if(logger.isDebugEnabled()) {
				logger.debug(dataSql);
			}
			RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
			Object[] params = condition.getParams();
			List<T> list = jdbcTemplate.query(dataSql,params, rowMapper);
			ECPage<T> pageEntity = ECPage.initPage(list, totalRows, rowsPerPage, page);
			return pageEntity;
		}
	}
	
	@Override
	public <T extends BasePo> ECPage<T> findPage(Class<T> entityClass, Integer page, Integer rowsPerPage) {
		if(page == null) {
			page = 1;
		}
		if(rowsPerPage == null) {
			rowsPerPage = ECPage.DEFAULT_SIZE;
		}
		final Table table = getTable(entityClass);
		String countSql = SqlFactoryManager.getSql(dialect, table, Method.findPageCount, null);
		
		if(logger.isDebugEnabled()) {
			logger.debug(countSql);
		}
		Integer totalRows = jdbcTemplate.queryForObject(countSql, Integer.class);
		if(totalRows <= 0) {
			@SuppressWarnings("unchecked")
			ECPage<T> pageEntity = ECPage.initPage(emptyList, totalRows, rowsPerPage, page);
			return pageEntity;
		} else {
			String dataSql = SqlFactoryManager.getPageDataSql(dialect, table, Method.findPageData, null,(page-1)*rowsPerPage,rowsPerPage);
			if(logger.isDebugEnabled()) {
				logger.debug(dataSql);
			}
			RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
			List<T> datas = jdbcTemplate.query(dataSql, rowMapper);
			ECPage<T> pageEntity = ECPage.initPage(datas, totalRows, rowsPerPage, page);
			return pageEntity;
		}
	}
	
	@Override
	public <T extends Serializable> ECPage<T> findPageBySql(
			Class<T> entityClass, String sql, Integer page,
			Integer rowsPerPage, Object... params) {
		if(page == null) {
			page = 1;
		}
		if(rowsPerPage == null) {
			rowsPerPage = ECPage.DEFAULT_SIZE;
		}
		String countSql = "select count(*) from ("+sql+") alias"+System.currentTimeMillis();
		if(logger.isDebugEnabled()) {
			logger.debug(countSql);
		}
		Integer totalRows = jdbcTemplate.queryForObject(countSql, params,Integer.class);//.queryForInt(countSql,countParams);
        if(totalRows <= 0) {//优化，减少查询
            @SuppressWarnings("unchecked")
            ECPage<T> pageEntity = ECPage.initPage(emptyList, totalRows, rowsPerPage, page);
            return pageEntity;
        } else {
        	Table table = null;
            try {
            	 table = Tables.getFromPo(entityClass);
            } catch (Exception e) {
            }
            List<T> datas = jdbcTemplate.query(sql,params, RowMapperUtils.getSimpleRowMapper(entityClass, table));
            ECPage<T> pageEntity = ECPage.initPage(datas, totalRows, rowsPerPage, page);
            return pageEntity;
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasePo,E> Map<E, T> findObjectsForMap(Class<T> entityClass, WhereCondition condition) {
		final Table table = getTable(entityClass);
		return (Map<E, T>)findObjectsForMap(entityClass,table.getPK(), condition);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasePo,E> Map<E, T> findObjectsForMap(Class<T> entityClass, TableField<E> field, WhereCondition condition) {
		final Table table = getTable(entityClass);
		Map<String, Field> fieldMap = TableUtils.getDeclaredFieldMap(entityClass);
		Field keyField = fieldMap.get(field.getJavaName());
		
		if(keyField == null) {
			throw new RuntimeException(" field not found in table["+table.getTableName()+"].");
		}
		
		String sql = SqlFactoryManager.getSql(dialect, table, Method.findObjectsForMap, condition);
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		List<T> list = null;
		if(condition != null) {
			list = jdbcTemplate.query(sql,condition.getParams(), rowMapper);
		} else {
			list = jdbcTemplate.query(sql, rowMapper);
		}
		
		try {
			Map<E, T> map = new HashMap<E, T>();
			keyField.setAccessible(true);
			for (T t : list) {
				map.put((E)keyField.get(t), t);
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BasePo,E> Map<E, T> findObjectsForMapBySql(Class<T> entityClass, TableField<E> field, String sql, Object... params) {
		final Table table = getTable(entityClass);
		Map<String, Field> fieldMap = TableUtils.getDeclaredFieldMap(entityClass);
		Field keyField = fieldMap.get(field.getJavaName());
		
		if(keyField == null) {
			throw new RuntimeException(" field not found in table["+table.getTableName()+"].");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		List<T> list = jdbcTemplate.query(sql, rowMapper,params);
		
		try {
			Map<E, T> map = new HashMap<E, T>();
			keyField.setAccessible(true);
			for (T t : list) {
				map.put((E)keyField.get(t), t);
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
	}

	@Override
	public Map<String, Object> findOneRecord(String sql, Object... params) {
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return jdbcTemplate.queryForMap(sql,params);
	}

	@Override
	public List<Map<String, Object>> findRecords(String sql, Object... params) {
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		return jdbcTemplate.queryForList(sql,params);
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	private <T extends Serializable> Table getTable(Class<T> t) {
		final Table table = Tables.getFromPo(t);
		if(table == null) {
			throw new RuntimeException("table not found with["+t.getClass().getName()+"].");
		}
		return table;
	}

	private <T extends Serializable> Table getTable(T t) {
		return getTable(t.getClass());
	}

	@Override
	public <T extends BasePo> void save(T t) {
			final Table table = Tables.getFromPo(t.getClass());
			final String sql = SqlFactoryManager.getSql(dialect, table, SqlFactory.Method.save,null);
			if(logger.isDebugEnabled()) {
				logger.debug(sql);
			}
			TableField<?> [] columns = table.getAllFields();
			final Object[] params = new Object[columns.length];
			
			Map<String, Field> fieldMap = TableUtils.getDeclaredFieldMap(t.getClass());
			Field keyField = fieldMap.get(table.getPK().getJavaName());
			
			if(keyField == null) {
				throw new RuntimeException(" field not found in table["+table.getTableName()+"].");
			}
			
			for(int i=0,len=columns.length;i<len;i++) {
				TableField<?> f =columns[i];
				Field df = null;
				try {
					df = fieldMap.get(f.getJavaName());
					df.setAccessible(true);
					params[i] = df.get(t);
					if(table.getCreateTimeField() != null && "createTime".equals(f.getJavaName()) && params[i] == null) {
						params[i] = new Date();	
						df.set(t, params[i]);
					}
					if(table.getUpdateTimeField() != null && "updateTime".equals(f.getJavaName()) && params[i] == null) {
						params[i] = new Date();	
						df.set(t, params[i]);
					}
				} catch (Exception e) {
					if(t != null) {
						logger.error(t.toString(),e);
					} else {
						logger.error("",e);
					}
					params[i] = null;
				}
			}
			
			
			if(table.isAutoGeneratedPK()) {
				GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
				jdbcTemplate.update(new PreparedStatementCreator() {
					
					@Override
					public PreparedStatement createPreparedStatement(Connection con)
							throws SQLException {
						PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
						for(int i=1,len = params.length;i<=len;i++) {
							ps.setObject(i, params[i-1]);
						}
						return ps;
					}
				}, generatedKeyHolder);
				try {
					Number number = generatedKeyHolder.getKey();
					if(keyField.getType() == Integer.class) {
						number = number.intValue();
					}
					if(keyField.getType() ==  Long.class) {
						number = number.longValue();
					}
					keyField.set(t, number);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				jdbcTemplate.update(sql, params);
			}
	}

	@Override
	public <T extends BasePo> int update(T t) {

		final Table table = getTable(t);
		
		Map<String, Field> fieldMap = TableUtils.getDeclaredFieldMap(t.getClass());
		Field pkf = fieldMap.get(table.getPK().getJavaName());
		
		if(pkf == null) {
			throw new RuntimeException("pk not found in table "+table.getTableName());
		}
		TableField<?> [] columns = table.getAllUpdateFields();
		Object[] params = new Object[columns.length+1];
		
		
		try {
			for(int i=0,len=columns.length;i<len;i++) {
				TableField<?> f =columns[i];
				
				Field df = null;
				df = fieldMap.get(f.getJavaName());
				if(df == null) {
					throw new RuntimeException("no propertie ["+f.getJavaName()+"] found in "+t.getClass().getName());
				}
				df.setAccessible(true);
				params[i] = df.get(t);
				
				if(table.getUpdateTimeField() != null && "updateTime".equals(f.getJavaName())) {
					params[i] = new Date();
					df.set(t, params[i]);
				}
					
			}
			pkf.setAccessible(true);
			params[columns.length] = pkf.get(t);
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
		
		String sql = SqlFactoryManager.getSql(dialect, table, Method.update,null);
		
		return jdbcTemplate.update(sql,params);
	}

//	@Override
//	public <T extends Serializable> PageIterator<T> pageIterator(Class<T> entityClass, Integer rowsPerPage) {
//		ECPage<T> ecPage = findPage(entityClass, 1, rowsPerPage);
//		PageIterator<T> pageInput = new PageIteratorImpl<T>(entityClass, null, null, this, ecPage,false);
//		return pageInput;
//	}
//
//	@Override
//	public <T extends Serializable> PageIterator<T> pageIterator(Class<T> entityClass, WhereCondition condition, Integer rowsPerPage) {
//		ECPage<T> ecPage = findPage(entityClass,condition, 1, rowsPerPage);
//		PageIterator<T> pageInput = new PageIteratorImpl<T>(entityClass, null, condition, this, ecPage,false);
//		return pageInput;
//	}

}
