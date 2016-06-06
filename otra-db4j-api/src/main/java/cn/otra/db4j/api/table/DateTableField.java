package cn.otra.db4j.api.table;

import cn.otra.db4j.api.condition.Condition;

/**
 * 日期字段
 * 日期增加字符比较功能，方便调用
 * @author satuo20
 *
 * @param <T>
 */
public interface DateTableField<T> extends TableField<T> {

	// equal
	Condition eq(String t);

	Condition equal(String t);

	// no equal
	Condition ne(String t);

	Condition notEqual(String t);

	// lessThan
	Condition lt(String t);

	Condition lessThan(String t);

	// less or equal
	Condition le(String t);

	Condition lessOrEqual(String t);

	// greaterThan
	Condition gt(String t);

	Condition greaterThan(String t);

	// greaterOrEqual
	Condition ge(String t);

	Condition greaterOrEqual(String t);

	Condition in(String... ts);

	Condition between(String from, String end);

}
