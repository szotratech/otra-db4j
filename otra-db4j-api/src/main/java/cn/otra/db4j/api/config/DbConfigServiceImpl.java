package cn.otra.db4j.api.config;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import cn.otra.db4j.api.annotation.Entity;
import cn.otra.db4j.api.ctx.DB;
import cn.otra.db4j.api.po.BasePo;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.table.Tables;

public class DbConfigServiceImpl implements DbConfigService {

	protected DB db;
	private String poClass;
	private String keyName = "id";
	private String valueName = "value";
	private Integer reloadSecond = 30;

	private Map<String, String> val = new ConcurrentHashMap<String, String>();
	private Lock lock = new ReentrantLock();
	private static final Logger LOG = Logger.getLogger(DbConfigServiceImpl.class);
	private Date lastUpdateTime;
	private static String startLoadSql = "select * from %s where enable=1 and update_time > ?";
	private static String sql = "select * from %s where update_time > ?";
	private Class<BasePo> poClazz;// = (Class<BasePo>) Class.forName(poClass);
	private Entity entity;// = poClazz.getAnnotation(Entity.class);
	private Table table;// = Tables.get(entity.table());
	private TableField<String> keyField = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private void loadFromDb(Boolean isStartLoad) throws Exception {
		lock.lock();
		try {
			Map<String, BasePo> map = null;
			if(isStartLoad) {
				map = db.findObjectsForMap(poClazz, keyField, startLoadSql, lastUpdateTime);
			} else {
				map = db.findObjectsForMap(poClazz, keyField, sql, lastUpdateTime);
			}
			if(map.size() == 0) {
				return;
			}
			Field idField = null;
			Field valueField = null;
			Field updateTimeField = null;
			Field enableField = null;
			for (Map.Entry<String, BasePo> en:map.entrySet()) {
				BasePo configInfo = en.getValue();
				if (idField == null) {
					idField = configInfo.getClass().getDeclaredField(keyName);
					valueField = configInfo.getClass().getDeclaredField(valueName);
					updateTimeField = configInfo.getClass().getDeclaredField("updateTime");
					enableField = configInfo.getClass().getDeclaredField("enable");
					idField.setAccessible(true);
					valueField.setAccessible(true);
					updateTimeField.setAccessible(true);
					enableField.setAccessible(true);
				}
				String key = (String) idField.get(configInfo);
				String value = (String) valueField.get(configInfo);
				Boolean enable = (Boolean) enableField.get(configInfo);
				if(enable) {
					if(val.get(key) != null) {
						LOG.info(">>>>>>>>update config key=["+key+"],value=["+value+"]");
					} else {
						LOG.info(">>>>>>>>add config key=["+key+"],value=["+value+"]");
					}
					val.put(key,value);
				} else {
					val.remove(key);
					LOG.info(">>>>>>>>del config key=["+key+"],value=["+value+"]");
				}
				
				Date poUpdateTime = (Date)updateTimeField.get(configInfo);
				if(lastUpdateTime.getTime() < poUpdateTime.getTime()) {
					lastUpdateTime = poUpdateTime;
				}
			}
			LOG.info(">>>>>>>>config.size=["+val.size()+"]");
			LOG.info(">>>>>>>>lastUpdateTime=["+dateFormat.format(lastUpdateTime)+"]");
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public void init() {
		lock.lock();
		try {
			poClazz = (Class<BasePo>) Class.forName(poClass);
			entity = poClazz.getAnnotation(Entity.class);
			table = Tables.get(entity.table());
			if(lastUpdateTime == null) {
				lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01");
			}
			
			TableField<?>[] tableFields = table.getAllFields();
			for(TableField<?> tableField:tableFields) {
				if(tableField.getName().equals(keyName)) {
					keyField = (TableField<String>)tableField;
					break;
				}
			}
			
			startLoadSql = String.format(startLoadSql, table.getTableName());
			sql = String.format(sql, table.getTableName());
			
			loadFromDb(true);
			new Thread(new Runnable() {
				@Override
				public void run() {

					while (true) {
						try {
							Thread.sleep(reloadSecond*1000);
							loadFromDb(false);
						} catch (Exception e) {
							LOG.error("", e);
						}
					}
				}
			}).start();
		} catch (Exception e) {
			LOG.error("", e);
			throw new RuntimeException("", e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getVal(String key) {
		lock.lock();
		try {
			String value = val.get(key);
			if (value == null) {
				throw new RuntimeException("no propertie [" + key
						+ "] found in db.configuration.");
			}
			return value;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<String, String> getVal() {
		return val;
	}

	@Override
	public Integer getInt(String key) {
		return Integer.valueOf(getVal(key));
	}

	@Override
	public Long getLong(String key) {
		return Long.valueOf(getVal(key));
	}

	@Override
	public String getString(String key) {
		return getVal(key);
	}

	@Override
	public Boolean getBoolean(String key) {
		return Boolean.valueOf(getVal(key));
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void setPoClass(String poClass) {
		this.poClass = poClass;
	}

	public void setReloadSecond(Integer reloadSecond) {
		this.reloadSecond = reloadSecond;
	}
}
