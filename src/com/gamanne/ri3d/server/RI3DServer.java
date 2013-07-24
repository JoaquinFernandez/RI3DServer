package com.gamanne.ri3d.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.Channel;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

public class RI3DServer {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	private final static int defaultPort = 20122;
	
	private static SshServer sshServer;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sshServer = SshServer.setUpDefaultServer();
		sshServer.setPasswordAuthenticator(new ClientSessionAuthenticator());
		sshServer.setPort(defaultPort);
		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshServer.setCommandFactory(new ScpCommandFactory());
		//Only accept channel sessions (security)
		sshServer.setChannelFactories(Arrays.<NamedFactory<Channel>>asList(
                new ChannelSession.Factory()));
		try {
			sshServer.start();
			LOGGER.info("Server started successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
