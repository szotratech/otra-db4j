package cn.otra.db4j.api.sql;

import cn.otra.db4j.api.builder.WhereCondition;
import cn.otra.db4j.api.table.Table;

public interface SqlFactory {

	enum Method {
		save,
		findAllObjects,
		findObject,
		findObjectsForList,
		findObjectsForMap,
		findByPK,
		findPageCount,
		findPageData,
		update,
		updateByCondition,
		deleteByPK,
		deleteByCondition
	}
	
	String getFindByPKSql(Table table);
	
	String getFindAllObjectsSql(Table table);
	
	String getFindObjectSql(Table table,WhereCondition condition);
	
	String getFindObjectsForMapSql(Table table,WhereCondition condition);
	
	String getFindObjectsForListSql(Table table,WhereCondition condition);

	String getFindPageCountSql(Table table,WhereCondition condition);
	
	String getFindPageDataSql(Table table,WhereCondition condition,int offset,int rowsPerPage);
	
	String getSaveSql(Table table);
	
	String getUpdateSql(Table table);
	
	String getUpdateByCondition(Table table,WhereCondition condition);
	
	String getDeleteByPKSql(Table table);
	
	String getDeleteByCondition(Table table,WhereCondition condition);
	
}
