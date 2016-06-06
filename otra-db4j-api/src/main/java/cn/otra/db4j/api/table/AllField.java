package cn.otra.db4j.api.table;

import cn.otra.db4j.api.ctx.BuilderContext;


public class AllField<T> extends TableFieldImpl<T> {
	private static final long serialVersionUID = 8436229229441417799L;
	public AllField(Table table,Class<T> tClass, String name,String comment,boolean isPK) {
		super(table,tClass, name,null,comment,isPK);
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		String alias = this.getTable().getAliasName();
		if(alias != null && enableAlias) {
			builder.append(alias).append(".").append(name);
		} else {
			builder.append(name);
		}
	}
	
}
