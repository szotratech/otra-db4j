package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class LikeCondition<T> extends Condition {
	private TableField<T> field;
	private String value;
	private boolean isNotLike = false;
	
	public LikeCondition(TableField<T> field,String value) {
		this.field = field;
		this.value = value;
		this.isNotLike = false;
	}
	
	public LikeCondition(TableField<T> field,boolean isNotLike,String value) {
		this.field = field;
		this.value = value;
		this.isNotLike = isNotLike;
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
		
		if(isNotLike) {
			builder.append(Keyword.notLike);
		} else {
			builder.append(Keyword.like);
		}
		
		builder.append("?");
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TableField<T> getField() {
		return field;
	}

	public void setField(TableField<T> field) {
		this.field = field;
	}

	@Override
	public Object[] getParamValue() {
		return new Object[]{value};
	}
}
