//package cn.otra.db4j.core.paginate;
//
//import java.io.Serializable;
//
//import cn.otra.commons.model.ECPage;
//import cn.otra.commons.model.PageIterator;
//import cn.otra.db4j.api.builder.WhereCondition;
//import cn.otra.db4j.api.ctx.BuilderContext;
//import cn.otra.db4j.api.ctx.OO4DB;
//
//public class PageIteratorImpl<T extends Serializable> implements PageIterator<T> {
//	
//	private Class<T> tClass;
//	private boolean useBuilderContext;
//	private BuilderContext builderContext;
//	private WhereCondition whereCondition;
//	private OO4DB oo4db;
//	private ECPage<T> ecPage; // 当前页
//	private int idx = 0;
//	
//	public PageIteratorImpl(Class<T> tClass, BuilderContext builderContext,
//			WhereCondition whereCondition, OO4DB oo4db, ECPage<T> ecPage,boolean useBuilderContext) {
//		super();
//		this.tClass = tClass;
//		this.builderContext = builderContext;
//		this.whereCondition = whereCondition;
//		this.oo4db = oo4db;
//		this.ecPage = ecPage;
//		this.useBuilderContext = useBuilderContext;
//	}
//
//	@Override
//	public boolean hasNext() {
//		if(idx == 0) {
//			return true;
//		}
//		return !ecPage.isLastPage();
//	}
//	
//	@Override
//	public ECPage<T> next() {
//		if(idx == 0) {
//			idx++;
//			return ecPage;
//		}
//		if(ecPage.isLastPage()) {
//			return null;
//		}
//		
//		if(useBuilderContext) {
//			ecPage = builderContext.queryPage(tClass, ecPage.getCurrentPage()+1, ecPage.getRowsPerPage());
//		} else {
//			ecPage = oo4db.findPage(tClass, whereCondition, ecPage.getCurrentPage()+1, ecPage.getRowsPerPage());
//		}
//		idx++;
//		return ecPage;
//	}
//	
//}
