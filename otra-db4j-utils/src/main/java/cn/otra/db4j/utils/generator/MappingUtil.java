package cn.otra.db4j.utils.generator;

import java.util.HashMap;
import java.util.Map;

public class MappingUtil {
	private static final Map<String,String> var4MysqlToJavaMap = new HashMap<String,String>();
	private static final Map<String,String> var4JavaToMysqlMap = new HashMap<String,String>();
	static {
		init();
	}
	private static final void init() {
		var4MysqlToJavaMap.put("BIGINT", "Long");
		var4MysqlToJavaMap.put("INT", "Integer");
		var4MysqlToJavaMap.put("FLOAT", "Float");
		var4MysqlToJavaMap.put("DATETIME", "Date");
		var4MysqlToJavaMap.put("TIMESTAMP", "Date");
		var4MysqlToJavaMap.put("DATE", "Date");
		var4MysqlToJavaMap.put("ENUM", "String");
		var4MysqlToJavaMap.put("CHAR", "String");
		var4MysqlToJavaMap.put("VARCHAR", "String");
		var4MysqlToJavaMap.put("TEXT", "String");
		var4MysqlToJavaMap.put("MEDIUMTEXT", "String");
		var4MysqlToJavaMap.put("LONGTEXT", "String");
		var4MysqlToJavaMap.put("BIT", "Integer");
		var4MysqlToJavaMap.put("BOOL", "Boolean");
		var4MysqlToJavaMap.put("BOOLEAN", "Boolean");
		var4MysqlToJavaMap.put("TINYINT", "Boolean");
		var4MysqlToJavaMap.put("SMALLINT", "Integer");
		var4MysqlToJavaMap.put("DOUBLE", "Double");
		var4MysqlToJavaMap.put("DECIMAL", "Double");
		
//		var4MysqlToJavaMap.put("POINT", "java.sql.Blob");
		var4JavaToMysqlMap.put("Long","bigint(20)" );
		var4JavaToMysqlMap.put("Integer","int(11)");
		var4JavaToMysqlMap.put("Float","float(6,2)");
		var4JavaToMysqlMap.put("Date","datetime");
		var4JavaToMysqlMap.put("String","varchar(20)");
		var4JavaToMysqlMap.put("Boolean","tinyint(1)");
		var4JavaToMysqlMap.put("Double","double(8,2)");
	}
	
	public static final String getJavaType(String mysqlType) {
		String type = var4MysqlToJavaMap.get(mysqlType);
		if(type == null) {
//			System.err.println("mysqlType = "+mysqlType);
			type = "String";
		}
		return type;
	}
	
	public static final String getMySqlType(String javaType) {
		String type = var4JavaToMysqlMap.get(javaType);
		if(type == null) {
			System.err.println("-- 警告：字段["+javaType+"]找不到对应的数据库类型。");
		}
		return type;
	}
}
