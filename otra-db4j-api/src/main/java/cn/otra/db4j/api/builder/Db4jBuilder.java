package cn.otra.db4j.api.builder;


/**
 * 
 * @author satuo20
 <br/>
 <br/>
 <br/>
	   SELECT <br/>
	    [ALL | DISTINCT | DISTINCTROW ] <br/>
	      [HIGH_PRIORITY] <br/>
	      [STRAIGHT_JOIN] <br/>
	      [SQL_SMALL_RESULT] [SQL_BIG_RESULT] [SQL_BUFFER_RESULT] <br/>
	      [SQL_CACHE | SQL_NO_CACHE] [SQL_CALC_FOUND_ROWS] <br/>
	    select_expr, ... <br/>
	    [INTO OUTFILE 'file_name' export_options <br/>
	      | INTO DUMPFILE 'file_name'] <br/>
	    [FROM table_references <br/>
	    [WHERE where_definition] <br/>
	    [GROUP BY {col_name | expr | position} <br/>
	      [ASC | DESC], ... [WITH ROLLUP]] <br/>
	    [HAVING where_definition] <br/>
	    [ORDER BY {col_name | expr | position} <br/>
	      [ASC | DESC] , ...] <br/>
	    [LIMIT {[offset,] row_count | row_count OFFSET offset}] <br/>
	    [PROCEDURE procedure_name(argument_list)] <br/>
	    [FOR UPDATE | LOCK IN SHARE MODE]] <br/>
 
 */
public interface Db4jBuilder {
    
	String getSql(boolean ... enableAlias);
	
	String getCountSql(boolean ... enableAlias);
	
	Object[] getParams();
	
	Object[] getCountParams();
	
	Boolean hasGroupBy();
	
	Boolean hasDistinct();
	
	Boolean hasOrderBy();
	
}
