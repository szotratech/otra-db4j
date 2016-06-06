package cn.otra.db4j.api.ctx;

import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.builder.FromBuilder;
import cn.otra.db4j.api.builder.HavingBuilder;
import cn.otra.db4j.api.builder.JoinBuilder;
import cn.otra.db4j.api.builder.SelectBuilder;
import cn.otra.db4j.api.builder.SetBuilder;
import cn.otra.db4j.api.builder.SortTypeBuilder;
import cn.otra.db4j.api.builder.TableBuilder;
import cn.otra.db4j.api.builder.UpdateBuilder;
import cn.otra.db4j.api.builder.WhereBuilder;
import cn.otra.db4j.api.element.SqlElement;

public interface BuilderContext extends TableBuilder, SelectBuilder,UpdateBuilder,SetBuilder,FromBuilder,WhereBuilder,JoinBuilder,HavingBuilder,SortTypeBuilder, EndBuilder{
	
//	String getAlias(Table table);
	
	StringBuilder getBuilder();
	
	<T extends SqlElement> BuilderContext add(T e);
	
}
