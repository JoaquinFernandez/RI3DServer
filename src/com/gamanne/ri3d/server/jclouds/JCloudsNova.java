package com.gamanne.ri3d.server.jclouds;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class JCloudsNova implements Closeable {

	private static Logger LOGGER = Logger.getLogger("InfoLogging");

	private ComputeService compute;
	private NovaApi api;
	private Set<String> zones;

	public JCloudsNova() {
		try {
			LOGGER.info("Initializing...");
			init();
			retrieveServerInfo();
		}
		catch (Exception e) {
			LOGGER.warning(e.getMessage());
			e.printStackTrace();
		}
	}

	private void init() {		
		String identity = "admin:admin"; // tenantName:userName
		String password = "ri3ddb"; // demo account uses ADMIN_PASSWORD too

		api = ContextBuilder.newBuilder(new NovaApiMetadata())
				.endpoint("http://localhost:5000/v2.0")
				.credentials(identity, password)
				.buildApi(NovaApi.class);
		zones = api.getConfiguredZones();
	}

	private void retrieveServerInfo() {

		try {

			JSONObject serversInfo = new JSONObject();

			for (String zone: zones) {
				JSONObject serverInfo = new JSONObject();

				JSONArray serverArray = new JSONArray();
				ServerApi serverApi = api.getServerApiForZone(zone);
				for (Server server: serverApi.listInDetail().concat()) {
					System.out.println(server);
					JSONObject object = new JSONObject();
					object.put("id", server.getId());
					object.put("tenantId", server.getTenantId());
					object.put("userId", server.getUserId());
					object.put("flavorId", server.getFlavor().getId());
					object.put("name", server.getName());
					serverArray.put(object);
				}
				serverInfo.put("servers", serverArray);

				JSONArray flavorArray = new JSONArray();
				FlavorApi flavorApi = api.getFlavorApiForZone(zone);
				for (Flavor flavor: flavorApi.listInDetail().concat()) {
					JSONObject object = new JSONObject();
					object.put("id", flavor.getId());
					object.put("name", flavor.getName());
					object.put("ram", flavor.getRam());
					object.put("disk", flavor.getDisk());
					flavorArray.put(object);
				}
				serverInfo.put("flavors", flavorArray);


				JSONArray imageArray = new JSONArray();
				ImageApi imageApi = api.getImageApiForZone(zone);
				for (Image image: imageApi.listInDetail().concat()) {
					JSONObject object = new JSONObject();
					object.put("name", image.getName());
					object.put("status", image.getStatus());
					object.put("id", image.getId());
					imageArray.put(object);
				}
				serverInfo.put("images", imageArray);

				serversInfo.put(zone, serverInfo);
			}

			writeServerInfo(serversInfo.toString());
			
		} catch (JSONException e) {
			LOGGER.warning(e.getMessage());
			e.printStackTrace();
		}
	}


	private void writeServerInfo(String serverInfo) {
		try {
			String path = "/home/joaquin/.ri3d/serverInfo.txt";
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new	FileOutputStream(path), "UTF-8"));
			writer.write(serverInfo);
			writer.close();
			LOGGER.info("Server info written to: " + path);
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override 
	public void close() {
		try {
			com.google.common.io.Closeables.close(compute.getContext(), false);
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
			//Handle error
			e.printStackTrace();
		}
	}

}