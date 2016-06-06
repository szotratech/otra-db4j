package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.Table;

public interface UpdateBuilder extends Db4jBuilder {

	//update(table).set().set().set().where().executeUpdate();
	
	SetBuilder updateWithCondition(Table table);
	
	SetBuilder updateWithCondition(Table ...tables);
	
}
