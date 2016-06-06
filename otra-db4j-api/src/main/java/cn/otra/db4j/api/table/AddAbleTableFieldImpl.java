package cn.otra.db4j.api.table;

import cn.otra.db4j.api.condition.AddCondition;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;

public class AddAbleTableFieldImpl<T extends Number> extends TableFieldImpl<T> implements AddAbleTableField<T> {
	public AddAbleTableFieldImpl(Table table,Class<T> tClass, String name, String javaName,
			String comment,boolean isPK) {
		super(table,tClass, name, javaName, comment,isPK);
	}
	
	@Override
	public AddCondition<T> add(T value) {
		//TODO java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Double
		boolean gt0 = value.doubleValue() >= 0;
		return new AddCondition<T>(this,gt0?Keyword.add:null, value);
	}
	
	@Override
	public AddCondition<T> mul(T value) {
		return new AddCondition<T>(this,Keyword.mul, value);
	}
	
	@Override
	public AddCondition<T> div(T value) {
		return new AddCondition<T>(this,Keyword.div, value);
	}

	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,
			T value,Keyword operation, boolean enableAlias) {
		String alias = this.getTable().getAliasName();
		if(alias != null && enableAlias) {
			builder.append(alias).append(".`").append(name).append("`=").append(alias).append(".`").append(name).append("`");
			if(operation != null) {
				builder.append(operation);
			}
			builder.append("?");
		} else {
			builder.append("`").append(name).append("`=`").append(name).append("`");
			if(operation != null) {
				builder.append(operation);
			}
			builder.append("?");
		}
	}

}
