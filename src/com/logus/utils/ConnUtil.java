package com.logus.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnUtil {
	static String jdbcURL = "jdbc:oracle:thin:@//192.168.0.38:1521/desenv02.logusinfo.com.br";
	static String username = "DIVIDA_PI_2022";
	static String password = "DIVIDA_PI_2022";
	
	private static InheritableThreadLocal<Connection> connection;
	
	static {
		connection = new InheritableThreadLocal<Connection>();
	}
	
	public static Connection init() throws ConnectionException{
		try {
			if (connection.get() == null) {
				Connection c = DriverManager.getConnection(jdbcURL, username, password);
				c.setAutoCommit(false);		
				connection.set(c);				
			}
		} catch (SQLException e) {
			throw new ConnectionException("Erro ao abrir uma conexão");
		}
		return connection.get();	
	}

}
