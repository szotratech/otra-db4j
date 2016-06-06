package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.condition.AddCondition;
import cn.otra.db4j.api.table.TableField;

public interface SetBuilder extends WhereBuilder,EndBuilder,Db4jBuilder {
	/**
	 * 设置表字段【key】为值为【value】
	 * @param key
	 * @param value
	 * @return
	 */
	<T> SetBuilder set(TableField<T> key,T value);
	
	<T extends Number> SetBuilder set(AddCondition<T> condition);
	
	<T> SetBuilder setWithBuilder(TableField<T> key,EndBuilder endBuilder);
	
}
