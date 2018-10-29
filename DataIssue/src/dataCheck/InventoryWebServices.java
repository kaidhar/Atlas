package dataCheck;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class InventoryWebServices {

	public void UpdateInventory(String ItemID, String Banner, String ShipNode, String Quantity, String Environment)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {

		String url = getProperty("URL" + Environment);

		if (Banner == "OFF") {
			Banner = "OFF5";

		}

		String requestTemplate = getProperty("WebRequestAdjustInventory");

		String requestTemplate1 = requestTemplate.replaceAll("&ItemID", ItemID);
		String requestTemplate2 = requestTemplate1.replaceAll("&Banner", Banner);
		String requestTemplate3 = requestTemplate2.replaceAll("&ShipNode", ShipNode);
		String requestTemplateFinal = requestTemplate3.replaceAll("&Quantity", Quantity);

		String finalrequest = requestTemplateFinal;

		String name = getCredentialProperty("UserCredentials");
		String password = getCredentialProperty("PWDCredentials");

		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(finalrequest);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/xml");
		httpPost.setHeader("Content-type", "application/xml");
		httpPost.setHeader("Authorization", "Basic  " + authStringEnc);

		CloseableHttpResponse response = client.execute(httpPost);
		client.close();

		System.out.println(response.toString());

	}

	public void UpdateMonitor(String ItemID, String Banner, String Environment) throws IOException {

		String url = getProperty("URLMonitor" + Environment);

		if (Banner == "OFF") {
			Banner = "OFF5";

		}

		String requestTemplate = getProperty("WebRequestMonitorAvailability");

		String requestTemplate1 = requestTemplate.replaceAll("&ItemID", ItemID);
		String requestTemplate2 = requestTemplate1.replaceAll("&Banner", Banner);

		String finalrequest = requestTemplate2;

		String name = getCredentialProperty("UserCredentials");
		String password = getCredentialProperty("PWDCredentials");
		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(finalrequest);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/xml");
		httpPost.setHeader("Content-type", "application/xml");
		httpPost.setHeader("Authorization", "Basic  " + authStringEnc);

		CloseableHttpResponse response = client.execute(httpPost);
		client.close();

		System.out.println(response.toString());

	}

	String getProperty(String Prop) throws IOException {

		Properties prop = new Properties();
		InputStream input = null;

		input = this.getClass().getResourceAsStream("Keys.properties");
		// FileInputStream("//Users//h895458//Desktop//Workspace//DataIssue//src//dataCheck//Keys.properties");
		prop.load(input);
		String Value = prop.getProperty(Prop);
		return Value;

	}

	String getCredentialProperty(String Prop) throws IOException {

		Properties prop = new Properties();
		InputStream input = null;
		String path = ClassLoader.getSystemClassLoader().getResource(".").getPath() + "Credentials.properties";
		System.out.println(path + "\n");
		// input = this.getClass().getResourceAsStream(path);
		input = new FileInputStream(path);
		// FileInputStream("//Users//h895458//Desktop//Workspace//DataIssue//src//dataCheck//Keys.properties");
		prop.load(input);
		String Value = prop.getProperty(Prop);
		return Value;

	}

	public Multimap<String, String> getOrderDetails(String orderIDValue, String environment, String banner)
			throws IOException, JSONException, JDOMException {

		String requestTemplate = getProperty("getOrderDetails");

		String requestTemplate1 = requestTemplate.replaceAll("&orderIDValue", orderIDValue);
		String requestTemplate2 = requestTemplate1.replaceAll("&banner", banner);

		String url = getProperty("getOrderDetails" + environment);

		String finalrequest = requestTemplate2;

		String name = getCredentialProperty("UserCredentials");
		String password = getCredentialProperty("PWDCredentials");
		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(finalrequest);
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/xml");
		httpPost.setHeader("Content-type", "application/xml");
		httpPost.setHeader("Authorization", "Basic  " + authStringEnc);

		ResponseHandler<String> handler = new BasicResponseHandler();

		String body = client.execute(httpPost, handler);

		client.close();

		System.out.println(body);

		Multimap<String, String> AttributeValue = XMLConvertor(body);
		return AttributeValue;

	}

	public Multimap<String, String> XMLConvertor(String body)
			throws JSONException, JsonParseException, JsonMappingException, IOException, JDOMException {

		SAXBuilder saxBuilder = new SAXBuilder();

		InputStream stream = new ByteArrayInputStream(body.getBytes("UTF-8"));

		System.out.println(body);
		Document document = saxBuilder.build(stream);
		Element classElement = document.getRootElement();
		List<Element> Values = classElement.getChildren();
		List<Attribute> ValueNodes = classElement.getAttributes();

		//HashMap<String, String> AttributesValues = new HashMap<String, String>();
		
		//Multimap<String, String> AttributesValues = ArrayListMultimap.create();
		

		ListMultimap<String, String> AttributesValues = Multimaps.newListMultimap(
				  new TreeMap<String, Collection<String>>(),
				  new Supplier<List<String>>() {
				    public List<String> get() {
				      return Lists.newArrayList();
				    }
				  });

		for (int temp = 0; temp < Values.size(); temp++) {
			Element Value = Values.get(temp);
			System.out.println("\nCurrent Element :" + Value.getName());

			List<Attribute> Attributes = Value.getAttributes();
			List<Element> Child = Value.getChildren();
			if(!Child.isEmpty())
			{
				
				for(Element f: Child)
				{
				
					List<Attribute> ChildAttributes = f.getAttributes();
					List<Element> GrandChild = f.getChildren();
					
					if(!GrandChild.isEmpty())
					{
						
						for(Element S: GrandChild)
							
						{
						List<Attribute> GrandChildAttributes = S.getAttributes();
						

						for(Attribute A:GrandChildAttributes)
						{
							

							if(A.getValue().isEmpty()) {continue;}
							AttributesValues.put(A.getName(), A.getValue());
							
						}
						
						
						}
					}
					
					for(Attribute g:ChildAttributes)
					{
						
						if(g.getValue().isEmpty()) {continue;}
						AttributesValues.put(g.getName(), g.getValue());
						
					}
					
				}
				
				
			}
			

			for (Attribute e : Attributes) {

				if(e.getValue().isEmpty()) {continue;}
				AttributesValues.put(e.getName(), e.getValue());

			}

		}

		return AttributesValues;

	}

}