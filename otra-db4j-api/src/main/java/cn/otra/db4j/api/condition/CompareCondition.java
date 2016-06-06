package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.common.Comparator;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class CompareCondition<T> extends Condition {
	private TableField<T> field;
	private Comparator comparator;
	private Object rightParameter;
	
	private Object [] innerObjects;

	private void init(TableField<T> field, Comparator comparator, Object rightParameter) {
		this.field = field;
		this.comparator = comparator;
		this.rightParameter = rightParameter;
	}
	
	public CompareCondition(TableField<T> field, Comparator comparator, Object rightParameter) {
		init(field, comparator, rightParameter);
	}

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(this.getOperator() != null) {
			builder.append(this.getOperator());
		}
		
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		this.field.appendTo(builder,this.getBuilderContext(),enableAlias);
		builder.append(this.comparator);
		
//		if(this.rightParameter instanceof String) {
//			builder.append("\'").append(this.rightParameter).append("\'");
//		} else if(this.rightParameter instanceof Date) {
//			String v = DateUtils.dateToString((Date)(this.rightParameter), TimeFormatter.FORMATTER1);
//			builder.append("\'").append(v).append("\'");
//		} else if(this.rightParameter instanceof TableField) {
//			((TableField<T>) this.rightParameter).appendTo(builder,this.getBuilderContext());
//		} else 
		if(this.rightParameter instanceof EndBuilder) {
			builder.append(Keyword.tagStart).append(((EndBuilder)this.rightParameter).getSql()).append(Keyword.tagEnd);
			innerObjects = ((EndBuilder)rightParameter).getParams();
		}else if(this.rightParameter instanceof TableField) {
			((TableField<T>) this.rightParameter).appendTo(builder,this.getBuilderContext(),enableAlias);
		}else  {
//			builder.append(getRealValue(rightParameter));
			builder.append("?");
			innerObjects = new Object[]{rightParameter};
		}
		
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}		
	}

	public TableField<T> getField() {
		return field;
	}

	public void setField(TableField<T> field) {
		this.field = field;
	}

	public Object getRightParameter() {
		return rightParameter;
	}

	public void setRightParameter(Object rightParameter) {
		this.rightParameter = rightParameter;
	}
	
	@Override
	public Object[] getParamValue() {
		return innerObjects;
	}
}
