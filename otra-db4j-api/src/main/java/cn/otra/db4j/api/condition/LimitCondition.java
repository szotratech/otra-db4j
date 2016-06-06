package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;

public class LimitCondition extends Condition {
	
	private Number offset;
	private Number rowsPerPage;
	private Object[] innerParams;
	
	public LimitCondition(Number offset, Number rowsPerPage) {
		this.offset = offset;
		this.rowsPerPage = rowsPerPage;
		innerParams = new Object[]{offset,rowsPerPage};
	}

	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		builder.append(Keyword.limit).append("?").append(Keyword.comma).append("?");
	}

	public Number getOffset() {
		return offset;
	}

	public void update(Number offset, Number rowsPerPage) {
		this.offset = offset;
		this.rowsPerPage = rowsPerPage;
		innerParams = new Object[]{offset,rowsPerPage};
	}
	
	public void setOffset(Number offset) {
		this.offset = offset;
	}

	public Number getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(Number rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	@Override
	public Object[] getParamValue() {
		return innerParams;
	}
}
