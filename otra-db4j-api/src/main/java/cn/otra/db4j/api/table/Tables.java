package cn.otra.db4j.api.table;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.otra.db4j.api.annotation.Entity;

public class Tables {
	private static final Logger logger = Logger.getLogger(Tables.class);
	private static final Map<Class<?>, Method> METHOD_MAP = new ConcurrentHashMap<Class<?>, Method>();
	
	private static final Map<Class<?>,Map<String, Table>> ALIAS_TABLE_MAP = new ConcurrentHashMap<Class<?>, Map<String, Table>>();
	private static final Map<Class<?>, Table> READ_ONLY_MAP = new ConcurrentHashMap<Class<?>, Table>();
	
	private static final <T extends Table> Table newTable(Class<T> tableClass,String aliasName) {
		try {
			Method method = METHOD_MAP.get(tableClass);//缓存方法，以减轻反射的开销
			if(method == null) {
				method = tableClass.getMethod("getInstance");
				METHOD_MAP.put(tableClass, method);
			}
			Table table = (Table)method.invoke(tableClass);
			logger.info(">>>>>> create Table["+tableClass.getSimpleName()+"]"+(aliasName==null?"":" as "+aliasName));
			if(aliasName != null && aliasName.trim().length() > 0) {
				((AbstractTable)table).setAliasName(aliasName);
			}
			return table;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Table> T get(Class<T> tableClass,String alias) {
		try {
			if(alias == null) {
				return get(tableClass);
			}
			
			Map<String, Table> aliasMap = ALIAS_TABLE_MAP.get(tableClass);
			
			boolean fromCache = true;
			if(aliasMap == null) {
				aliasMap = new ConcurrentHashMap<String, Table>();
				ALIAS_TABLE_MAP.put(tableClass, aliasMap);
				fromCache = false;
			}
			
			Table table = aliasMap.get(alias);
			
			if(table == null) {
				table = newTable(tableClass,alias);
				aliasMap.put(alias, table);
				fromCache = false;
			}
			((AbstractTable)table).setLastUpdateTime(System.currentTimeMillis());
			if (logger.isDebugEnabled()) {
				logger.debug(">>>>>> Alias Table's size = "+ALIAS_TABLE_MAP.size());
			}
			if(fromCache) {
				if (logger.isDebugEnabled()) {
					logger.debug(">>>>>> get "+tableClass.getSimpleName()+" from Cache.");
				}
			}
			return (T)table;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Table> T get(Class<T> tableClass) {
		Table table = READ_ONLY_MAP.get(tableClass);
		if(table == null) {
			table = newTable(tableClass,null);
			READ_ONLY_MAP.put(tableClass, table);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(">>>>>> Read Only Table's size = "+READ_ONLY_MAP.size());
		}
		return (T)table;
	}
	
	public static <T extends Serializable> Table getFromPo(Class<T> poClass) {
		Entity t = poClass.getAnnotation(Entity.class);
		Table table = null;
		if(t != null) {
			table = Tables.get(t.table().asSubclass(Table.class));
			if(table == null) {
				if (table == null) {
					throw new RuntimeException(poClass.getName() + " not found !");
				}
			}
		}
		return table;
	}
	
	public static <T extends Serializable> Table getFromPo(Class<T> poClass,String alias) {
		Entity t = poClass.getAnnotation(Entity.class);
		Table table = null;
		if(t != null) {
			table = Tables.get(t.table().asSubclass(Table.class),alias);
			if(table == null) {
				if (table == null) {
					throw new RuntimeException(poClass.getName() + " not found !");
				}
			}
		}
		return table;
	}
	
	
}
