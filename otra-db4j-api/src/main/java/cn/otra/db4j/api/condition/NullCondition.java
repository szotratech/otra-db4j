package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class NullCondition<T> extends Condition {
	private TableField<T> field;
	private Keyword nullKeyword;
	public NullCondition(TableField<T> field,Keyword nullKeyword) {
		this.field = field;
		this.nullKeyword = nullKeyword;
	}

	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(this.getOperator() != null) {
			builder.append(this.getOperator());
		}
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		
		field.appendTo(builder, builderContext,enableAlias);
		builder.append(nullKeyword);
		
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}
	}
	@Override
	public Object[] getParamValue() {
		return null;
	}
}
