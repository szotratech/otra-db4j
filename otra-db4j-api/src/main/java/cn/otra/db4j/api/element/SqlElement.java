package cn.otra.db4j.api.element;

import cn.otra.db4j.api.ctx.BuilderContext;

public interface SqlElement {
	void appendTo(StringBuilder builder,BuilderContext builderContext,boolean enableAlias);
}
