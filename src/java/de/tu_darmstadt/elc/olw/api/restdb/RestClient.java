package de.tu_darmstadt.elc.olw.api.restdb;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

public class RestClient {
	private static Logger logger = Logger.getLogger(RestClient.class);
	private ClientConfig config;
	private ApacheHttpClient client;
	private String fakeLogIn;
	private String apiURL;

	public RestClient(String fakeLogIn, String apiURL) {
		this.fakeLogIn = fakeLogIn;
		this.apiURL = apiURL;
		config = new DefaultApacheHttpClientConfig();
		config.getClasses().add(JacksonJsonProvider.class);
		client = ApacheHttpClient.create(config);

		ClientFilter filter = new ClientFilter() {
			private ArrayList<Object> cookies;

			@Override
			public ClientResponse handle(ClientRequest request)
					throws ClientHandlerException {
				if (cookies != null) {
					request.getHeaders().put("Cookie", cookies);
				}
				ClientResponse response = getNext().handle(request);
				// copy cookies
				if (response.getCookies() != null) {
					if (cookies == null) {
						cookies = new ArrayList<Object>();
					}
					
					cookies.addAll(response.getCookies());
				}

				logger.debug("Cookies: " + cookies.toString());

				return response;
			}
		};
		client.addFilter(filter);
		client.setFollowRedirects(false);
	}

	public void updateDB(String uuid, String dbField, String newValue) {
		WebResource webResource = client.resource(fakeLogIn);
		ClientResponse response = webResource
				.type("application/x-www-form-urlencoded")
				.accept("application/json").get(ClientResponse.class);
		logger.debug("Cookies: " + response.getCookies().toString());
		try {
			String result = client.resource(apiURL).path("resource")
					.path("uuid").path(uuid).path(dbField)
					.type(MediaType.APPLICATION_JSON)
					.put(String.class, newValue);
			logger.debug(uuid + ": update " + dbField + " to " + result);
		} catch (Exception e) {
			logger.info("Closing connection ...");
			response.close();
			client.destroy();
			logger.error(e.getMessage());			
		}

	}

	public String queryDB(String uuid, String dbField) {
		String result = "";
		WebResource webResource = client.resource(fakeLogIn);
		ClientResponse response = webResource
				.type("application/x-www-form-urlencoded")
				.accept("application/json").get(ClientResponse.class);
		logger.debug("Cookies: " + response.getCookies().toString());
		try {
			String internId = client.resource(apiURL).path("resource")
					.path("uuid").path(uuid).type(MediaType.APPLICATION_JSON)
					.get(String.class);
			result = client.resource(apiURL).path("resource")
					.path(internId).type(MediaType.APPLICATION_JSON)
					.get(String.class);
			
		} catch (Exception e) {
			logger.info("Closing connection ...");
			client.destroy();
			response.close();
			logger.error(e.getMessage());			
		}
		return extractField(result, dbField);
	}
	
	private String extractField(String result, String dbField) {
		if (result.equals(""))
			return "";
		int index = result.indexOf(dbField);
		if (index == -1)
			return "";
		StringBuilder str = new StringBuilder();
		char[] tokens = result.toCharArray();
		int i = result.indexOf(":", index) +1;
		while (tokens[i]!=',') {
			str.append(tokens[i]);
			i++;
		}
		return str.toString();
		
	}

}
