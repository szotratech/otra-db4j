package cn.otra.db4j.api.builder;

public interface LimitBuilder extends Db4jBuilder {

	EndBuilder limit(Integer offset,Integer rows);
	
	EndBuilder limit(Integer rows);
	
}
