package com.gamanne.ri3d.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import com.gamanne.ri3d.server.jclouds.JCloudsClientSession;

public class ClientSessionAuthenticator implements PasswordAuthenticator {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	@Override
	public boolean authenticate(String user, String password,
			ServerSession session) {
		LOGGER.info("Checking user credentials");
		if (isAuthenticated(user, password)) {
			LOGGER.info("Client authenticated successfully");
			//Each time a client is connected, it creates a session for him
			new JCloudsClientSession(session);
			return true;
		}
		LOGGER.info("Client couldn't be authenticated");
		return false;
	}

	private boolean isAuthenticated(String user, String password) {
		String dburl = "jdbc:mysql://localhost:3306/ri3d";
		String dbuser = "ri3dadmin";
		String dbpassword = "ri3ddb";
		String dbAESKey = "ri3dkey";
		String query = "SELECT AES_DECRYPT(password, '" + dbAESKey + "') FROM user WHERE user = '" + user + "';";
		boolean authenticated = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(dburl, dbuser, dbpassword);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				String dbPassword = resultSet.getString(1);
				if (password.contentEquals(dbPassword)) {
					authenticated = true;
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			LOGGER.info(e.getMessage());
			e.printStackTrace();
		}
		return authenticated;
	}

}
