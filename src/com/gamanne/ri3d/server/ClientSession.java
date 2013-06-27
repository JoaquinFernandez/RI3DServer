package com.gamanne.ri3d.server;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

public class ClientSession {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	private String password;
	private String user;

	public ClientSession(String user, String password) {
		this.user = user;
		this.password = password;
		int newPort = createServerAndWaitForRequest();
		createFile(newPort);
	}

	private int createServerAndWaitForRequest() {
		//Get a random port between 50175 64510
		int newPort = (int) (((Math.random()/4) + 0.875)*57343);
		SshServer sshServer = SshServer.setUpDefaultServer();
		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

			@Override
			public boolean authenticate(String usr, String pwd,
					ServerSession session) {
				if (user.contentEquals(usr) && password.contentEquals(pwd))
					return true;
				return false;
			}
		});
		sshServer.setPort(newPort);
		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		try {
			sshServer.start();
			LOGGER.info("New ssh server working on port: " + newPort);
		} catch (IOException e) {
			if (e.getMessage().contentEquals("Address already in use")) {
				LOGGER.info(e.getMessage() + " port: " + newPort);
				return createServerAndWaitForRequest();
			}
			else {
				LOGGER.info(e.getMessage());
				e.printStackTrace();
				return -1;
			}
		}
		return newPort;
	}

	private void createFile(int newPort) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new	FileOutputStream("/home/joaquin/.ri3d/configDataClient.cfg"), "UTF-8"));
			writer.write("newCommunicationPort = " + newPort + "\n");
			writer.close();
		} catch (IOException e) {
			LOGGER.info(e.getMessage());
			e.printStackTrace();
		}
	}

}
