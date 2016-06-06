package cn.otra.db4j.utils.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaUtil {
	
	/**
	 * 格式　<b>表名前缀,类名前缀:表名前缀,类名前缀</b>
	 */
	public static String PREFIX = null;//
	
	public static Map<String,String> prefixMap = new HashMap<String, String>();
	
	public static final String getJavaStandField(String sqlField) {
		char [] chars = sqlField.toCharArray();
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if('_' == c && i < chars.length-1) {
				if(isLowerEnglishChar(chars[i+1])) {
					chars[i+1] = (char)(chars[i+1]-32);
				}
			}
		}
		String dest = new String(chars);
		return dest.replace("_", "");
	}
	
	/**
	 * 是否小写的英文字母
	 * @param value
	 * @return
	 */
	private static final boolean isLowerEnglishChar(char value) {
		return (value >= 'a' && value <= 'z');
	}
	
	public static final String firstUpString(String value) {
		if(value.length() == 1) {
			return value.toUpperCase();
		} else if(value.length() >= 2) {
			return value.substring(0, 1).toUpperCase()+value.substring(1);
		} else {
			return value;
		}
	}
	
	public static final void genGetterAndSetter(BufferedWriter writer,String type,String fieldName) throws IOException {
		writer.newLine();
		writer.write("    public "+type+" get"+firstUpString(fieldName)+" () {");
		writer.newLine();
		writer.write("        return "+fieldName+";");
		writer.newLine();
		writer.write("    }");
		writer.newLine();
		
//		if("Date".equals(type)) {
//			writer.write("    public String get"+firstUpString(fieldName)+"2 () {");
//			writer.newLine();
//			writer.write("        if("+fieldName+" == null) {");
//			writer.newLine();
//			writer.write("            return null;");
//			writer.newLine();
//			writer.write("        }");
//			writer.newLine();
//			writer.write("        return DateUtils.dateToString("+fieldName+", DateUtils.FormatType.yyyy_MM_dd);");
//			writer.newLine();
//			writer.write("    }");
//			writer.newLine();
//		}
		writer.newLine();
		writer.write("    public void set"+firstUpString(fieldName)+" ("+type+" "+fieldName+") {");
		writer.newLine();
		writer.write("        this."+fieldName+" = "+fieldName+";");
		writer.newLine();
		writer.write("    }");
		writer.newLine();
	}
	
	private final String getTableCommon(Connection conn,String tableName) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//show full fields from car_info; 
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT TABLE_COMMENT from information_schema.TABLES where table_name='"+tableName+"'"); 
			////查看表注释
			TableMetaData data = new TableMetaData();
			data.setTableName(tableName);
			String common = null;
			while(rs.next()) {
				common = rs.getString("TABLE_COMMENT");
				common = common.split(";")[0];
				break;
			}
			return common;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn,stmt,rs);
		}
		return null;
	}
	
	public final TableMetaData getMetaData(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//show full fields from car_info; 
			conn = DBUtil.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("show full fields from "+tableName); 
			//"SELECT TABLE_COMMENT from information_schema.TABLES where table_name='"+tableName+"'"//查看表注释
			TableMetaData data = new TableMetaData();
			data.setTableName(tableName);
			data.setCommon(getTableCommon(conn, tableName));
			int idx = 0;
			while(rs.next()) {
				TableMetaDataField field = new TableMetaDataField();
				field.setComment(rs.getString("Comment"));
				field.setField(rs.getString("Field"));
				field.setKey(rs.getString("Key"));
				field.setType(rs.getString("Type"));
				field.setExtra(rs.getString("Extra"));
				field.setDefaultVal(rs.getString("Default"));
				if(field.getKey() != null && "PRI".equals(field.getKey())) {
					field.setPK(true);
					data.setPk(field.getField());
					data.setPkIdx(idx);
					data.setJavaPkType(getJavaType(field.getType()));
					//TODO 和数据库相关
					if(field.getExtra() != null && field.getExtra().equals("auto_increment")) {
						data.setAutoincrement(true);
					}
				}
				data.getFields().add(field);
				idx ++;
			}
			if(data.getPk() == null) {
				System.err.println("table ["+tableName+"] 's primary key not found!");
//				System.exit(1);
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn,stmt,rs);
		}
		return null;
	}
	
	
	
	public static final String getJavaType(String mysqlType) {
		if(mysqlType.contains("(")) {
			mysqlType = mysqlType.substring(0,mysqlType.indexOf('('));
		}
		mysqlType = mysqlType.toUpperCase();
		return MappingUtil.getJavaType(mysqlType);
	}
	
	private static final void initPrefixMap() {
		if(PREFIX == null) {
			return ;
		}
		for(String p:PREFIX.split(":")) {
			String arry [] = p.split(",");
			if(arry.length == 2) {
				prefixMap.put(p.split(",")[0], p.split(",")[1]);
			} else {
				prefixMap.put(p.split(",")[0], null);
			}
		}
	}
	
	public final void execute(String domain,String outputDir) {
		initPrefixMap();
		String []tableNames = null;
		try {
			if(outputDir == null || outputDir.trim().length() < 1) {
				outputDir = System.getProperty("user.dir");
			}
			if(tableNames==null||tableNames.length==0) {
				tableNames = new String[]{};
				tableNames = getTableNames().toArray(tableNames);
			}
			
			genStaticImports(tableNames,domain,outputDir);
			
			for(String tableName:tableNames) {
				tableName = tableName.trim();
				String className = tableName;
				className = getClassName(tableName);
//				System.err.println(tableName+" "+className+" "+firstUpString(getJavaStandField(className)));
				genTableClass(domain,tableName,tableName,outputDir);
				
				genPoClassFromTable(domain,tableName,className,outputDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final String getClassName(String tableName) {
		tableName = tableName.trim();
		String className = tableName;
		boolean found = false;
		if(prefixMap.size() > 0) { //如果要截取前缀
			for(Map.Entry<String, String> en:prefixMap.entrySet()) {
				String tablePre = en.getKey();//前缀
				String classPre = en.getValue();//替换
				if(classPre == null ||classPre.trim().length() == 0) {
					classPre = "";
				}
				if(tablePre!= null &&tablePre.trim().length() > 0 && tableName.startsWith(tablePre+"_")) {
					found = true;
					className = tableName.substring(tableName.indexOf("_"));
					className = classPre+className;
					className = firstUpString(getJavaStandField(className));
					break;
				}
			}
		}
		if(!found) {
			className = firstUpString(getJavaStandField(className));
		}
		
		return className;
	}
 	
	private final void genStaticImports(String[] tableNames,String domain,String outputDir) {
		BufferedWriter writer = null;
		try {
			File destDir = new File(outputDir+"/src/main/java/"+domain.replace(".", "/")+"/tables/");
			if(!destDir.exists()) {
				destDir.mkdirs();
			}
			
			writer = new BufferedWriter(new FileWriter(destDir+"/"+"StaticImport.java"));
			
			writer.write("package "+domain+".tables;");
			writer.newLine();
			writer.newLine();
			writer.write("import cn.otra.db4j.api.common.DBContext;");
			writer.newLine();
			
			writer.write("import cn.otra.db4j.api.table.Tables;");
			writer.newLine();
			writer.newLine();
			
			writer.write("public class StaticImport extends DBContext {");
			writer.newLine();
			writer.newLine();
			for(String name:tableNames) {
				String className = getClassName(name);
				writer.write("	public static final T"+className+" t"+className+" = Tables.get(T"+className+".class);");
				writer.newLine();
			}
			
			writer.newLine();
			for(String name:tableNames) {
				String className = getClassName(name);
				writer.write("	public static final T"+className+" T"+className+" = Tables.get(T"+className+".class);");
				writer.newLine();
			}
			
			writer.newLine();
			writer.write("}");
			writer.newLine();
			writer.newLine();
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	
	public static List<String> getTableNames() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			//show full fields from car_info; 
			conn = DBUtil.getConnection();
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(null, null, "%", new String[]{ "TABLE" }); 
			List<String> tableNames = new ArrayList<String>();
            while (rs.next()) {  
                String tableName = rs.getString("TABLE_NAME");  //表名  
                tableNames.add(tableName);
            }  
			return tableNames;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn,stmt,rs);
		}
		return null;
	}
	
	
	
	
	private final void genTableClass(String domain,String tableName,String className,String outputDir) throws Exception {
		System.err.println("process table "+tableName);
		className = tableName.toUpperCase();//firstUpString(getJavaStandField(className));
		TableMetaData md = getMetaData(tableName);
		File destDir = new File(outputDir+"/src/main/java/"+domain.replace(".", "/")+"/tables/");
		if(!destDir.exists()) {
			destDir.mkdirs();
		}
		className = "T"+getClassName(tableName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(destDir+"/"+className+".java"));
		writer.write("package "+domain+".tables;");
		writer.newLine();
		writer.write("import cn.otra.db4j.api.table.AllField;");
		writer.newLine();
		writer.write("import cn.otra.db4j.api.table.AbstractTable;");
		writer.newLine();
		
		writer.write("import cn.otra.db4j.api.table.TableField;");
		writer.newLine();
		writer.write("import cn.otra.db4j.api.table.TableFieldImpl;");
		writer.newLine();
		writer.write("import java.util.HashMap;");
		writer.newLine();
		writer.write("import java.util.Map;");
		writer.newLine();
		writer.write("import java.util.Date;");
		writer.newLine();
		
		for(TableMetaDataField f:md.getFields()) {
			if((f.getType().equals("datetime") || f.getType().equals("date") || f.getType().equals("timestamp"))) {
				writer.write("import cn.otra.db4j.api.table.DateTableField;");
				writer.newLine();
				writer.write("import cn.otra.db4j.api.table.DateTableFieldImpl;");
				writer.newLine();
				break;
			}
		}
		writer.newLine();
		
		for(TableMetaDataField f:md.getFields()) {
			String javaType = getJavaType(f.getType());
			if("Integer".equals(javaType) || "Float".equals(javaType) || "Double".equals(javaType) || "Long".equals(javaType)) {
				writer.write("import cn.otra.db4j.api.table.AddAbleTableField;");
				writer.newLine();
				writer.write("import cn.otra.db4j.api.table.AddAbleTableFieldImpl;");
				writer.newLine();
				break;
			}
		}
		
		
		writer.newLine();
		
		//package com.ecarinfo.persist.po;
		writer.write("public class "+className+" extends AbstractTable {");
		writer.newLine();
		writer.newLine();

		writer.write("	static {");
		writer.newLine();
		writer.write("		init();");
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		writer.write("	private "+className+"(){");
		writer.newLine();
		
		writer.write("		super.tableName = \""+tableName+"\";");
		writer.newLine();
		
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		writer.write("	private "+className+"(String aliasName){");
		writer.newLine();
		writer.write("		this();");
		writer.newLine();
		writer.write("		setAliasName(aliasName);");
		writer.newLine();
		
		
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		writer.write("	public static final "+className+" getInstance() {");
		writer.newLine();
		writer.write("		return new "+className+"();");
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		writer.write("	public static final "+className+" getInstance(String aliasName) {");
		writer.newLine();
		writer.write("		return new "+className+"(aliasName);");
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		writer.write("	private static Map<String, String> allFieldMap;// = new HashMap<String, String>();");
		writer.newLine();
		writer.write("	private static final void init() {");
		writer.newLine();
		writer.write("		allFieldMap = new HashMap<String, String>();");
		writer.newLine();
		for(TableMetaDataField f:md.getFields()) {
			String javaField = getJavaStandField(f.getField());
			writer.write("		allFieldMap.put(\""+javaField+"\", \""+f.getField()+"\");");
			writer.newLine();
		}
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		writer.write("	@Override");
		writer.newLine();
		writer.write("	public String getFieldName(String javaFieldName) {");
		writer.newLine();
		writer.write("		return allFieldMap.get(javaFieldName);");
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		
		
		writer.write("	public final TableField<Integer> all = new AllField<Integer>(this,Integer.class,  \"*\",null,false);");
		writer.newLine();
		writer.newLine();

		writer.write("	@Override");
		writer.newLine();
		writer.write("	public TableField<Date> getCreateTimeField() {");
		writer.newLine();
		boolean hasCreateTime = false;
		for(TableMetaDataField f:md.getFields()) {
			if("createTime".equals(f.getJavaField())) {
				hasCreateTime = true;
			}
		}
		if(hasCreateTime) {
			hasCreateTime = true;
			writer.write("		return createTime;");
			writer.newLine();
		} else {
			writer.write("		return null;");
			writer.newLine();
		}
		
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		writer.write("	@Override");
		writer.newLine();
		boolean hasUpdateTime = false;
		writer.write("	public TableField<Date> getUpdateTimeField() {");
		writer.newLine();
		for(TableMetaDataField f:md.getFields()) {
			if("updateTime".equals(f.getJavaField())) {
				hasUpdateTime = true;
			}
		}
		if(hasUpdateTime) {
			writer.write("		return updateTime;");
			writer.newLine();
		} else {
			writer.write("		return null;");
			writer.newLine();
		}
		
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		
		writer.write("	@Override");
		writer.newLine();
		writer.write("	public TableField<?> allField() {");
		writer.newLine();
		writer.write("		return all;");
		writer.newLine();
		writer.write("	}");
		writer.newLine();
		writer.newLine();
		
		try {
			
			int idx = 0;
			for(TableMetaDataField f:md.getFields()) {
				String comment = f.getComment();
				String javaType = getJavaType(f.getType());
				boolean isNumber = false;
				if("Integer".equals(javaType) || "Float".equals(javaType) || "Double".equals(javaType) || "Long".equals(javaType)) {
					isNumber = true;
				}
				
				String javaField = getJavaStandField(f.getField());
				
				writer.write("	/**");
				writer.newLine();
				writer.write("	 *"+comment+" | "+f.getType());
				writer.newLine();
				writer.write("	*/");
				writer.newLine();
				
				comment = encodeString(comment);
				
				if("PRI".equals(f.getKey())) {
					writer.write("	public final TableField<"+javaType+">  pk"+(idx>0?idx:"")+" = new TableFieldImpl<"+javaType+">(this,"+javaType+".class,\""+f.getField()+"\",\""+javaField+"\",\""+comment+"\","+f.isPK()+");");
					idx++;
					writer.newLine();
					
				} 
				if(javaType.equals("Date")) {
					writer.write("	public final DateTableField<"+javaType+"> "+javaField+" = new DateTableFieldImpl<"+javaType+">(this,"+javaType+".class,\""+f.getField()+"\",\""+javaField+"\",\""+comment+"\","+f.isPK()+");");
				} else if(isNumber) {
					writer.write("	public final AddAbleTableField<"+javaType+"> "+javaField+" = new AddAbleTableFieldImpl<"+javaType+">(this,"+javaType+".class,\""+f.getField()+"\",\""+javaField+"\",\""+comment+"\","+f.isPK()+");");
				} else {
					writer.write("	public final TableField<"+javaType+"> "+javaField+" = new TableFieldImpl<"+javaType+">(this,"+javaType+".class,\""+f.getField()+"\",\""+javaField+"\",\""+comment+"\","+f.isPK()+");");
				}
				
				writer.newLine();
				writer.newLine();
			}
			writer.newLine();
			
			
			writer.write("	private final TableField<?>[] allFields = new TableField<?>[] {");
			for(int i=0;i<md.getFields().size();i++) {
				TableMetaDataField f = md.getFields().get(i);
				String javaField = getJavaStandField(f.getField());
				writer.write(javaField);
				if(i<md.getFields().size() - 1) {
					writer.write(",");
				}
			}
			writer.write("};");
			writer.newLine();
			writer.newLine();
			
			writer.write("	@Override");
			writer.newLine();
			writer.write("	public TableField<?>[] getAllFields() {");
			writer.newLine();
			writer.write("		return allFields;");
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			writer.write("	private final TableField<?>[] allUpdateFields = new TableField<?>[] {");
			for(int i=0;i<md.getFields().size();i++) {
				TableMetaDataField f = md.getFields().get(i);
				if(f.isPK()) {
					continue;
				}
				if(f.getJavaField().equals("createTime")) {//创建时间不更新
					continue;
				}
				String javaField = getJavaStandField(f.getField());
				writer.write(javaField);
				if(i<md.getFields().size() - 1) {
					writer.write(",");
				}
			}
			writer.write("};");
			writer.newLine();
			writer.newLine();
			
			writer.write("	@Override");
			writer.newLine();
			writer.write("	public TableField<?>[] getAllUpdateFields() {");
			writer.newLine();
			writer.write("		return allUpdateFields;");
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			
			writer.write("	@SuppressWarnings(\"unchecked\")");
			writer.newLine();
			writer.write("	@Override");
			writer.newLine();	
			writer.write("	public TableField<?> getPK() {");
			writer.newLine();	
			if(md.getPk() == null) {
				writer.write("		return null;");
			} else {
				writer.write("		return pk;");
			}
			
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			
			
			boolean isAutoGeneratePK = md.isAutoincrement();
			if(!isAutoGeneratePK) {
				System.out.println(" ########warn:"+md.getTableName()+"'s pk["+md.getPk()+"] is not autoGeneratePK!");
			}
			writer.write("	@Override");
			writer.newLine();
			writer.write("	public final boolean isAutoGeneratedPK() {");
			writer.newLine();
			writer.write("		return "+isAutoGeneratePK+";");
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			writer.write("	@Override");
			writer.newLine();
			writer.write("	public String toString() {");
			writer.newLine();
			writer.write("		return \""+className+"[table="+tableName+"]\";");
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			writer.write("}");
			writer.newLine();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
		
//		genRMClass(md,outputDir,className);
	}
	
	private static final String encodeString(String value) {
		if(value == null) {
			return value;
		}
		value = value.replace("\"", "\\\"");
		return value;
	}
	
	private final void genPoClassFromTable(String domain,String tableName,String className,String outputDir) throws Exception {
//		className = firstUpString(getJavaStandField(className));
		TableMetaData md = getMetaData(tableName);
		File destDir = new File(outputDir+"/src/main/java/"+domain.replace(".", "/")+"/po/");
		if(!destDir.exists()) {
			destDir.mkdirs();
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(destDir+"/"+className+".java"));
		writer.write("package "+domain+".po;");
		writer.newLine();
		
		writer.write("import "+domain+".tables.T"+className+";");
		writer.newLine();
		
		writer.write("import cn.otra.db4j.api.annotation.Entity;");
		writer.newLine();
		writer.write("import cn.otra.db4j.api.po.BasePo;");
		writer.newLine();
		
		boolean containsDate = false;// 防止重复引入
		for(TableMetaDataField f:md.getFields()) {
			if(!containsDate && (f.getType().equals("datetime") || f.getType().equals("date") || f.getType().equals("timestamp"))) {
				writer.write("import java.util.Date;");
				writer.newLine();
				containsDate = true;
			}
		}
		
		writer.newLine();
		
		if(md.getCommon() != null && !md.getCommon().contains(":")) {
			writer.write("/**");
			writer.newLine();
			writer.write("*  "+md.getCommon());
			writer.newLine();
			writer.write("*");
			writer.newLine();
			writer.write("**/");
			writer.newLine();
		}
		
		writer.write("@Entity(table=T"+className+".class)");
		writer.newLine();
		
		writer.write("public class "+className+" extends BasePo {");
		writer.newLine();
		writer.newLine();
		writer.write("	private static final long serialVersionUID = -2260388125919493487L;");
		writer.newLine();
		
		try {
			for(TableMetaDataField f:md.getFields()) {
				String comment = f.getComment();
				String javaType = getJavaType(f.getType());
				String javaField = getJavaStandField(f.getField());
				Object defVal = f.getDefaultVal();
				String defaultValue = "";
				if(defVal == null) {
					defaultValue = "";
				} else {
					//gen defaultValue
					defaultValue = getDefaultString(javaType,defVal);
					if(defaultValue.length() > 0) {
						defaultValue = " = "+defaultValue;
					}
				}
				if(comment != null && comment.length() > 0) {
					writer.write("	private "+javaType+" "+javaField+defaultValue+";//"+comment);
				} else {
					writer.write("	private "+javaType+" "+javaField+defaultValue+";");
				}
				writer.newLine();
			}
			
			writer.newLine();
			writer.newLine();
			writer.write("	@Override");
			writer.newLine();
			writer.write("	public Class<?> getTableClass() {");
			writer.newLine();
			writer.write("		return T"+className+".class;");
			writer.newLine();
			writer.write("	}");
			writer.newLine();
			writer.newLine();
			
			for(TableMetaDataField f:md.getFields()) {
				String javaType = getJavaType(f.getType());
				String javaField = getJavaStandField(f.getField());
				genGetterAndSetter(writer, javaType, javaField);
			}
			
			
			
			writer.write("}");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
		
	}
	
	private static final String getDefaultString(String javaType,Object value) {
		/**
		 * var4MysqlToJavaMap.put("BIGINT", "Long");
		var4MysqlToJavaMap.put("CHAR", "String");
		var4MysqlToJavaMap.put("INT", "Integer");
		var4MysqlToJavaMap.put("FLOAT", "Float");
		var4MysqlToJavaMap.put("DATETIME", "Date");
		var4MysqlToJavaMap.put("TIMESTAMP", "Date");
		var4MysqlToJavaMap.put("DATE", "Date");
		var4MysqlToJavaMap.put("VARCHAR", "String");
		var4MysqlToJavaMap.put("ENUM", "String");
		var4MysqlToJavaMap.put("TEXT", "String");
		var4MysqlToJavaMap.put("BIT", "Integer");
		var4MysqlToJavaMap.put("BOOL", "Boolean");
		var4MysqlToJavaMap.put("BOOLEAN", "Boolean");
		var4MysqlToJavaMap.put("TINYINT", "Boolean");
		var4MysqlToJavaMap.put("SMALLINT", "Integer");
		var4MysqlToJavaMap.put("DOUBLE", "Double");
		var4MysqlToJavaMap.put("DECIMAL", "Double");
		 */
		if(value == null) {
			return "";
		}
		
		if("Long".equals(javaType)) {
			return value+"l";
		}
		if("String".equals(javaType)) {
			return "\""+value+"\"";
		}
		if("Float".equals(javaType)) {
			return value+"f";
		}
		if("Date".equals(javaType)) {
			return "new Date(0)";
		}
		if("Boolean".equals(javaType)) {
			Integer intVal = Integer.parseInt(value+"");
			if(intVal == null || intVal == 0) {
				return "false";
			}
			return "true";
		}
		if("Double".equals(javaType)) {
			return value+"d";
		}
		return value+"";
	}
	
	public static void main(String[] args) {
		String date = "1970-01-01 00:00:00";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.err.println(dateFormat.format(new Date(0+System.currentTimeMillis())));
	}
}
