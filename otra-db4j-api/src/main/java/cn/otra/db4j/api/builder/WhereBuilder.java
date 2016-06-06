package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.condition.Condition;

public interface WhereBuilder extends Db4jBuilder {
	/**
	 * if(condition == null) {
	 *     //do nothing
	 * } 
	 * @param condition
	 * @return
	 */
	OrderByBuilder where(Condition condition);
	
}
