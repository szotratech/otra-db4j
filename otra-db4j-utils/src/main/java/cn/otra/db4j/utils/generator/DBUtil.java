package cn.otra.db4j.utils.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	
	public static String URL ;//= PropUtil.getProp(propFile, key, reloadFile);
	public static String USERNAME;
	public static String PASSWORD;
	public static String DIREVER;
	private static boolean loadDriver;
	
	static void loadDriver() {
		try {
			//加载驱动
			Class.forName(DIREVER);
			loadDriver = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static final Connection getConnection() {
		if(!loadDriver) {
			loadDriver();
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static final void close(Connection conn,Statement stmt,ResultSet rs) {
		if(rs != null) {
			try {rs.close();} catch (SQLException e) {}
		}
		if(stmt != null) {
			try {stmt.close();} catch (SQLException e) {}
		}
		if(conn != null) {
			try {conn.close();} catch (SQLException e) {}
		}
	}
}
