package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.table.TableField;

public interface JoinBuilder extends EndBuilder {
	
	TableBuilder on(Condition condition);
	
	JoinBuilder using(TableField<?> field);
	
}
