package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class BetweenCondition<T> extends Condition {
	private TableField<T> field;
	private Object from;
	private Object end;
	private Object[] innerParams;
	
	public BetweenCondition(TableField<T> field,Object from,Object end) {
		this.field = field;
		this.from = from;
		this.end  = end;
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(this.getOperator() != null) {
			builder.append(this.getOperator());
		}
		
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		
//		Object fromObj = getRealValue(from);
//		Object endObj = getRealValue(end);
		field.appendTo(builder, builderContext,enableAlias);
		builder.append(Keyword.between)
		.append("?")
		.append(Keyword.and)
		.append("?");
		
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}
		innerParams = new Object[]{from,end};
	}
	
	@Override
	public Object[] getParamValue() {
		return innerParams;
	}
}
