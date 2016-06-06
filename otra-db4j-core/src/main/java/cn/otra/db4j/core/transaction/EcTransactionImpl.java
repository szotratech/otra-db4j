package cn.otra.db4j.core.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import cn.otra.db4j.api.ctx.DB;
import cn.otra.db4j.api.transaction.EcTransaction;

public class EcTransactionImpl implements EcTransaction {
	
	public EcTransactionImpl(DB db) {
		defaultTransactionDefinition = new DefaultTransactionDefinition();
		dataSourceTransactionManager = new DataSourceTransactionManager(db.getDataSource());
	}
	
	private DataSourceTransactionManager dataSourceTransactionManager;
	private DefaultTransactionDefinition defaultTransactionDefinition;
	private TransactionStatus transactionStatus;

	public void beginTrans() {
		transactionStatus = dataSourceTransactionManager.getTransaction(defaultTransactionDefinition);
	}
	
	public void rollback() {
		dataSourceTransactionManager.rollback(transactionStatus);
	}
	
	public void commit() {
		dataSourceTransactionManager.commit(transactionStatus);
	}
	
}
