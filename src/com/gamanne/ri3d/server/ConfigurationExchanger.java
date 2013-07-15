package com.gamanne.ri3d.server;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.apache.sshd.server.session.ServerSession;

import com.gamanne.ri3d.server.jclouds.JCloudsNova;

public class ConfigurationExchanger {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	public ConfigurationExchanger(ServerSession session) {
		JCloudsNova.startJCloudsNova();
		createFile();
	}

	private void createFile() {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new	FileOutputStream("/home/joaquin/.ri3d/configDataClient.cfg"), "UTF-8"));
			writer.write("newCommunicationPort = " + 11 + "\n");
			writer.close();
		} catch (IOException e) {
			LOGGER.info(e.getMessage());
			e.printStackTrace();
		}
	}
}
