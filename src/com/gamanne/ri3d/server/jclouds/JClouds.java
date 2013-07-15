package com.gamanne.ri3d.server.jclouds;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;


public class JClouds {

	public JClouds() {
		String identity = "";
		String credential = "";
		ComputeServiceContext context = 
				ContextBuilder.newBuilder("openstack-nova")
				.credentials(identity, credential)
				.buildView(ComputeServiceContext.class);
	}
}
