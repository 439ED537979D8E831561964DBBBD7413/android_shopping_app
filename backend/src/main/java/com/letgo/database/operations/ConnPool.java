package com.letgo.database.operations;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

public class ConnPool {
	static ConnPool instance = null;
	

	private int minConnections = 10;
	private int maxconnections = 50;
	private String DBNAME = "letgo";
	private String HOST = "localhost:3306";
	List<Connection> connlist = new ArrayList<Connection>();
	List<Connection> usedconnlist = new ArrayList<Connection>();
	List<Connection> availableconnlist = new ArrayList<Connection>();
	
	String jdbcUrl = "jdbc:mysql://"+ HOST + "/" + DBNAME + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
	private String user = "root"; 
	private String password = "1234";
	
	
	public ConnPool() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		} catch (InstantiationException e1) {

			e1.printStackTrace();
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {

			e1.printStackTrace();
		}
		for (int i = 0; i < minConnections; i++) {
			try {
				Connection conn = createConnection();
				connlist.add(conn);
				availableconnlist.add(conn);

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

	public static ConnPool getInstance() {
		if (instance == null) {
			instance = new ConnPool();
		}
		return instance;
	}

	public Connection getConnection() {
		if (availableconnlist.size() > 0) {
			Connection conn = availableconnlist.remove(0);
			usedconnlist.add(conn);
			return conn;
		}
		if (connlist.size() < maxconnections) {

			try {
				Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
				connlist.add(conn);
				usedconnlist.add(conn);
				return conn;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			synchronized (availableconnlist) {
				try {
					availableconnlist.wait(20000);
					if (availableconnlist.size() > 0) {
						Connection conn = createConnection();
						connlist.add(conn);
						usedconnlist.add(conn);
						return conn;
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private Connection createConnection() throws SQLException {

		Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

		conn.setAutoCommit(true);

		return conn;
	}

	public void returnConnection(Connection conn) {
		usedconnlist.remove(conn);
		if (testConnection(conn)) {
			availableconnlist.add(conn);
		} else {
			connlist.remove(conn);
			try {
				conn = createConnection();
				connlist.add(conn);
				availableconnlist.add(conn);
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		synchronized (availableconnlist) {
			availableconnlist.notify();
		}
	}

	private boolean testConnection(Connection conn) {
		boolean answer = false;
		try {
			ResultSet resRaws = null;

			PreparedStatement testSql = conn.prepareStatement("select 1;");
			resRaws = testSql.executeQuery();

			String res = null;
			while (resRaws.next()) {
				res = resRaws.getString(1);
			}

			if (res != null) {
				answer = true;
			}

			resRaws.close();

			testSql.close();
			
		} catch (Throwable t) {
			t.printStackTrace();
			answer = false;
		}

		return answer;
	}

	public void flushAllConnection() {
		for (Connection connection : connlist) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		connlist.clear();
	}
}
