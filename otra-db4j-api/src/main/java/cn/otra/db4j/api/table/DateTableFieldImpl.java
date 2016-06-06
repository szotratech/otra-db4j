package cn.otra.db4j.api.table;

import cn.otra.db4j.api.common.Comparator;
import cn.otra.db4j.api.condition.BetweenCondition;
import cn.otra.db4j.api.condition.CompareCondition;
import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.condition.InCondition;

public class DateTableFieldImpl<T> extends TableFieldImpl<T> implements DateTableField<T> {
	private static final long serialVersionUID = 8436229229441417799L;
	public DateTableFieldImpl(Table table,Class<T> tClass, String name, String javaName,String comment,boolean isPK) {
		super(table,tClass, name, javaName,comment,isPK);
	}

	@Override
	public Condition equal(String t) {
		return new CompareCondition<T>(this, Comparator.EQ, t);
	}

	@Override
	public Condition ne(String t) {
		return notEqual(t);
	}

	@Override
	public Condition notEqual(String t) {
		return new CompareCondition<T>(this, Comparator.NE, t);
	}

	@Override
	public Condition lt(String t) {
		return lessThan(t);
	}

	@Override
	public Condition lessThan(String t) {
		return new CompareCondition<T>(this, Comparator.LT, t);
	}

	@Override
	public Condition le(String t) {
		return lessOrEqual(t);
	}

	@Override
	public Condition lessOrEqual(String t) {
		return new CompareCondition<T>(this, Comparator.LE, t);
	}

	@Override
	public Condition gt(String t) {
		return greaterThan(t);
	}

	@Override
	public Condition greaterThan(String t) {
		return new CompareCondition<T>(this, Comparator.GT, t);
	}

	@Override
	public Condition ge(String t) {
		return greaterOrEqual(t);
	}

	@Override
	public Condition greaterOrEqual(String t) {
		return new CompareCondition<T>(this, Comparator.GE, t);
	}

	@Override
	public Condition in(String ...ts) {
		return new InCondition<T>(this,ts);
	}

	@Override
	public Condition between(String from, String end) {
		return new BetweenCondition<T>(this, from, end);
	}
	
	@Override
	public Condition eq(String t) {
		return equal(t);
	}
	
}
