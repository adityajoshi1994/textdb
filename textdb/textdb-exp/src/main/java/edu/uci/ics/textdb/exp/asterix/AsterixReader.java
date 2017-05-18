package edu.uci.ics.textdb.exp.asterix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.naming.spi.DirStateFactory.Result;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import edu.uci.ics.textdb.api.dataflow.IOperator;
import edu.uci.ics.textdb.api.dataflow.ISink;
import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.tuple.Tuple;

public class AsterixReader implements ISink {
	private static final String asterixDB_URL = "http://localhost:19002/query/service";
	
	private AsterixReaderPredicate predicate;
	private String query;
	private Integer limit;	// number of lines that will be returned by the query
	private List<Tuple> tweetsTupleList;
	private int curser = CLOSED;
	private String stringResults;
	
	AsterixReader(AsterixReaderPredicate predicate){
		this.predicate = predicate;
		tweetsTupleList = new ArrayList<Tuple>();
		curser = CLOSED;
	}

	public void updateQuery(){
		query = "use twitter;"
				+ "select * from ds_tweet limit "+limit+";";
	}
		
	public Integer getLimit(){
		return limit;
	}
	
	public List<Tuple> getTweetTupleList(){
		return tweetsTupleList;
	}
	
	/**
	 * @Notice: new limit will be updated by update query.
	 * 			updateQuery is called in the beginning of processTuples.
	 * @param newLimit
	 */
	public void setLimit(Integer newLimit){
		limit = newLimit;
	}

	@Override
	public Schema getOutputSchema() {
		return TwitterSchema.TWITTER_SCHEMA;
	}

	@Override
	public void open() throws TextDBException {
		this.limit = predicate.getLimit();
		curser = OPENED;
	}

	@Override
	public void processTuples() throws TextDBException {
		if(curser == CLOSED)
			return;
		updateQuery();
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(asterixDB_URL);

		// set post parameters
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("statement", query));
		urlParameters.add(new BasicNameValuePair("pretty", "true"));
		urlParameters.add(new BasicNameValuePair("client_context_id", "xyz"));

		// load parameters
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			throw new TextDBException("AsterixReader::run. UnsupportedEncodingException");
		}

		// connect to the server
		HttpResponse response;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			throw new TextDBException("AsterixReader::run. ClientProtocolException");
		} catch (IOException e) {
			throw new TextDBException("AsterixReader::run. IOException");
		}

		// load response to buffer
		BufferedReader rd;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e) {
			throw new TextDBException("AsterixReader::run. UnsupportedOperationException");
		} catch (IOException e) {
			throw new TextDBException("AsterixReader::run. IOException");
		}
		
		// parse json and get query results 
		JsonReader jsonReader = Json.createReader(rd);
        JsonObject jsonObject = jsonReader.readObject();
        JsonValue jsonTweets = jsonObject.get("results");
        jsonReader.close();
        try {
        	stringResults = jsonTweets.toString();
			tweetsTupleList = TwitterSample.getTweetTupleList(stringResults);
		} catch (Exception e) {
			throw new TextDBException(e.getMessage());
		}		
	}

	// write tweets into index files
	@Override
	public void close() throws TextDBException {
		if(curser == CLOSED){
			return;
		}
		try{
			TwitterSample.writeTwitterIndex(tweetsTupleList);
			curser = CLOSED;
		} catch(Exception e){
			throw new TextDBException(e.getMessage());
		}
	}
	
	public String getResults(){
		return stringResults;
	}
}
