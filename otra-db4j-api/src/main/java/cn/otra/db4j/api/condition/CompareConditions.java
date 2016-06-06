package cn.otra.db4j.api.condition;

import cn.otra.db4j.api.common.Comparator;
import cn.otra.db4j.api.table.TableField;


public class CompareConditions {
//	private static final Logger logger = Logger.getLogger(CompareCondition.class);
//	private static final int capacity = 1;
//	private static final ArrayBlockingQueue<CompareCondition<?>> QUEUE = new ArrayBlockingQueue<CompareCondition<?>>(capacity);
	
	public static final <T> CompareCondition<T> get(TableField<T> field, Comparator comparator, Object rightParameter) {
		return new CompareCondition<T>(field, comparator, rightParameter);
//		@SuppressWarnings("unchecked")
//		CompareCondition<T> condition = (CompareCondition<T>) QUEUE.poll();
//		if (condition == null) {
//			condition = new CompareCondition<T>(field, comparator, rightParameter);
//			if (logger.isInfoEnabled()) {
//				logger.info(">>>>>> create CompareCondition.");
//			}
//		} else {
//			condition.init(field, comparator, rightParameter);
//			if (logger.isInfoEnabled()) {
//				logger.info(">>>>>> get CompareCondition from cache.");
//			}
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug(">>>>>> getCompareCondition queue.size="+QUEUE.size());
//		}
//		return condition;
	}

	public static final <T> void free(CompareCondition<T> condition) {
//		if(QUEUE.remainingCapacity() <= 0) {
//			return ;
//		}
//		condition.reset();
//		condition.setField(null);
//		condition.setComparator(null);
//		condition.setRightParameter(null);
//		QUEUE.offer(condition);
//		if (logger.isDebugEnabled()) {
//			logger.debug("freeCompareCondition queue.size="+QUEUE.size());
//		}
	}
	
}
