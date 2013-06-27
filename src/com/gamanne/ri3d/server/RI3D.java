package com.gamanne.ri3d.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

public class RI3D {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SshServer sshServer = SshServer.setUpDefaultServer();
		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

			@Override
			public boolean authenticate(String user, String password,
					ServerSession session) {
				boolean authenticated = isAuthenticated(user, password);
				if (authenticated) {
					new ClientSession(user, password);
					return true;
				}
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
						if (password.contentEquals(dbPassword))
							authenticated = true;
					}
				} catch (SQLException | ClassNotFoundException e) {
					LOGGER.info(e.getMessage());
					e.printStackTrace();
				}
				return authenticated;
			}

		});
		Config config = new Config();
		int port = Integer.parseInt(config.getProperty("initialPort"));
		sshServer.setPort(port);
		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshServer.setCommandFactory(new ScpCommandFactory());
		try {
			sshServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
