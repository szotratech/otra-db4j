package cn.otra.db4j.api.transaction;

public interface EcTransaction {
	
	void beginTrans();
	
	public void rollback();
	
	public void commit();
	
}
