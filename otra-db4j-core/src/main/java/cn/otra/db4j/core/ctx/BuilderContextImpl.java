package cn.otra.db4j.core.ctx;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.otra.commons.model.ECPage;
import cn.otra.db4j.api.builder.EndBuilder;
import cn.otra.db4j.api.builder.FromBuilder;
import cn.otra.db4j.api.builder.HavingBuilder;
import cn.otra.db4j.api.builder.JoinBuilder;
import cn.otra.db4j.api.builder.OrderByBuilder;
import cn.otra.db4j.api.builder.SetBuilder;
import cn.otra.db4j.api.builder.SortTypeBuilder;
import cn.otra.db4j.api.builder.TableBuilder;
import cn.otra.db4j.api.condition.AddCondition;
import cn.otra.db4j.api.condition.Condition;
import cn.otra.db4j.api.condition.LimitCondition;
import cn.otra.db4j.api.ctx.BuilderContext;
import cn.otra.db4j.api.element.Keyword;
import cn.otra.db4j.api.element.SqlElement;
import cn.otra.db4j.api.sql.Dialect;
import cn.otra.db4j.api.table.AliasField;
import cn.otra.db4j.api.table.SelectField;
import cn.otra.db4j.api.table.Table;
import cn.otra.db4j.api.table.TableField;
import cn.otra.db4j.api.table.Tables;
import cn.otra.db4j.api.util.TableUtils;
import cn.otra.db4j.core.utils.RowMapperUtils;

public class BuilderContextImpl implements BuilderContext {

	private static final Logger logger = Logger.getLogger(BuilderContextImpl.class);
	private LinkedList<SqlElement> list = null;//
	
	transient protected JdbcTemplate jdbcTemplate;
	protected Dialect dialect;
	
	private StringBuilder builder;
	private StringBuilder countBuilder;
//	private int fieldCount = 1;//记录查询字段的数量
	private int updateFiledCound = 0;
	private boolean hasOrderBy = false;
	private boolean hasGroupBy = false;
	private boolean hasDistinct = false;
	private boolean hasCalcAction = false;//是否有汇总需求
	private List<Object> params;
	private List<Object> countParams;
	
	boolean hasSetUpdateTime = false;
	
	public void setHasSetUpdateTime(boolean hasSetUpdateTime) {
		this.hasSetUpdateTime = hasSetUpdateTime;
	}
	
	@Override
	public StringBuilder getBuilder() {
		return builder;
	}
	
	public BuilderContextImpl(JdbcTemplate jdbcTemplate,Dialect dialect) {
		this.jdbcTemplate = jdbcTemplate;
		this.dialect = dialect;
	}
	
	public final <T extends SqlElement> BuilderContextImpl add(T e) {
		if(e == null) {
			return this;
		}
		if(list == null) {
			list = new LinkedList<SqlElement>();
		}
		if (e instanceof Condition) {// 添加查询条件（条件为链表形式）
			Condition cond = (Condition) e;
			Condition head = cond.head();
			int idx = list.size();
			int count = 0;
			while (head != null) {
				count++;
				head.setBuilderContext(this);
				list.addLast(head);
				head = head.next();
			}
			if (count > 1) {
				list.add(idx, Keyword.tagStart);
				list.addLast(Keyword.tagEnd);
			}

		} else {
			list.addLast(e);
		}
		if(!hasDistinct && e instanceof Keyword && Keyword.distinct.equals(e)) {
			hasDistinct = true;
		}
		//hasCalcAction
		if(!hasCalcAction && e instanceof AliasField && Keyword.sum.equals(((AliasField)e).getFunction())) {
			hasCalcAction = true;
		}
		if(!hasCalcAction && e instanceof AliasField && Keyword.count.equals(((AliasField)e).getFunction())) {
			hasCalcAction = true;
		}
		if(!hasCalcAction && e instanceof AliasField && Keyword.max.equals(((AliasField)e).getFunction())) {
			hasCalcAction = true;
		}
		if(!hasCalcAction && e instanceof AliasField && Keyword.min.equals(((AliasField)e).getFunction())) {
			hasCalcAction = true;
		}
		return this;
	}

