package cn.otra.db4j.utils.generator;

import java.util.ArrayList;
import java.util.List;

public class TableMetaData {
	
	private String pk;
	private boolean autoincrement;
	private String javaPkType;
	private int pkIdx;
	private String tableName;
	private String common;
	private List<TableMetaDataField> fields = new ArrayList<TableMetaDataField>();
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public int getPkIdx() {
		return pkIdx;
	}
	public void setPkIdx(int pkIdx) {
		this.pkIdx = pkIdx;
	}
	public List<TableMetaDataField> getFields() {
		return fields;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getJavaPkType() {
		return javaPkType;
	}
	public void setJavaPkType(String javaPkType) {
		this.javaPkType = javaPkType;
	}
	public boolean isAutoincrement() {
		return autoincrement;
	}
	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}
	public String getCommon() {
		return common;
	}
	public void setCommon(String common) {
		this.common = common;
	}

}
