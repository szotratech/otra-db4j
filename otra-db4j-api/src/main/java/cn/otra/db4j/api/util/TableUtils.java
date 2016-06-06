package cn.otra.db4j.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.table.Tables;

public class TableUtils {
	//缓存反射，以最大限度降低调用开销
	private static final Map<Class<?>,Field[]> fieldMap = new HashMap<Class<?>,Field[]>();
	private static final Map<Class<?>,Map<String,String>> mysqlFieldMap = new HashMap<Class<?>,Map<String,String>>();
	private static final Map<Class<?>,TypeEnum> TYPE_MAP = new HashMap<Class<?>,TypeEnum>();
	private static final Map<Class<?>, Map<String, Field>> nameFieldMap = new HashMap<Class<?>, Map<String,Field>>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	enum TypeEnum {
		//_short,_int,_long,_float,_double,_boolean,Short,Integer,Long,Float,Double,Boolean,String,Date,Object
		Short,Integer,Long,Float,Double,Boolean,String,Date,Object
	}
	static void init() {
//		TYPE_MAP.put(short.class, TypeEnum._short);
//		TYPE_MAP.put(int.class, TypeEnum._int);
//		TYPE_MAP.put(long.class, TypeEnum._long);
//		TYPE_MAP.put(float.class, TypeEnum._float);
//		TYPE_MAP.put(double.class, TypeEnum._double);
//		TYPE_MAP.put(boolean.class, TypeEnum._boolean);
		TYPE_MAP.put(Short.class, TypeEnum.Short);
		TYPE_MAP.put(Integer.class, TypeEnum.Integer);
		TYPE_MAP.put(Long.class, TypeEnum.Long);
		TYPE_MAP.put(Float.class, TypeEnum.Float);
		TYPE_MAP.put(Double.class, TypeEnum.Double);
		TYPE_MAP.put(Boolean.class, TypeEnum.Boolean);
		TYPE_MAP.put(String.class, TypeEnum.String);
		TYPE_MAP.put(Date.class, TypeEnum.Date);
		TYPE_MAP.put(Object.class, TypeEnum.Object);
	}
	static {
		init();
	}
	
	public static final <T> Field[] getFields(Class<T> entityClass,boolean withSuperClass) {
		Field[] fs = fieldMap.get(entityClass);
		
		if(fs == null) {
			fs = ReflectUtil.getFields(entityClass);
			
			//缓存反射方法getDeclaredFields的调用结果
			fieldMap.put(entityClass, fs);
			//缓存javaType<-->mysqlType名称对应,如:userName<-->user_name
			if(mysqlFieldMap.get(entityClass) == null) {
				Map<String,String> fm = new HashMap<String, String>();
				mysqlFieldMap.put(entityClass, fm);
				for(Field f:fs) {
					if(!Modifier.isFinal(f.getModifiers())) {
						String name = getMysqlStandField(f.getName());
						fm.put(f.getName(), name);
					}
				}
			}
			
		}
		return fs;
	}
	
	
	/**
	 * 将一个类的的属性名和属性封装到一个MAP中并返回
	 * @param entityClass
	 * @return
	 */
	public static final <T> Map<String, Field> getDeclaredFieldMap(Class<T> entityClass) {
		Map<String, Field> map = nameFieldMap.get(entityClass);
		if(map == null) {
			map = new HashMap<String, Field>();
			nameFieldMap.put(entityClass, map);
			Field[] fs = getFields(entityClass, true);
			for(Field f:fs) {
				map.put(f.getName(), f);
			}
		}
		return map;
	}
	
