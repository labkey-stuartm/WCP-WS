package com.studymetadata.corn;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


public class MyJob implements Job {
	private static final Logger LOGGER = Logger.getLogger(MyJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("MyJob.execute() :: Starts ");
		ClientConfig config = new DefaultClientConfig();
		try {
			Client client = Client.create(config);
			WebResource service = client.resource(getBaseURI());
			System.out.println("Test :: " + service.path("schedule").get(String.class).toString());
			System.out.println("Job --->>> Hello Common WS!");
		} catch (Exception e) {
			LOGGER.error("MyJob.execute() : ERROR "+ e);
		}
		LOGGER.info("MyJob.execute() : Ends");
	}
    
    private static URI getBaseURI() {
    	 return UriBuilder.fromUri("http://localhost:8080/fdahpStudyDesignerWS/").build();
	}
}