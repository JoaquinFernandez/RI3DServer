package com.gamanne.ri3d.server.jclouds;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;

public class JCloudsNova implements Closeable {
   private ComputeService compute;
   private NovaApi api;
   private Set<String> zones;

   public static void startJCloudsNova() {
      JCloudsNova jCloudsNova = new JCloudsNova();

      try {
         jCloudsNova.init();
         jCloudsNova.listServers();
         jCloudsNova.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      finally {
         jCloudsNova.close();
      }
   }

   private void init() {
	   ;
      //Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());

      //String provider = "openstack-nova";
      //String identity = "demo:demo"; // tenantName:userName
      //String password = "devstack"; // demo account uses ADMIN_PASSWORD too

      //ComputeServiceContext context = ContextBuilder.newBuilder(provider)
        //    .endpoint("http://172.16.0.1:5000/v2.0/")//direccion a trystack
          //  .credentials(identity, password)
            //.modules(modules)
            //.buildView(ComputeServiceContext.class);
      api = ContextBuilder.newBuilder("openstack-nova")
              .endpoint("http://8.21.28.222:5000/v2.0/")//direccion a trystack
              .credentials("facebook100003380026930:facebook100003380026930", "rw7cQ8eO")
              .buildApi(NovaApi.class);
      zones = api.getConfiguredZones();
   }

   private void listServers() {
      for (String zone: zones) {
    	  
         ServerApi serverApi = api.getServerApiForZone(zone);
         System.out.println("Servers in " + zone);
         for (Server server: serverApi.listInDetail().concat()) {
            System.out.println("  " + server);
         }
         
         System.out.println("Flavors in " + zone);
         FlavorApi flavorApi = api.getFlavorApiForZone(zone);
         for (Flavor flavor: flavorApi.listInDetail().concat()) {
             System.out.println("  " + flavor);
          }

         System.out.println("Flavors in " + zone);
         ImageApi imageApi = api.getImageApiForZone(zone);
         for (Image image: imageApi.listInDetail().concat()) {
             System.out.println("  " + image);
         }
      }
   }

   @Override 
   public void close() {
	   try {
		com.google.common.io.Closeables.close(compute.getContext(), false);
	} catch (IOException e) {
		//Handle error
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

}