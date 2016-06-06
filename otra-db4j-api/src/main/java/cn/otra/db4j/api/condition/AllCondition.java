package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;

public class AllCondition extends Condition {

	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(this.getOperator() != null) {
			builder.append(this.getOperator());
		}
		
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		
		builder.append("?");
		
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}
	}
	
	private static final Object[] constObject = new Object[]{1};
	@Override
	public Object[] getParamValue() {
		return constObject;
	}
}
