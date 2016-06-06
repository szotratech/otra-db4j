package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.AddAbleTableField;

public class AddCondition<T extends Number> extends Condition {
	
	private AddAbleTableField<T> field;
	private Keyword keyword;
	private T rightParameter;
	private Object [] innerObjects;
	
	public AddAbleTableField<T> getField() {
		return field;
	}
	
	public AddCondition(AddAbleTableField<T> field,Keyword keyword,T rightParameter) {
		this.field = field;
		this.keyword = keyword;
		this.rightParameter = rightParameter;
	}

	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,
			boolean enableAlias) {
		innerObjects = new Object[]{rightParameter};
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		this.field.appendTo(builder,this.getBuilderContext(),rightParameter,keyword,enableAlias);
	}

	@Override
	public Object[] getParamValue() {
		return innerObjects;
	}

}
