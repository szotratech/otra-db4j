package cn.otra.db4j.api.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;

public class MySqlFactory implements SqlFactory {
	private static final Logger logger = Logger.getLogger(MySqlFactory.class);
	private static final Map<Table,Map<Method, String>> sqlMap = new ConcurrentHashMap<Table, Map<Method,String>>();
	
	@Override
	public String getDeleteByCondition(Table table,WhereCondition condition) {
		
		StringBuilder builder = new StringBuilder("delete from ");
		builder.append(table.getTableName());
		
		processCondition(builder,condition);
		
		String sql = builder.toString();
		
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getDeleteByPKSql(Table table) {
		Object uk = getSqlFromCache(table, Method.deleteByPK);
		if(uk instanceof String) {
			return (String)uk;
		}
		
		StringBuilder builder = new StringBuilder("delete from ");
		builder.append(table.getTableName())
		.append(" where ")
		.append(table.getPK().getName()).append("=?");
		
		String sql = builder.toString();
		
		cacheSql(((Map<Method, String>)uk),Method.deleteByPK, sql);
		
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getFindAllObjectsSql(Table table) {
		Object uk = getSqlFromCache(table, Method.findAllObjects);
		if(uk instanceof String) {
			return (String)uk;
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(Keyword.select).append(getAllStr(table)).append(Keyword.from).append(table.getTableName());
		String sql = builder.toString();
		
		cacheSql(((Map<Method, String>)uk),Method.findAllObjects, sql);
		
		return sql;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getFindByPKSql(Table table) {
		
		Object uk = getSqlFromCache(table, Method.findByPK);
		if(uk instanceof String) {
			return (String)uk;
		}
		
		//select * from t where id=?
		StringBuilder builder = new StringBuilder("select * from ");
		builder.append(table.getTableName())
		.append(" where ")
		.append(table.getPK().getName())
		.append("=?");
		String sql = builder.toString();
		
		cacheSql(((Map<Method, String>)uk),Method.findByPK, sql);
		
		return sql;
	}
	
	@Override
	public String getFindObjectsForListSql(Table table,WhereCondition condition) {
		StringBuilder builder = new StringBuilder();
		builder.append(Keyword.select).append(getAllStr(table)).append(Keyword.from).append(table.getTableName());
		processCondition(builder,condition);
		String sql = builder.toString();
		
		return sql;
	}
	
	private String getAllStr(Table table) {
		return Keyword.all.toString();
	}
	
	private void cacheSql(Map<Method, String> cache,Method method,String sql) {
		cache.put(method, sql);
	}

	@Override
	public String getFindPageCountSql(Table table, WhereCondition condition) {
		
		StringBuilder builder = new StringBuilder();
		if(condition != null && (condition.hasDistinct() || condition.hasGroupBy())) {
			builder.append(Keyword.select).append(Keyword.countAll).append(Keyword.from).append(Keyword.tagStart)
			.append(Keyword.select).append(Keyword.all).append(Keyword.from).append(table.getTableName());
			//.append(" where ");
			processCondition(builder,condition);
			builder.append(Keyword.tagEnd).append(" _alias");
			String sql = builder.toString();
			
			return sql;
		} else {
			builder.append(Keyword.select).append(Keyword.countAll).append(Keyword.from).append(table.getTableName());
			//.append(" where ");
			processCondition(builder,condition);
			String sql = builder.toString();
			
			return sql;
		}
	}
	
	@Override
	public String getFindPageDataSql(Table table, WhereCondition condition, int offset,
			int rowsPerPage) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(Keyword.select).append(getAllStr(table)).append(Keyword.from).append(table.getTableName());
		//.append(" where ");
		processCondition(builder,condition);
		String sql = builder.append(Keyword.limit).append(offset).append(Keyword.comma).append(rowsPerPage).toString();
		
		return sql;
	}
	
	private void processCondition(StringBuilder builder,WhereCondition condition) {
		if(condition != null) {
			String condSql = condition.getSql(false);
			if(condSql == null || condSql.trim().length() == 0) {
				return;
			}
			if(condSql.startsWith(" order by")) {
				builder.append(condSql);
			} else {
				builder.append(" where ").append(condSql);
			}
		}
	}

	@Override
	public String getFindObjectsForMapSql(Table table,WhereCondition condition) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(Keyword.select).append(getAllStr(table)).append(Keyword.from).append(table.getTableName());
		processCondition(builder,condition);
		String sql = builder.toString();
		
		return sql;
	}


	@Override
	public String getFindObjectSql(Table table,WhereCondition condition) {
		StringBuilder builder = new StringBuilder();
		builder.append(Keyword.select).append(getAllStr(table)).append(Keyword.from).append(table.getTableName());//.append(" where ");
		processCondition(builder,condition);
		String sql = builder.toString();
		
		return sql;
	}

	private final Map<Method, String> getMethods(Table table) {
		Map<Method, String> map = sqlMap.get(table);
		if(map == null) {
			map = new HashMap<SqlFactory.Method, String>();
			sqlMap.put(table, map);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getSaveSql(Table table) {
		//from cache
		Object uk = getSqlFromCache(table, Method.save);
		if(uk instanceof String) {
			return (String)uk;
		}
		
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(table.getTableName())
		.append("(");
		TableField<?> [] allFields = table.getAllFields();
		StringBuilder paras = new StringBuilder();
		for(int i=0,len=allFields.length;i<len;i++) {
			TableField<?> field = allFields[i];
			builder.append(field.getName());
			paras.append("?");
			if(i < len - 1) {
				builder.append(",");
				paras.append(",");
			}
		}
		
		builder.append(") values (").append(paras).append(")");
		
		String sql = builder.toString();
		
		cacheSql(((Map<Method, String>)uk),Method.save, sql);
		
		return sql;
	}

	private final Object getSqlFromCache(Table table,Method method) {
		Map<Method, String> map = getMethods(table);
		String sql = map.get(method);
		if (sql != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("get sql from cache. ["+sql+"]");
			}
			return sql;
		} else {
			return map;
		}
		
 	}

	@Override
	public String getUpdateByCondition(Table table,WhereCondition condition) {
		
		StringBuilder builder = new StringBuilder("update ");
		builder.append(table.getTableName())
		.append(" set ");
		TableField<?> [] allFields = table.getAllUpdateFields();
		for(int i=0,len=allFields.length;i<len;i++) {
			TableField<?> field = allFields[i];
			if(field.isPK()) {
				continue;//不更新主键
			}
			
			builder.append(field.getName())
			.append("=?");
			if(i < len - 1) {
				builder.append(",");
			}
		}
		processCondition(builder,condition);
		String sql = builder.toString();
		
		return sql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getUpdateSql(Table table) {
		Object uk = getSqlFromCache(table, Method.update);
		if(uk instanceof String) {
			return (String)uk;
		}
		
		StringBuilder builder = new StringBuilder("update ");
		builder.append(table.getTableName())
		.append(" set ");
		TableField<?> [] allFields = table.getAllUpdateFields();
		for(int i=0,len=allFields.length;i<len;i++) {
			TableField<?> field = allFields[i];
			
			if(field.isPK()) {
				continue;//不更新主键
			}
			
			builder.append(field.getName())
			.append("=?");
			if(i < len - 1) {
				builder.append(",");
			}
		}
		builder.append(" where ").append(table.getPK().getName()).append("=?");
		String sql = builder.toString();
		
		cacheSql(((Map<Method, String>)uk),Method.update, sql);
		
		return sql;
	}
	
}
