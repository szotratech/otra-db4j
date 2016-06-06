package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.Table;

public interface FromBuilder extends Db4jBuilder {
	
	TableBuilder from(Table table);
	
	TableBuilder from(Table ... tables);

//	TableBuilder from(AliasTable aliasTable);

//	TableBuilder from(AliasTable ... aliasTables);
	
//	TableBuilder from(String tableName);
	
}
