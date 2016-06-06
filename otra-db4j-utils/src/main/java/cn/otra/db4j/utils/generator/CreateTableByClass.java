package cn.otra.db4j.utils.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Caret;

public class CreateTableByClass {

	// DROP TABLE IF EXISTS `car_info`;
	// CREATE TABLE `insurance_query_accident` (
	// `id` bigint(20) NOT NULL AUTO_INCREMENT,
	// `query_id` bigint(20) NOT NULL,
	// `accident_id` bigint(20) NOT NULL,
	// `create_time` datetime NOT NULL,
	// `update_time` datetime NOT NULL,
	// PRIMARY KEY (`id`)
	// ) ENGINE=InnoDB DEFAULT CHARSET=utf8

	private static final String DROP_SQL = "DROP TABLE IF EXISTS `";

	public static final String getMySqlTableSqlByClass(Class<?> clazz, String tableName, String tableComment,File javaSourceFile) {
		StringBuilder builder = new StringBuilder();
		builder.append(DROP_SQL).append(tableName).append("`;\r\n");
		builder.append("CREATE TABLE `").append(tableName).append("` (\r\n");
		Field[] fields = clazz.getDeclaredFields();
		Map<String,String> comments = new HashMap<String, String>();
		if(javaSourceFile != null && javaSourceFile.exists()) {
			comments = getCommentMap(fields, javaSourceFile);
		}
		boolean hasId = false;
		boolean isNumberId =false;
		for(int i=0;i<fields.length;i++) {
			Field field = fields[i];
			String name = field.getName();
			if(name.trim().equals("id")) {
				hasId = true;
				if (Number.class.isAssignableFrom(field.getType())) {
					isNumberId = true;
				}
			}
			String mysqlType = MappingUtil.getMySqlType(field.getType().getSimpleName());
			if(mysqlType == null) {
				continue;
			}
			builder.append("  ").append(getSqlFieldFromJavaField(field)).append(" ");
			builder.append(mysqlType).append(" NULL DEFAULT NULL");
			if(name.trim().equals("id") && isNumberId) {
				builder.append(" AUTO_INCREMENT COMMENT '表的主键'");
			} else {
				builder.append(" COMMENT '").append(comments.get(field.getName())).append("'");
			}
			builder.append(",\r\n");
		}
		builder.deleteCharAt(builder.lastIndexOf(",\r\n"));
		if(hasId) {
			builder.append(",\r\n");
			builder.append("  ").append("PRIMARY KEY (`id`)\r\n");
		}
		builder.append(") ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci COMMENT='"
				+ tableComment + "'\r\n");
		if(isNumberId) {
			builder.append("AUTO_INCREMENT=1;");
		}
		return builder.toString();
	}
	
	private static final Map<String,String> getCommentMap(Field[] fields,File javaSourceFile) {
		Map<String,String> map = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			List<String> lines = new ArrayList<String>();
			reader = new BufferedReader(new FileReader(javaSourceFile));
			String tmp = null;
			while((tmp = reader.readLine()) != null) {
				tmp = tmp.trim();
				if (tmp.length() == 0) {
					continue;
				}
				if(!tmp.startsWith("private")) {
					continue;
				}
				if(!tmp.contains("//")) {
					continue;
				}
				lines.add(tmp);
			}
			for(Field field:fields) {
				for(String line:lines) {
					if(line.indexOf(field.getName()+";") == -1) {
						continue;
					}
					String comment = line.substring(line.indexOf("//")+2);
					map.put(field.getName(), comment);
				}
			}
		} catch (Exception e) {
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return map;
	}

	private static final String getSqlFieldFromJavaField(Field javaField) {
		String name = javaField.getName();
		char[] chars = name.toCharArray();
		StringBuilder builder = new StringBuilder();
		for(char c:chars) {
			if(c >= 'A' && c <= 'Z') {
				builder.append("_").append((char)(c+32));
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		String javaSourceFile = "E:/workspace/otra-db4j/otra-db4j-utils/src/main/java/cn/otra/db4j/utils/generator/CreateTableByClass.java";
		
		System.err.println(getMySqlTableSqlByClass(Foo.class, "foo_info", "测试表",new File(javaSourceFile)));
		
	}

	static class Foo {
		private Integer id;
		private String name;//姓名
		private Date createTime;//创建时间
		private Double money;
		private Float fix;
		private Caret a;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Double getMoney() {
			return money;
		}

		public void setMoney(Double money) {
			this.money = money;
		}

		public Float getFix() {
			return fix;
		}

		public void setFix(Float fix) {
			this.fix = fix;
		}

	}
}
