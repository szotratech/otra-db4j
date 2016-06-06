package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class NotInCondition<T> extends Condition {
	private TableField<T> field;
	private Object[] value;
	private String sqlValue;
	
	public NotInCondition(TableField<T> field,EndBuilder endBuilder) {
		this.field = field;
		sqlValue = endBuilder.getSql();
		value = endBuilder.getParams();
	}
	
	public NotInCondition(TableField<T> field,T t) {
		this.field = field;
		value = new Object[]{t};
	}
	
	public NotInCondition(TableField<T> field,String t) {
		this.field = field;
		value = new Object[]{t};
	}
	
	public NotInCondition(TableField<T> field,String ...ts) {
		this.field = field;
		value = ts;
	}
	
	public NotInCondition(TableField<T> field,T ...ts) {
		this.field = field;
		value = ts;
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(this.getOperator() != null) {
			builder.append(this.getOperator());
		}
		
		for(int i=0;i<this.getStartTag();i++) {
			builder.append(Keyword.tagStart);
		}
		
		this.field.appendTo(builder,this.getBuilderContext(),enableAlias);
		
		if(sqlValue != null) {
			builder.append(" not in (").append(sqlValue).append(")");
		} else {
			StringBuilder paramsBuff = new StringBuilder();
			for(int i=0,len=value.length;i<len;i++) {
//				Object obj = value[i];
//				paramsBuff.append(getRealValue(obj));
				paramsBuff.append("?");
				if(i < len - 1) {
					paramsBuff.append(Keyword.comma);
				}
			}
			
			builder.append(" not in (").append(paramsBuff.toString()).append(")");
		}
		
		for(int i=0;i<this.getEndTag();i++) {
			builder.append(Keyword.tagEnd);
		}
 	}

	@Override
	public Object[] getParamValue() {
		return value;
	}
}
