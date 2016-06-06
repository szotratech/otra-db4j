package cn.otra.db4j.api.sql;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.table.Table;

public class SqlFactoryManager {
	
	private static final Logger logger = Logger.getLogger(SqlFactoryManager.class);
	private static final Map<Dialect, SqlFactory> cacheMap = new ConcurrentHashMap<Dialect, SqlFactory>();
	
	static {
		initDialect();
	}
	
	private static void initDialect() {
		cacheMap.put(Dialect.MySql, new MySqlFactory());
		//TODO 当增加数据库支持时，在些初始化方言
	}
	
	/**
	 * 根据方言获取缓存，（可以统一管理方言）
	 * @param dialect
	 * @return
	 */
	private static final SqlFactory getSqlFactory(Dialect dialect) {
		SqlFactory factory = cacheMap.get(dialect);
		if(factory == null) {
			logger.error("["+dialect+"]not supported.");
			throw new RuntimeException("不支持的方言类型:"+dialect);
		}
		return factory;
	}
	
	public static final <T extends Serializable> String getSql(Dialect dialect,Table table,SqlFactory.Method method,WhereCondition condition) {
		
		SqlFactory sqlFactory = getSqlFactory(dialect);
		
		switch (method) {
		case save:
			return sqlFactory.getSaveSql(table);
		case findByPK:
			return sqlFactory.getFindByPKSql(table);
		case update:
			return sqlFactory.getUpdateSql(table);
		case updateByCondition:
			return sqlFactory.getUpdateByCondition(table, condition);
		case deleteByCondition:
			return sqlFactory.getDeleteByCondition(table,condition);
		case deleteByPK:
			return sqlFactory.getDeleteByPKSql(table);
		case findAllObjects:
			return sqlFactory.getFindAllObjectsSql(table);
		case findObject:
			return sqlFactory.getFindObjectSql(table, condition);
		case findObjectsForList:
			return sqlFactory.getFindObjectsForListSql(table, condition);
		case findObjectsForMap:
			return sqlFactory.getFindObjectsForMapSql(table, condition);
		case findPageCount:
			return sqlFactory.getFindPageCountSql(table, condition);
		default:
			logger.error(method + " not found!");
			return null;
		}
	}
	
	public static final <T extends Serializable> String getPageDataSql(Dialect dialect,Table table,SqlFactory.Method method,WhereCondition condition,int offset,int rowsPerPage) {

		SqlFactory sqlFactory = getSqlFactory(dialect);
		
		return sqlFactory.getFindPageDataSql(table, condition, offset, rowsPerPage);
	}
}
