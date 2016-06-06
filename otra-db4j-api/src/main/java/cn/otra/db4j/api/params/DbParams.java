package cn.otra.db4j.api.params;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据字段串进行快速单表查询
 * 
 * @author xiaodx
 * 
 */
public class DbParams implements Serializable {

	private static final long serialVersionUID = 3758522251922484560L;
	private Map<String, Object> eqMap;
	private Map<String, Object> neMap;
	private Map<String, Object> gtMap;
	private Map<String, Object> geMap;
	private Map<String, Object> leMap;
	private Map<String, Object> ltMap;
	private Map<String, Object> nullMap;
	private Map<String, Object> notNullMap;
	private Map<String, Object> likeMap;
	private Map<String, Object> likeEndWithMap;
	private Map<String, Object> likeStartWithMap;
	private Map<String, Object> containMap;
	private Map<String, Object> orderByMap;

	public static final DbParams getInstance() {
		return new DbParams();
	}

	public final DbParams eq(String key, Object value) {
		if (eqMap == null) {
			eqMap = new HashMap<String, Object>();
		}
		eqMap.put(key, value);
		return this;
	}

	public final DbParams ne(String key, Object value) {
		if (neMap == null) {
			neMap = new HashMap<String, Object>();
		}
		neMap.put(key, value);
		return this;
	}

	public final DbParams gt(String key, Object value) {
		if (gtMap == null) {
			gtMap = new HashMap<String, Object>();
		}
		gtMap.put(key, value);
		return this;
	}

	public final DbParams ge(String key, Object value) {
		if (geMap == null) {
			geMap = new HashMap<String, Object>();
		}
		geMap.put(key, value);
		return this;
	}

	public final DbParams le(String key, Object value) {
		if (leMap == null) {
			leMap = new HashMap<String, Object>();
		}
		leMap.put(key, value);
		return this;
	}

	public final DbParams lt(String key, Object value) {
		if (ltMap == null) {
			ltMap = new HashMap<String, Object>();
		}
		ltMap.put(key, value);
		return this;
	}

	public final DbParams isNull(String key) {
		if (nullMap == null) {
			nullMap = new HashMap<String, Object>();
		}
		nullMap.put(key, "true");
		return this;
	}

	public final DbParams isNotNull(String key) {
		if (notNullMap == null) {
			notNullMap = new HashMap<String, Object>();
		}
		notNullMap.put(key, "true");
		return this;
	}

	public final DbParams like(String key, Object value) {
		if (likeMap == null) {
			likeMap = new HashMap<String, Object>();
		}
		likeMap.put(key, value);
		return this;
	}

	public final DbParams likeStartWith(String key, Object value) {
		if (likeStartWithMap == null) {
			likeStartWithMap = new HashMap<String, Object>();
		}
		likeStartWithMap.put(key, value);
		return this;
	}

	public final DbParams likeEndWith(String key, Object value) {
		if (likeEndWithMap == null) {
			likeEndWithMap = new HashMap<String, Object>();
		}
		likeEndWithMap.put(key, value);
		return this;
	}

	public final DbParams contain(String key, Object value) {
		if (containMap == null) {
			containMap = new HashMap<String, Object>();
		}
		containMap.put(key, value);
		return this;
	}
	
	public final DbParams orderByDesc(String key) {
		if (orderByMap == null) {
			orderByMap = new HashMap<String, Object>();
		}
		orderByMap.put(key, "DESC");
		return this;
	}
	
	public final DbParams orderByAsc(String key) {
		if (orderByMap == null) {
			orderByMap = new HashMap<String, Object>();
		}
		orderByMap.put(key, "ASC");
		return this;
	}

	//getter setter
	public Map<String, Object> getEqMap() {
		return eqMap;
	}

	public void setEqMap(Map<String, Object> eqMap) {
		this.eqMap = eqMap;
	}

	public Map<String, Object> getNeMap() {
		return neMap;
	}

	public void setNeMap(Map<String, Object> neMap) {
		this.neMap = neMap;
	}

	public Map<String, Object> getGtMap() {
		return gtMap;
	}

	public void setGtMap(Map<String, Object> gtMap) {
		this.gtMap = gtMap;
	}

	public Map<String, Object> getGeMap() {
		return geMap;
	}

	public void setGeMap(Map<String, Object> geMap) {
		this.geMap = geMap;
	}

	public Map<String, Object> getLeMap() {
		return leMap;
	}

	public void setLeMap(Map<String, Object> leMap) {
		this.leMap = leMap;
	}

	public Map<String, Object> getLtMap() {
		return ltMap;
	}

	public void setLtMap(Map<String, Object> ltMap) {
		this.ltMap = ltMap;
	}

	public Map<String, Object> getNullMap() {
		return nullMap;
	}

	public void setNullMap(Map<String, Object> nullMap) {
		this.nullMap = nullMap;
	}

	public Map<String, Object> getNotNullMap() {
		return notNullMap;
	}

	public void setNotNullMap(Map<String, Object> notNullMap) {
		this.notNullMap = notNullMap;
	}

	public Map<String, Object> getLikeMap() {
		return likeMap;
	}

	public void setLikeMap(Map<String, Object> likeMap) {
		this.likeMap = likeMap;
	}

	public Map<String, Object> getLikeEndWithMap() {
		return likeEndWithMap;
	}

	public void setLikeEndWithMap(Map<String, Object> likeEndWithMap) {
		this.likeEndWithMap = likeEndWithMap;
	}

	public Map<String, Object> getLikeStartWithMap() {
		return likeStartWithMap;
	}

	public void setLikeStartWithMap(Map<String, Object> likeStartWithMap) {
		this.likeStartWithMap = likeStartWithMap;
	}

	public Map<String, Object> getContainMap() {
		return containMap;
	}

	public void setContainMap(Map<String, Object> containMap) {
		this.containMap = containMap;
	}

	public Map<String, Object> getOrderByMap() {
		return orderByMap;
	}

	public void setOrderByMap(Map<String, Object> orderByMap) {
		this.orderByMap = orderByMap;
	}

}
