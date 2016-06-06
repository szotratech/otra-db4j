package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.condition.Condition;

public interface HavingBuilder extends OrderByBuilder, EndBuilder {
	OrderByBuilder having(Condition condition);
}
