package cn.otra.db4j.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.util.TableUtils;

public class RowMapperUtils {
	public static final <T> MyRowMapper<T> getSimpleRowMapper(
			Class<T> entityClass, Table table) {
		return new MyRowMapper<T>(entityClass, table);
	}

	public static class MyRowMapper<T> implements RowMapper<T> {
		private Class<T> entityClass;
		private Table table;

		public MyRowMapper(Class<T> entityClass, Table table) {
			this.entityClass = entityClass;
			this.table = table;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			T t = null;
			try {
				if (Number.class.isAssignableFrom(entityClass)) {
					t = (T) TableUtils.getRealValue(entityClass, rs.getObject(1));
					return t;
				}
				if(entityClass == String.class) {
					t = (T) TableUtils.getRealValue(entityClass, rs.getObject(1));
					return t;
				}
				t = entityClass.newInstance();
				Field[] fs = TableUtils.getFields(entityClass, true);
				for (Field f : fs) {
					f.setAccessible(true);
					if (Modifier.isFinal(f.getModifiers())) {
						continue;
					}
					if (Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					String column = null;
					if (table != null) {
						column = table.getFieldName(f.getName());
						// 如果字段属性不存在，则跳过赋值
						if (column == null) {
							continue;
						}
						try {
							f.set(t,
									TableUtils.getRealValue(f,
											rs.getObject(column)));
						} catch (Exception e) {
							// e.printStackTrace();
						}
					} else {
						column = TableUtils.getMysqlStandField(f.getName());
						try {
							f.set(t,
									TableUtils.getRealValue(f,
											rs.getObject(column)));
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return t;
		}

	}
}
