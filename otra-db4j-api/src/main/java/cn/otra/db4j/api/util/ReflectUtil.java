package cn.otra.db4j.api.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtil {

	/**
	 * 获取给定对象的所有属性，包括父类属性
	 * @param value
	 * @return
	 */
	public static final Field[] getFields (Class<?> entityClass) {
		Field[] fs = entityClass.getDeclaredFields();
		
		//如果是继承关系的话，把父类的属性也加进来，目前只支持一层父类
		Class<?> pClass = entityClass.getSuperclass();
		Field[] pfs = null;
		if(pClass != Object.class) {
			pfs = pClass.getDeclaredFields();
		}
		
		if(pfs != null) {
			Field[] temp = entityClass.getDeclaredFields();
			fs = new Field[pfs.length+temp.length];
			System.arraycopy(pfs, 0, fs, 0, pfs.length);
			System.arraycopy(temp, 0, fs, pfs.length, temp.length);
		}
		return fs;
	}
	
	public static final Map<String, Field> getFieldMap (Class<?> entityClass) {
		Field[] fs = getFields(entityClass);
		Map<String, Field> map = new HashMap<String, Field>();
		for(Field f:fs) {
			map.put(f.getName(), f);
		}
		return map;
	}
}
