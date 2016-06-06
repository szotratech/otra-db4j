package cn.otra.db4j.api.element;

import cn.otra.db4j.api.ctx.BuilderContext;

public class NumberElement implements SqlElement {
	private static final long serialVersionUID = 8436229229441417799L;
	private Number value;
	public NumberElement(Number value) {
		this.value = value;
	}
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		builder.append(value);
	}

}
