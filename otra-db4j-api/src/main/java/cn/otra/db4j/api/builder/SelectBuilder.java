package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.SelectField;
import cn.otra.db4j.api.table.Table;

public interface SelectBuilder extends Db4jBuilder {
	
	FromBuilder select(SelectField<?> ...fields);
	
	TableBuilder selectFrom(Table table);
	
	FromBuilder selectDistinct(SelectField<?> ...fields);
	
	TableBuilder selectDistinctFrom(Table table);
	
}
