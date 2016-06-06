package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.TableField;

public interface GroupByBuilder extends Db4jBuilder {

	HavingBuilder groupBy(TableField<?> field);
}
