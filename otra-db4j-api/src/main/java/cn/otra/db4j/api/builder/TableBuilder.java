package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.Table;

public interface TableBuilder extends WhereBuilder,GroupByBuilder,OrderByBuilder{
	
	JoinBuilder join(Table table);
	JoinBuilder rightJoin(Table table);
	JoinBuilder leftJoin(Table table);
	
//	Condition and(Condition condition);
	
//	JoinBuilder join(AliasTable table);
//	JoinBuilder rightJoin(AliasTable table);
//	JoinBuilder leftJoin(AliasTable table);
	
}
