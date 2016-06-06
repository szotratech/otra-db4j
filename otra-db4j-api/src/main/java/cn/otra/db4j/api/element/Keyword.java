package cn.otra.db4j.api.element;

import cn.otra.db4j.api.ctx.BuilderContext;



public enum Keyword implements SqlElement {
	parameter("?"),
	comma(","),
	tagStart("("),
	tagEnd(")"),
	select("select "),
	update("update "),
	set(" set "),
	from(" from "),
	all("*"),
	countStart("count("),
	countAll("count(*)"),
	where(" where "),
	on(" on "),
	join(" join "),
	leftJoin(" left join "),
	rightJoin(" right join "),
	groupBy(" group by "),
	having(" having "),
	limit(" limit "),
	orderBy(" order by "),
	usingStart(" using ("),
	usingEnd(") "),
	asc(" asc "),
	desc(" desc "),
	between(" between "),
	and(" and "),
	like(" like "),
	notLike(" not like "),
	likeO("%"),
	isNull(" is null "),
	isNotNull(" is not null "),
	selectFrom("select * from "),
	//functions
	avg("avg"),
	distinct("distinct "),
	count("count"),
	max("max"),
	sum("sum"),
	min("min"),
	sumAndDistinct("sumAndDistinct"),
	countAndDistinct("countAndDistinct"),
	add("+"),
	sub("-"),
	mul("*"),
	div("/");
	
	private final String keyword;
	private Keyword(String keyword) {
		this.keyword = keyword;
	}
	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext dbContext,boolean enableAlias) {
		builder.append(keyword);
	}
	
	@Override
	public String toString() {
		return keyword;
	}
	
}