	// ========================================================================
//	public BuilderContext addAliasTable(AliasTable table) {
//		if (aliasMap == null) {
//			aliasMap = new ConcurrentHashMap<Table, String>();
//		}
//		aliasMap.put(table.getTable(), table.getAlias());
//		return this;
//	}

	private <T extends SqlElement> BuilderContextImpl addAll(T... ts) {
		for (int i = 0, len = ts.length; i < len; i++) {
			SqlElement e = ts[i];
			add(e);
		}
		return this;
	}

	private BuilderContextImpl addTableFields(SelectField<?>... fields) {
		for (int i = 0, len = fields.length; i < len; i++) {
			SelectField<?> e = fields[i];
//			e.setBuilderContext(this);
			add(e);
			if (i < len - 1) {
				add(Keyword.comma);
			}
		}
		return this;
	}

	private BuilderContextImpl addTables(Table... tables) {
		for (int i = 0, len = tables.length; i < len; i++) {
			SqlElement e = tables[i];
			add(e);
			if (i < len - 1) {
				add(Keyword.comma);
			}
		}
		return this;
	}

	@Override
	public <T extends Serializable> T queryObject(Class<T> clazz) {
		try {
			String sql = getSql();
			if(logger.isDebugEnabled()) {
				logger.debug("sql[" + sql + "]");
			}
			Map<String, Object> map = jdbcTemplate.queryForMap(sql,getParams());
			boolean isEmpty = true;
			for(Map.Entry<String, Object> en:map.entrySet()) {
				if(en.getValue() != null) {
					isEmpty = false;
					break;
				}
			}
			if(isEmpty) {
				return null;
			} else {
				return TableUtils.map2Entity(map, clazz);
			}
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
//
//	@Override
//	public TableBuilder from(AliasTable aliasTable) {
//		addAliasTable(aliasTable);
//		add(Keyword.from);
//		return addTables(aliasTable.getTable());
//	}
//
//	@Override
//	public TableBuilder from(AliasTable... aliasTables) {
//		Table[] tables = new Table[aliasTables.length];
//		add(Keyword.from);
//		for (int i = 0; i < aliasTables.length; i++) {
//			AliasTable aliasTable = aliasTables[i];
//			addAliasTable(aliasTable);
//			tables[i] = aliasTable.getTable();
//		}
//		return addTables(tables);
//	}

	@Override
	public TableBuilder from(Table table) {
		addAll(Keyword.from, table);
		return this;
	}

	@Override
	public TableBuilder from(Table... tables) {
		add(Keyword.from).addTables(tables);
		return this;
	}

//	public String getAlias(Table table) {
//		if (aliasMap == null) {
//			aliasMap = new ConcurrentHashMap<Table, String>();
//		}
//		
//		String alias = aliasMap.get(table);
//		if(alias == null) {
//			alias = String.valueOf((char)(aliasMap.size()+'a'));
//			table.as(alias);
//			AliasTable aliasTable = new AliasTable(table, alias);
//			addAliasTable(aliasTable);
//		}
//		return alias;
//	}

	@Override
	public String getSql(boolean ... enableAlias) {
		//不传或传true，无为有效，只有传false时方为无效
		boolean eAlias = (enableAlias.length == 0 || (enableAlias.length==1 && enableAlias[0]));
		if(list != null) {
			if(builder == null) {
				builder = new StringBuilder();
			}
			
			if(builder.length() > 0) {
				return builder.toString();
			}
			for (int i = 0; i < list.size(); i++) {
				SqlElement e = list.get(i);
				if (e instanceof TableField) {
					TableField<?> etf = (TableField<?>) e;
					etf.appendTo(builder,this,eAlias);
				} else if (e instanceof Table) {
					Table table = (Table) e;
					table.appendTo(builder,this,eAlias);
				} else {
					e.appendTo(builder, this,eAlias);
				}
			}
			
			return builder.toString();
		} else {
			return null;
		}
	}

	@Override
	public String getCountSql(boolean ... enableAlias) {
		//不传或传true，无为有效，只有传false时方为无效
//		for (int i = 0; i < list.size(); i++) {
//			SqlElement e = list.get(i);
//			if(e instanceof Keyword && (e.equals(Keyword.distinct) || e.equals(Keyword.groupBy))) {
//				return getCountSqlWithDistinct(enableAlias);
//			}
//		}
		if(hasDistinct || hasGroupBy || hasCalcAction) {
			return getCountSqlWithAround(enableAlias);
		}
		boolean eAlias = (enableAlias.length == 0 || (enableAlias.length==1 && enableAlias[0]));
		if(list != null) {
			if(builder == null) {
				getSql();
			}
			
			if(countBuilder == null) {
				countBuilder = new StringBuilder();
			}
			
			if(countBuilder.length() > 0) {
				return countBuilder.toString();
			}
			
			int idx = 0;
			for (int i = 0; i < list.size(); i++) {
				SqlElement e = list.get(i);
				
				if(idx == 0 && e instanceof SelectField) {
//					if(fieldCount == 1) {
//						countBuilder.append(Keyword.countStart);
//						countBuilder.append("*");
//						countBuilder.append(Keyword.tagEnd);
//					} else {
//						countBuilder.append(Keyword.countAll);
//					}
					countBuilder.append(Keyword.countAll);
					idx ++;
				}
				
				
				if(idx == 1 && !(e instanceof Keyword && e.equals(Keyword.from))) {
					continue;
				}
				
				if(idx != 0) {
					idx ++;
				}
				
				int lidx = 0;
				if(lidx == 0 && e instanceof LimitCondition) {
					lidx = 1;
					continue;
				}
				
				if (e instanceof SelectField) {
					SelectField<?> etf = (SelectField<?>) e;
					etf.appendTo(countBuilder,this,eAlias);
				} else if (e instanceof Table) {
					Table table = (Table) e;
					table.appendTo(countBuilder,this,eAlias);
				} else {
					e.appendTo(countBuilder, this,eAlias);
				}
			}
			return countBuilder.toString();
		} else {
			return super.toString();
		}
	}
	
	
	private String getCountSqlWithAround(boolean ... enableAlias) {
		//不传或传true，无为有效，只有传false时方为无效
		//不传或传true，无为有效，只有传false时方为无效
		boolean eAlias = (enableAlias.length == 0 || (enableAlias.length==1 && enableAlias[0]));
		if(list != null) {
			if(countBuilder == null) {
				countBuilder = new StringBuilder();
			}
			
			if(countBuilder.length() > 0) {
				return countBuilder.toString();
			}
			countBuilder.append("select count(*) from (");
			for (int i = 0; i < list.size(); i++) {
				SqlElement e = list.get(i);
				int lidx = 0;
				if(lidx == 0 && e instanceof LimitCondition) {
					lidx = 1;
					continue;
				}
				
				if (e instanceof SelectField) {
					SelectField<?> etf = (SelectField<?>) e;
					etf.appendTo(countBuilder,this,eAlias);
				} else if (e instanceof Table) {
					Table table = (Table) e;
					table.appendTo(countBuilder,this,eAlias);
				} else {
					e.appendTo(countBuilder, this,eAlias);
				}
			}
			countBuilder.append(") _alias ");
			return countBuilder.toString();
		} else {
			return super.toString();
		}
		
	}
	
	@Override
	public Object[] getParams() {
		if(params == null) {
			params = new ArrayList<Object>();
		} else {
			params.clear();
		}
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				SqlElement e = list.get(i);
				if (e instanceof Condition) {
					Condition condition = (Condition)e;
					Object[] ps = condition.getParamValue();
					if(ps != null && ps.length > 0) {
						for(Object obj:ps) {
							params.add(obj);
						}
					}
				}
			}
		}
		
		Object [] objs = params.toArray();
		if(logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder("Parameters : [");
			for(int i=0;i<objs.length;i++) {
				builder.append(objs[i]);
				if(i < objs.length - 1) {
					builder.append(Keyword.comma);
				}
			}
			builder.append("]");
			logger.debug(builder.toString());
		}
		return objs;
	}
	
