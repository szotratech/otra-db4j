package cn.otra.db4j.api.table;

import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.util.TableUtils;

public class AliasField<T> implements SelectField<T> {
	
//	private static final Logger logger = Logger.getLogger(AliasField.class);
	private static final long serialVersionUID = 8436229229441417799L;
	private SelectField<T> field;
	private Keyword function;
	private String alias;
	
	public AliasField(SelectField<T> field,String alias) {
		this.field = field;
		this.alias = alias;
	}
	
	public void setFunction(Keyword function) {
		this.function = function;
	}
	
	public Keyword getFunction() {
		return function;
	}

	public SelectField<T> getField() {
		return field;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public AliasField<T> as(String alias) {
		this.alias = TableUtils.getMysqlStandField(alias);
		return this;
	}
	
//	@Override
//	public void setBuilderContext(BuilderContext builderContext) {
////		this.builderContext = builderContext;
//	}
	
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		String tableAlias = null;
//		TableField<T> tf = null;
//		if(field instanceof TableField) {
//			tf = (TableField<T>)field;
//		} else if(field instanceof AliasField) {
//			tf = (TableField<T>)(((AliasField<T>)field).getField());
//		}
//		
//		if(tf.getTable() != null && builderContext != null) {
//			tableAlias = builderContext.getAlias(tf.getTable());
//		}
//		if(tableAlias != null) {
//			builder.append(tableAlias).append(".`").append(tf.getName()).append("`");
//		} else {
//			builder.append("`").append(tf.getName()).append("`");
//		}
//		if(alias != null && function == null) {
//			builder.append(" as ").append(alias);
//		}
//		return builder.toString();
//	}
	
	private void init(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		TableField<T> tf = null;
		if(field instanceof TableField) {
			tf = (TableField<T>)field;
		} else if(field instanceof AliasField) {
			tf = (TableField<T>)(((AliasField<T>)field).getField());
		}
		String tableAlias = tf.getTable().getAliasName();
		
//		boolean isCountFn = (function == Keyword.count);
//		if(isCountFn) {
//			tableAlias = null;
//		}
		//add alias
		if(tableAlias != null && enableAlias && !"*".equals(tf.getName())) {
			builder.append(tableAlias).append(".");
		}
		//add value
		if("*".equals(tf.getName())) {
			builder.append("*");
		} else {
			builder.append("`").append(tf.getName()).append("`");
		}
		if(alias != null && function == null) {
			builder.append(" as ").append(alias);
		}
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		if(function == null) {
			init(builder,builderContext,enableAlias);
			return;
		}
		//如果有函数
		switch (function) {
		case avg:
			simpleInit(builder, builderContext,Keyword.avg,enableAlias);
			break;
		case sum:
			simpleInit(builder, builderContext,Keyword.sum,enableAlias);
			break;
		case max:
			simpleInit(builder, builderContext,Keyword.max,enableAlias);
			break;
		case min:
			simpleInit(builder, builderContext,Keyword.min,enableAlias);
			break;
		case count:
			simpleInit(builder, builderContext,Keyword.count,enableAlias);
			break;
		case distinct:
			distinctInit(builder, builderContext,Keyword.distinct,enableAlias);
			break;
		case sumAndDistinct:
			sumAndDistinctInit(builder, builderContext,Keyword.distinct,enableAlias);
			break;
		case countAndDistinct:
			countAndDistinct(builder, builderContext,Keyword.distinct,enableAlias);
			break;
//TODO MORE FUNCTION TO ADD TO
		default:
			throw new RuntimeException(function+" not supported !");
		}
	}
	
	private void simpleInit(StringBuilder builder, BuilderContext builderContext,Keyword keyword,boolean enableAlias) {
		builder.append(keyword).append(Keyword.tagStart);
		
		init(builder,builderContext,enableAlias);
		
		builder.append(Keyword.tagEnd);
		if(alias != null && function != null) {
			builder.append(" as ").append(alias);
		}
	}
	
	private void distinctInit(StringBuilder builder, BuilderContext builderContext,Keyword keyword,boolean enableAlias) {
		builder.append(keyword);
		init(builder,builderContext,enableAlias);
	}
	
	private void sumAndDistinctInit(StringBuilder builder, BuilderContext builderContext,Keyword keyword,boolean enableAlias) {
		builder.append(Keyword.sum).append(Keyword.tagStart).append(Keyword.distinct);
		init(builder,builderContext,enableAlias);
		builder.append(Keyword.tagEnd);
		if(alias != null && function != null) {
			builder.append(" as ").append(alias);
		}
	}
	
	private void countAndDistinct(StringBuilder builder, BuilderContext builderContext,Keyword keyword,boolean enableAlias) {
		builder.append(Keyword.count).append(Keyword.tagStart).append(Keyword.distinct);
		init(builder,builderContext,enableAlias);
		builder.append(Keyword.tagEnd);
		if(alias != null && function != null) {
			builder.append(" as ").append(alias);
		}
	}
	
	
	
}
