package cn.otra.db4j.api.condition;

import java.util.Collection;
import java.util.Iterator;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.table.TableField;

public class InCondition<T> extends Condition {
	private TableField<T> field;
	private Object[] value;
	private String sqlValue;
	
	public InCondition(TableField<T> field,EndBuilder endBuilder) {
		this.field = field;
		sqlValue = endBuilder.getSql();
		value = endBuilder.getParams();
	}
	
	public InCondition(TableField<T> field,T t) {
		this.field = field;
		value = new Object[]{t};
	}
	
	public InCondition(TableField<T> field,String t) {
		this.field = field;
		value = new Object[]{t};
	}
	
	public InCondition(TableField<T> field,String ...ts) {
		this.field = field;
		value = ts;
	}
	
	public InCondition(TableField<T> field,T ...ts) {
		this.field = field;
		value = ts;
	}
	
	public InCondition(TableField<T> field,Collection<T> collection) {
		this.field = field;
		value = new Object[collection.size()];
		int idx = 0;
		Iterator<T> it = collection.iterator();
		while (it.hasNext()) {
			value[idx++] = it.next();
		}
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
			builder.append(" in (").append(sqlValue).append(")");
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
			
			builder.append(" in (").append(paramsBuff.toString()).append(")");
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