	public static final <T> Field getDeclaredField(Class<T> entityClass,String fieldName) {
		Map<String, Field> map = getDeclaredFieldMap(entityClass);
		return map.get(fieldName);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T map2Entity(Map<String,Object> map,Class<T> entityClass) {
		T t = null;
		try {
			//基本数据类型
			if(Number.class.isAssignableFrom(entityClass) || entityClass == String.class || Date.class.isAssignableFrom(entityClass)) {
				for(Map.Entry<String, Object> en:map.entrySet()) {
					t = (T)getRealValue(entityClass, en.getValue());
					break;
				}
				return t;
			}
			t = entityClass.newInstance();
			
			Field[] fs = getFields(entityClass, true);
			
			for(Field f:fs) {
				if(Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				f.setAccessible(true);
				String fieldName = f.getName();
				String key = mysqlFieldMap.get(entityClass).get(fieldName);
				if(key == null) {
					continue;
				}
				Object value = map.get(key);
				if(value == null) {
					value = map.get(fieldName);
				}
				f.set(t, getRealValue(f,value));
			}
		} catch (Exception e) {
			throw new RuntimeException("map2Entity"+map+","+entityClass.getName()+","+mysqlFieldMap,e);
		} 
		return t;
	}
	
	public static final Object getRealValue(Field field,Object value) {
		if(value == null) {
			TypeEnum key = TYPE_MAP.get(field.getType());
			if(key == null) {
				return null;
			}
			switch (key) {
//				case _int:
//					return 0;
//				case _long:
//					return 0l;
//				case _float:
//					return 0.0f;
//				case _double:
//					return 0.0;
//				case _boolean:
//					return false;
				default:
					return null;
			}
//			return null;
		} else {
			if(value instanceof String) {
				return getValueByClass(field.getType(),value.toString());
			}
			if(value instanceof Number) {
				return getValueByClass(field.getType(),(Number)value);
			}
			if(value instanceof Date) {
				if(field.getType() == String.class) {
					if(value instanceof Timestamp) {
						return DATETIME_FORMAT.format((Date)value);
					} else {
						return DATE_FORMAT.format((Date)value);
					}
				} else {
					return (Date)value;
				}
			}
			if(value instanceof Boolean) {
				return (Boolean)value;
			}
		}
		
		return null;
	}
	
	public static final Object getRealValue(Class<?> clazz,Object value) {
		if(value == null) {
			switch (TYPE_MAP.get(clazz)) {
//				case _int:
//					return 0;
//				case _long:
//					return 0l;
//				case _float:
//					return 0.0f;
//				case _double:
//					return 0.0;
//				case _boolean:
//					return false;
				default:
					return null;
			}
		} else {
			if(value instanceof String) {
				return getValueByClass(clazz,value.toString());
			}
			if(value instanceof Number) {
				return getValueByClass(clazz,(Number)value);
			}
			if(value instanceof Date) {
				if(clazz == String.class) {
					if(value instanceof Timestamp) {
						return DATETIME_FORMAT.format((Date)value);
					} else {
						return DATE_FORMAT.format((Date)value);
					}
				} else {
					return (Date)value;
				}
			}
			if(value instanceof Boolean) {
				return (Boolean)value;
			}
		}
		
		return null;
	}
	
	private static final Object getValueByClass(Class<?> clazz,String value) {
//		System.err.println("clazz="+clazz.getSimpleName());
		TypeEnum type = TYPE_MAP.get(clazz);
		switch (type) {
		case Integer:
			return Integer.parseInt(value);
		case Long:
			return Long.parseLong(value);
		case Float:
			return Float.parseFloat(value);
		case Double:
			return Double.parseDouble(value);
		case Boolean:
			return Boolean.parseBoolean(value);
		case String:
			return value;
		case Object:
			return value;
		default:
			break;
		}
		return null;
	}
	
	private static final Object getValueByClass(Class<?> clazz,Number value) {
//		System.err.println("clazz="+clazz.getSimpleName());
		TypeEnum type = TYPE_MAP.get(clazz);
		switch (type) {
		case Integer:
			return value.intValue();
//		case _int:
//			return value == null?0:value.intValue();
		case Long:
//		case _long:
			return value.longValue();
		case Float:
//		case _float:
			return value.floatValue();
		case Double:
//		case _double:
			return value.doubleValue();
		case Boolean:
//		case _boolean:
			return value.intValue()==1;
		case String:
			return value.toString();
		case Object:
			return value;
		default:
			break;
		}
		return null;
	}
	
	public static final <T> List<T> map2List(List<Map<String,Object>> list,Class<T> entityClass) {
		List<T> nList = new ArrayList<T>();
		for(Map<String,Object> map:list) {
			nList.add(map2Entity(map,entityClass));
		}
		return nList;
	}
	
	public static final String getMysqlStandField(String javaField) {
		char [] chars = javaField.toCharArray();
		int count = 0;
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if(c >= 'A' && c <='Z') {
				count += 2;
			} else {
				count ++;
			}
		}
		
		char [] dest = new char[count];
		int index = 0;
		for(int i=0;i<chars.length;i++) {
			char c = chars[i];
			if(c >= 'A' && c <='Z') {
				count += 2;
				dest[index++] = '_';
				dest[index++] = (char)(c+32);
			} else {
				dest[index++] = c;
			}
		}
		return new String(dest);
	}
	
	public static void main(String[] args) {
//		String f = getMysqlStandField("email0");
//		System.err.println(f);
		System.err.println(Number.class.isAssignableFrom(Long.class));
	}
	
	public static final <T extends Table> Map<String, String> getComments (Class<T> entityClass) {
		Table table = Tables.get(entityClass);
		TableField<?> [] tableFields = table.getAllFields();
		Map<String, String> map = new HashMap<String, String>();
		for(TableField<?> field:tableFields) {
			String commen = field.getComment();
			if (commen == null || commen.trim().length() == 0) {
				commen = field.getName();
			}
			map.put(field.getJavaName(), field.getComment());
		}
		return map;
	}
}
