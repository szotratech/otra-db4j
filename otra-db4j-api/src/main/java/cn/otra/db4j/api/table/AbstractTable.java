package cn.otra.db4j.api.table;

import cn.otra.db4j.api.ctx.BuilderContext;

public abstract class AbstractTable implements Table {
	
	protected String tableName;
	protected String aliasName;
	protected long lastUpdateTime;
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@Override
	public long lastUpdateTime() {
		return lastUpdateTime;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	
	@Override
	public String getAliasName() {
		return aliasName;
	}
 	
	@Override
	public void appendTo(StringBuilder builder, BuilderContext builderContext,boolean enableAlias) {
		
 		if(aliasName != null && enableAlias) {
 			builder.append("`").append(tableName).append("` ").append(aliasName);
 		} else {
 			builder.append(tableName);
 		}
	}
}