	@Override
	public Object[] getCountParams() {
		if(countParams == null) {
			countParams = new ArrayList<Object>();
		} else {
			countParams.clear();
		}
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				SqlElement e = list.get(i);
				if (e instanceof Condition) {
					Condition condition = (Condition)e;
					Object[] ps = condition.getParamValue();
					if(ps != null && ps.length > 0) {
						for(Object obj:ps) {
							if(!(condition instanceof LimitCondition)) {
								countParams.add(obj);
							}
						}
					}
				}
			}
		}
		
		Object [] objs = countParams.toArray();
		if(logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder("Parameters : [");
			for(int i=0;i<objs.length;i++) {
				builder.append(objs[i]);
				if(i < objs.length - 1) {
					builder.append(Keyword.comma);
				}
			}
			builder.append("]");
			logger.debug(builder.toString());
		}
		return objs;
	}

	
	@Override
	public HavingBuilder groupBy(TableField<?> field) {
		add(Keyword.groupBy).add(field);
		hasGroupBy = true;
		return this;
	}

	@Override
	public OrderByBuilder having(Condition condition) {
		addAll(Keyword.having,condition);
		return this;
	}

//	@Override
//	public JoinBuilder join(AliasTable aliasTable) {
//		addAliasTable(aliasTable);
//		add(Keyword.join);
//		return addTables(aliasTable.getTable());
//	}

	@Override
	public JoinBuilder join(Table table) {
		addAll(Keyword.join,table);
		return this;
	}

