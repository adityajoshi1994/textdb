package edu.uci.ics.textdb.exp.asterix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class AsterixReader {
	public static final String asterixDB_URL = "http://localhost:19002/query/service";
	public static void run() throws ClientProtocolException, IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(asterixDB_URL);

		// add header
		post.setHeader("User-Agent", "Mozilla/5.0");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		String query = "show tables;";
		urlParameters.add(new BasicNameValuePair("statement", query));
		urlParameters.add(new BasicNameValuePair("pretty", "true"));
		urlParameters.add(new BasicNameValuePair("client_context_id", "xyz"));
//		urlParameters.add(new BasicNameValuePair("caller", ""));
//		urlParameters.add(new BasicNameValuePair("num", "12345"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("HTTP Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		JsonReader jsonReader = Json.createReader(rd);
        JsonObject jsonObject = jsonReader.readObject();
        JsonValue jsonResult = jsonObject.get("results");
        jsonReader.close();

        System.out.println("JsonValue: "+jsonResult);
        System.out.println("JsonObject: "+ jsonObject.toString());


    }
}
