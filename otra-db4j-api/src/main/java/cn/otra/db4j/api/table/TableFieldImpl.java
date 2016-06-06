package cn.otra.db4j.api.table;

import java.util.Collection;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.common.Comparator;
import cn.otra.db4j.api.condition.BetweenCondition;
import cn.otra.db4j.api.condition.CompareConditions;
import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.condition.InCondition;
import cn.otra.db4j.api.condition.LikeCondition;
import cn.otra.db4j.api.condition.NotInCondition;
import cn.otra.db4j.api.condition.NullCondition;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.util.TableUtils;


public class TableFieldImpl<T> implements TableField<T> {
	protected final Table table;
	protected final String name;
	protected final String javaName;
	protected final String comment;
	protected final boolean isPK;
	protected final Class<T> tClass;
			
	public TableFieldImpl(Table table,Class<T> tClass,String name,String javaName,String comment,boolean isPK) {
		this.table = table;
		this.name = name;
		this.javaName = javaName;
		this.comment = comment;
		this.isPK = isPK;
		this.tClass = tClass;
	}
	
	@Override
	public Class<T> getJavaType() {
		return tClass;
	}
	
	@Override
	public boolean isPK() {
		return isPK;
	}
	
	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		String alias = this.getTable().getAliasName();
//		if(table != null && builderContext != null) {
//			alias = builderContext.getAlias(table);
//		}
		if(alias != null && enableAlias) {
			builder.append(alias).append(".`").append(name).append("`");
		} else {
			builder.append("`").append(name).append("`");
		}
	}

	@Override
	public Condition between(T from, T end) {
		return new BetweenCondition<T>(this,from, end);
	}

	@Override
	public Condition eq(EndBuilder endBuilder) {
		return equal(endBuilder);
	}

	@Override
	public Condition eq(T t) {
		return CompareConditions.get(this, Comparator.EQ, t);
	}

	@Override
	public Condition eq(TableField<T> field) {
		return CompareConditions.get(this, Comparator.EQ, field);
	}

	@Override
	public Condition equal(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.EQ, endBuilder);
	}

	@Override
	public Condition equal(T t) {
		return eq(t);
	}
	
	@Override
	public Condition equal(TableField<T> field) {
		return eq(field);
	}


	@Override
	public Condition ge(EndBuilder endBuilder) {
		return greaterOrEqual(endBuilder);
	}

	@Override
	public Condition ge(T t) {
		return greaterOrEqual(t);
	}

	@Override
	public Condition ge(TableField<T> field) {
		return greaterOrEqual(field);
	}

	@Override
	public String getJavaName() {
		return javaName;
	}

	public String getName() {
		return name;
	}

	public Table getTable() {
		return table;
	}

	@Override
	public Condition greaterOrEqual(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.GE, endBuilder);
	}

	@Override
	public Condition greaterOrEqual(T t) {
		return CompareConditions.get(this, Comparator.GE, t);
	}

	@Override
	public Condition greaterOrEqual(TableField<T> field) {
		return CompareConditions.get(this, Comparator.GE, field);
//		return CompareConditions.get(this, Comparator.GE, field);
	}

	@Override
	public Condition greaterThan(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.GT, endBuilder);
	}

	@Override
	public Condition greaterThan(T t) {
		return CompareConditions.get(this, Comparator.GT, t);
	}

	@Override
	public Condition greaterThan(TableField<T> field) {
		return CompareConditions.get(this, Comparator.GT, field);
	}

	@Override
	public Condition gt(EndBuilder endBuilder) {
		return greaterThan(endBuilder);
	}

	@Override
	public Condition gt(T t) {
		return greaterThan(t);
	}

	@Override
	public Condition gt(TableField<T> field) {
		return greaterThan(field);
	}

	@Override
	public Condition in(EndBuilder endBuilder) {
		return new InCondition<T>(this,endBuilder);
	}

	@Override
	public Condition in(T... ts) {
		return new InCondition<T>(this,ts);
	}

	@Override
	public Condition in(Collection<T> collection) {
		return new InCondition<T>(this,collection);
	}

	@Override
	public Condition le(EndBuilder endBuilder) {
		return lessOrEqual(endBuilder);
	}

	@Override
	public Condition le(T t) {
		return lessOrEqual(t);
	}

	@Override
	public Condition le(TableField<T> field) {
		return lessOrEqual(field);
	}

	@Override
	public Condition lessOrEqual(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.LE, endBuilder);
	}

	@Override
	public Condition lessOrEqual(T t) {
		return CompareConditions.get(this, Comparator.LE, t);
	}

	@Override
	public Condition lessOrEqual(TableField<T> field) {
		return CompareConditions.get(this, Comparator.LE, field);
	}

	@Override
	public Condition lessThan(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.LT, endBuilder);
	}

	@Override
	public Condition lessThan(T t) {
		return CompareConditions.get(this, Comparator.LT, t);
	}

	@Override
	public Condition lessThan(TableField<T> field) {
		return CompareConditions.get(this, Comparator.LT, field);
	}

	@Override
	public Condition like(String value) {
		return new LikeCondition<T>(this,value);
	}
	
	@Override
	public Condition likeContain(String value) {
		return new LikeCondition<T>(this,Keyword.likeO+value+Keyword.likeO);
	}
	
	@Override
	public Condition contain(String value) {
		return likeContain(value);
	}
	
	@Override
	public Condition likeEndWith(String value) {
		return new LikeCondition<T>(this,Keyword.likeO+value);
	}
	
	@Override
	public Condition likeStartWith(String value) {
		return new LikeCondition<T>(this,value+Keyword.likeO);
	}
	
	@Override
	public Condition notLike(String value) {
		return new LikeCondition<T>(this,true,value);
	}

	@Override
	public Condition notLikeEndWith(String value) {
		return new LikeCondition<T>(this,true,Keyword.likeO+value);
	}

	@Override
	public Condition notLikeStartWith(String value) {
		return new LikeCondition<T>(this,true,value+Keyword.likeO);
	}

	@Override
	public Condition notLikeContain(String value) {
		return new LikeCondition<T>(this,true,Keyword.likeO+value+Keyword.likeO);
	}
	
	@Override
	public Condition notContain(String value) {
		return new LikeCondition<T>(this,true,Keyword.likeO+value+Keyword.likeO);
	}
	
	@Override
	public Condition lt(EndBuilder endBuilder) {
		return lessThan(endBuilder);
	}

	@Override
	public Condition lt(T t) {
		return lessThan(t);
	}

	@Override
	public Condition lt(TableField<T> field) {
		return lessThan(field);
	}

	@Override
	public Condition ne(EndBuilder endBuilder) {
		return notEqual(endBuilder);
	}

	@Override
	public Condition ne(T t) {
		return notEqual(t);
	}

	@Override
	public Condition ne(TableField<T> field) {
		return notEqual(field);
	}

	@Override
	public Condition notEqual(EndBuilder endBuilder) {
		return CompareConditions.get(this, Comparator.NE, endBuilder);
	}

	@Override
	public Condition notEqual(T t) {
		return CompareConditions.get(this, Comparator.NE, t);
	}

	@Override
	public Condition notEqual(TableField<T> field) {
		return CompareConditions.get(this, Comparator.NE, field);
	}
	
	@Override
	public AliasField<T> as(String alias) {
		alias = TableUtils.getMysqlStandField(alias);
		return new AliasField<T>(this, alias);
	}

//	@Override
//	public void setBuilderContext(BuilderContext builderContext) {
//		//nothing to do.
//	}
	
	@Override
	public AliasField<T> avg() {
		AliasField<T> af = new AliasField<T>(this, null);
		af.setFunction(Keyword.avg);
		return af;
	}

	@Override
	public Condition isNotNull() {
		return new NullCondition<T>(this, Keyword.isNotNull);
	}

	@Override
	public Condition isNull() {
		return new NullCondition<T>(this, Keyword.isNull);
	}

	@Override
	public Condition notIn(T... ts) {
		return new NotInCondition<T>(this, ts);
	}

	@Override
	public Condition notIn(EndBuilder endBuilder) {
		return new NotInCondition<T>(this, endBuilder);
	}
	
}
