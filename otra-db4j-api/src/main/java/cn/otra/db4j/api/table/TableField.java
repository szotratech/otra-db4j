package cn.otra.db4j.api.table;

import java.util.Collection;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.condition.Condition;


public interface TableField<T> extends SelectField<T> {
	
	Class<T> getJavaType();
	
	boolean isPK();
	
	Table getTable();
	
	/**
	 * 获取数据库中的字段名
	 * @return
	 */
	String getName();
	/**
	 * 获取JAVA规范的属性名
	 * @return
	 */
	String getJavaName();
	
	String getComment();
	
	//equal
	Condition eq(T t);
	Condition eq(TableField<T> field);
	Condition eq(EndBuilder endBuilder);
	
	Condition equal(T t);
	Condition equal(TableField<T> field);
	Condition equal(EndBuilder endBuilder);

	//no equal
	Condition ne(T t);
	Condition ne(TableField<T> field);
	Condition ne(EndBuilder endBuilder);
	
	Condition notEqual(T t);
	Condition notEqual(TableField<T> field);
	Condition notEqual(EndBuilder endBuilder);
	
	//lessThan
	Condition lt(T t);
	Condition lt(TableField<T> field);
	Condition lt(EndBuilder endBuilder);
	
	Condition lessThan(T t);
	Condition lessThan(TableField<T> field);
	Condition lessThan(EndBuilder endBuilder);
	
	//less or equal
	Condition le(T t);
	Condition le(TableField<T> field);
	Condition le(EndBuilder endBuilder);
	
	Condition lessOrEqual(T t);
	Condition lessOrEqual(TableField<T> field);
	Condition lessOrEqual(EndBuilder endBuilder);
	
	//greaterThan
	Condition gt(T t);
	Condition gt(TableField<T> field);
	Condition gt(EndBuilder endBuilder);
	
	Condition greaterThan(T t);
	Condition greaterThan(TableField<T> field);
	Condition greaterThan(EndBuilder endBuilder);
	
	//greaterOrEqual
	Condition ge(T t);
	Condition ge(TableField<T> field);
	Condition ge(EndBuilder endBuilder);
	
	Condition greaterOrEqual(T t);
	Condition greaterOrEqual(TableField<T> field);
	Condition greaterOrEqual(EndBuilder endBuilder);
	
	Condition in(T ...ts);
	
	Condition in(Collection<T> collection);
	
	Condition in(EndBuilder endBuilder);
	
	Condition notIn(T ...ts);
	
	Condition notIn(EndBuilder endBuilder);
	
	Condition between(T from,T end);
	
	Condition like(String value);
	
	/**
	 * value%
	 * @param value
	 * @return
	 */
	Condition likeEndWith(String value);
	
	/**
	 * %value
	 * @param value
	 * @return
	 */
	Condition likeStartWith(String value);
	
	/**
	 * %value%
	 * @param value
	 * @return
	 */
	Condition likeContain(String value);
	
	Condition notLike(String value);
	
	/**
	 * value%
	 * @param value
	 * @return
	 */
	Condition notLikeEndWith(String value);
	
	/**
	 * %value
	 * @param value
	 * @return
	 */
	Condition notLikeStartWith(String value);
	
	/**
	 * %value%
	 * @param value
	 * @return
	 */
	Condition notLikeContain(String value);
	
	Condition contain(String value);
	
	Condition notContain(String value);
	
	Condition isNotNull();
	
	Condition isNull();
	
	//alias
	AliasField<T> as(String alias);
	
	//simple function
	AliasField<T> avg();
	
}
