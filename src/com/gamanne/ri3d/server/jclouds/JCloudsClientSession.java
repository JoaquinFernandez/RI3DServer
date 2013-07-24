package com.gamanne.ri3d.server.jclouds;

import java.util.logging.Logger;

import org.apache.sshd.server.session.ServerSession;

public class JCloudsClientSession {
	
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	public JCloudsClientSession(ServerSession session) {
		LOGGER.info("JCloudsNova object created");
		JCloudsNova jCloudsNova = new JCloudsNova();
		//Wait for the user to answer
		//LOGGER.info("JCloudsNova object closed");
		//jCloudsNova.close();
	}
}