//	@Override
//	public JoinBuilder leftJoin(AliasTable aliasTable) {
//		addAliasTable(aliasTable);
//		add(Keyword.leftJoin);
//		return addTables(aliasTable.getTable());
//	}
	
	@Override
	public JoinBuilder leftJoin(Table table) {
		add(Keyword.leftJoin).addTables(table);
		return this;
	}
	
	@Override
	public EndBuilder limit(Integer rows) {
		if(rows == null || rows < 0) {
			throw new RuntimeException("rows < 0,rows="+rows);
		}
		add(new LimitCondition(0, rows));
		return this;
	}

	@Override
	public EndBuilder limit(Integer offset, Integer rows) {
		if(offset == null || offset < 0) {
			throw new RuntimeException("offset is null or offset < 0,offset="+offset);
		}
		if(rows == null || rows < 0) {
			throw new RuntimeException("rows is null or rows < 0,rows="+rows);
		}
		add(new LimitCondition(offset, rows));
		return this;
	}

	@Override
	public TableBuilder on(Condition condition) {
		addAll(Keyword.on, condition);
		return this;
	}

	@Override
	public SortTypeBuilder orderBy(TableField<?> field) {
		if(hasOrderBy) {
			add(Keyword.comma);
		} else {
			add(Keyword.orderBy);
			hasOrderBy = true;
		}
		addTableFields(field);
		return this;
	}

//	@Override
//	public JoinBuilder rightJoin(AliasTable aliasTable) {
//		addAliasTable(aliasTable);
//		add(Keyword.rightJoin);
//		return addTables(aliasTable.getTable());
//	}

	@Override
	public JoinBuilder rightJoin(Table table) {
		add(Keyword.rightJoin).addTables(table);
		return this;
	}

	@Override
	public FromBuilder select(SelectField<?>... fields) {
//		fieldCount = fields.length;
		add(Keyword.select).addTableFields(fields);
		return this;
	}

	@Override
	public TableBuilder selectFrom(Table table) {
		add(Keyword.select).add(table.allField()).add(Keyword.from).addTables(table);
//		addAll(Keyword.select,table.allField(), Keyword.from).addTables(table);
		return this;
	}

	
	
	
