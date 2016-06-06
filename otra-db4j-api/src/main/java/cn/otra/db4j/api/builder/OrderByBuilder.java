package cn.otra.db4j.api.builder;

import cn.otra.db4j.api.table.TableField;

public interface OrderByBuilder extends GroupByBuilder,EndBuilder {
	
	SortTypeBuilder orderBy(TableField<?> field);
	
}
