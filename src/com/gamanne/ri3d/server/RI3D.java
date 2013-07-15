package com.gamanne.ri3d.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

public class RI3D {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SshServer sshServer = SshServer.setUpDefaultServer();
		sshServer.setPasswordAuthenticator(new ClientSessionAuthenticator());
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