//	private static final Object[] tempParams = new Object[0];
	
	@Override
	public JoinBuilder using(TableField<?> field) {
		addAll(Keyword.usingStart,field,Keyword.usingEnd);
		return this;
	}

	@Override
	public OrderByBuilder where(Condition condition) {
		if(condition != null) {
			add(Keyword.where).add(condition);
		}
		return this;
	}

	@Override
	public SortTypeBuilder asc() {
		add(Keyword.asc);
		return this;
	}

	@Override
	public SortTypeBuilder desc() {
		add(Keyword.desc);
		return this;
	}
	
	private void updateLimitCondition(Integer offset, Integer rowsPerPage) {
		if(limitCondition == null) {
			limitCondition = new LimitCondition(offset, rowsPerPage);
        	addAll(limitCondition);
        } else {
        	limitCondition.update(offset, rowsPerPage);
        }
	}
	private LimitCondition limitCondition;
	
	@SuppressWarnings("rawtypes")
	private static final List emptyList = new ArrayList();
	@Override
	public <T extends Serializable> ECPage<T> queryPage(Class<T> entityClass, Integer page, Integer rowsPerPage) {
		if(page == null) {
			page = 1;
		}
		if(rowsPerPage == null) {
			rowsPerPage = ECPage.DEFAULT_SIZE;
		}
		updateLimitCondition((page-1)*rowsPerPage, rowsPerPage);
		String countSql = this.getCountSql();
		countSql = this.getCountSql();
		Object []countParams = getCountParams();
		Integer totalRows = jdbcTemplate.queryForObject(countSql, countParams,Integer.class);//.queryForInt(countSql,countParams);
        
        if(totalRows <= 0) {//优化，减少查询
            @SuppressWarnings("unchecked")
            ECPage<T> pageEntity = ECPage.initPage(emptyList, totalRows, rowsPerPage, page);
            return pageEntity;
        } else {
        	Table table = null;
            try {
            	 table = Tables.getFromPo(entityClass);
            } catch (Exception e) {
            }
            Object [] params = getParams();
            List<T> datas = jdbcTemplate.query(getSql(),params, RowMapperUtils.getSimpleRowMapper(entityClass, table));
            ECPage<T> pageEntity = ECPage.initPage(datas, totalRows, rowsPerPage, page);
            return pageEntity;
        }
        
	}

	@Override
	public Map<String, Object> queryOneRecord() {
		return jdbcTemplate.queryForMap(getSql(),getParams());
	}

	@Override
	public List<Map<String, Object>> queryRecords() {
		return jdbcTemplate.queryForList(getSql(),getParams());
	}

	@Override
	public <T extends Serializable> List<T> queryObjectsForList(Class<T> entityClass) {
		Table table = null;
		try {
			table = Tables.getFromPo(entityClass);
		} catch (Exception e) {
			//table = null;
		}
		return jdbcTemplate.query(getSql(),getParams(), RowMapperUtils.getSimpleRowMapper(entityClass, table));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable, E> Map<E, T> queryObjectsForMap(Class<T> entityClass,TableField<E> field) {
		Map<String, Field> fieldMap = TableUtils.getDeclaredFieldMap(entityClass);
		Field keyField = fieldMap.get(field.getJavaName());
		
		Table table = null;
		try {
			table = Tables.getFromPo(entityClass);
		} catch (Exception e1) {
		}
		if(keyField == null) {
			throw new RuntimeException(" field not found in table["+table.getTableName()+"].");
		}
		
		String sql = getSql();
		RowMapper<T> rowMapper = RowMapperUtils.getSimpleRowMapper(entityClass, table);
		List<T> list = jdbcTemplate.query(sql,getParams(), rowMapper);
		
		try {
			Map<E, T> map = new HashMap<E, T>();
			keyField.setAccessible(true);
			for (T t : list) {
				map.put((E)keyField.get(t), t);
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException("",e);
		}
	}

	@Override
	public SetBuilder updateWithCondition(Table table) {
		addAll(Keyword.update,table);
		return this;
	}
	
	@Override
	public SetBuilder updateWithCondition(Table... tables) {
		add(Keyword.update);
		for(int i=0;i<tables.length;i++) {
			Table t = tables[i];
			add(t);
			if(i<tables.length-1) {
				add(Keyword.comma);
			}
		}
		return this;
	}
	
	@Override
	public <T> SetBuilder set(TableField<T> key, T value) {
		if(key.getJavaName().equals("createTime")) {
			throw new RuntimeException("can not update createTime.");
		}
		if(updateFiledCound == 0) {
			add(Keyword.set);
			if(key.getTable().getUpdateTimeField() != null && !hasSetUpdateTime) {
				Table table = key.getTable();
				TableField<Date> updateField = table.getUpdateTimeField();
				if(updateField != null) {
					add(updateField.eq(new Date()))
					.add(Keyword.comma);
					setHasSetUpdateTime(true);
				}
			}
		} else if(updateFiledCound > 0) {
			if("updateTime".equals(key.getJavaName())) {
				//禁用用户自定义更新时间，强制使用系统更新时间
				return this;
			}
			add(Keyword.comma);
		}
		add(key.eq(value));
		
		updateFiledCound ++;
		return this;
	}
	
	@Override
	public <T extends Number> SetBuilder set(AddCondition<T> condition) {
		TableField<T> key = condition.getField();
		if(updateFiledCound == 0) {
			add(Keyword.set);
			if(key.getTable().getUpdateTimeField() != null && !hasSetUpdateTime) {
				Table table = key.getTable();
				TableField<Date> updateField = table.getUpdateTimeField();
				if(updateField != null) {
					add(updateField.eq(new Date()))
					.add(Keyword.comma);
					setHasSetUpdateTime(true);
				}
			}
		} else if(updateFiledCound > 0) {
			if("updateTime".equals(key.getJavaName())) {
				//禁用用户自定义更新时间，强制使用系统更新时间
				return this;
			}
			add(Keyword.comma);
		}
		
		add(condition);
		
		updateFiledCound ++;
		return this;
	}
	
	@Override
	public <T> SetBuilder setWithBuilder(TableField<T> key, EndBuilder endBuilder) {
		if(key.getJavaName().equals("createTime")) {
			throw new RuntimeException("can not update createTime.");
		}
		if(updateFiledCound == 0) {
			add(Keyword.set);
			if(key.getTable().getUpdateTimeField() != null && !hasSetUpdateTime) {
				Table table = key.getTable();
				TableField<Date> updateField = table.getUpdateTimeField();
				if(updateField != null) {
					add(updateField.eq(new Date()))
					.add(Keyword.comma);
					setHasSetUpdateTime(true);
				}
			}
		} else if(updateFiledCound > 0) {
			if("updateTime".equals(key.getJavaName())) {
				//禁用用户自定义更新时间，强制使用系统更新时间
				return this;
			}
			add(Keyword.comma);
		}
		
		add(key.eq(endBuilder));
		updateFiledCound ++;
		return this;
	}

	@Override
	public int executeUpdate() {
		return jdbcTemplate.update(getSql(),getParams());
	}

	@Override
	public void show() {
		System.err.println(this.getSql());
		Object[] params = this.getParams();
		for(int i=0;i<params.length;i++) {
			Object o = params[i];
			System.err.print(o);
			if(i<params.length-1) {
				System.err.print(",");
			}
		}
		System.err.println();
	}

//	@Override
//	public <T extends Serializable> PageIterator<T> pageIterator(Class<T> entityClass, Integer rowsPerPage) {
//		ECPage<T> ecPage = queryPage(entityClass, 1, rowsPerPage);
//		PageIterator<T> pageInput = new PageIteratorImpl<T>(entityClass, this, null, null, ecPage,true);
//		return pageInput;
//	}

	@Override
	public FromBuilder selectDistinct(SelectField<?>... fields) {
//		fieldCount = fields.length;
		add(Keyword.select).add(Keyword.distinct).addTableFields(fields);
		return this;
	}

	@Override
	public TableBuilder selectDistinctFrom(Table table) {
		add(Keyword.select).add(Keyword.distinct).add(table.allField()).add(Keyword.from).addTables(table);
		return this;
	}

	@Override
	public Boolean hasGroupBy() {
		return hasGroupBy;
	}

	@Override
	public Boolean hasDistinct() {
		return hasDistinct;
	}

	@Override
	public Boolean hasOrderBy() {
		return hasOrderBy;
	}
}
