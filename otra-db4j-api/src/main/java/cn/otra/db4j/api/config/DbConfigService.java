package cn.otra.db4j.api.config;

import java.util.Map;

public interface DbConfigService {

	String getVal(String key);
	
	Map<String, String> getVal();
	
	Integer getInt(String key);
	
	Long getLong(String key);
	
	String getString(String key);
	
	Boolean getBoolean(String key);
	
}
