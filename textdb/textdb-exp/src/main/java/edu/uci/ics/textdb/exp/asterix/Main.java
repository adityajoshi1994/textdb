package edu.uci.ics.textdb.exp.asterix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.lucene.search.QueryRescorer;
import org.mockito.asm.tree.IntInsnNode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.textdb.api.exception.TextDBException;
import edu.uci.ics.textdb.api.field.IDField;
import edu.uci.ics.textdb.api.field.IField;
import edu.uci.ics.textdb.api.field.IntegerField;
import edu.uci.ics.textdb.api.field.StringField;
import edu.uci.ics.textdb.api.field.TextField;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.tuple.Tuple;

public class Main {
	public static final String asterixDB_URL = "http://localhost:19002/query/service";
	
	public static void main(String[] args) throws JsonProcessingException, IOException{
		String tweetId = "682833636383522817";
		String deletQuery = "use twitter; delete from ds_tweet where id = "+tweetId+";";
		runQuery(deletQuery);
		
		int limit = 1;
		AsterixReader asterixReader = new AsterixReader(new AsterixReaderPredicate());
		asterixReader.open();
		asterixReader.setLimit(limit);
		asterixReader.processTuples();
		String results = asterixReader.getResults();
		
		ArrayList<Tuple> tupleList = new ArrayList<Tuple>();
		//Two tuples
        JsonNode jsonNode = new ObjectMapper().readTree(results);
        for(JsonNode dsTweetNode : jsonNode){
	        for (JsonNode tweet : dsTweetNode) {
				List<IField> tupleFieldList = new ArrayList<>();
	            // Generate the new UUID.
				String text = tweet.get("text").asText();
	            Long id = Long.parseLong(tweetId);
	            String tweetLink = "https://twitter.com/statuses/" + id;
	            JsonNode userNode = tweet.get("user");
	            String userScreenName = userNode.get("screen_name").asText();
	            String userLink = "https://twitter.com/" + userScreenName;
	            String userName = userNode.get("name").asText();
	            String userDescription = userNode.get("description").asText();
	            Integer userFollowersCount = userNode.get("followers_count").asInt();
	            Integer userFriendsCount = userNode.get("friends_count").asInt();
	            JsonNode geoTagNode = tweet.get("geo_tag");
	            String state = geoTagNode.get("stateName").asText();
	            String county = geoTagNode.get("countyName").asText();
	            String city = geoTagNode.get("cityName").asText();
	            String createAt = tweet.get("create_at").asText();
	            String rawData = tweet.toString();
	            Tuple tuple = new Tuple(TwitterSchema.TWITTER_SCHEMA,
	            		
	                    new TextField(text),
	                    new StringField(tweetLink),
	                    new StringField(userLink),
	                    new TextField(userScreenName),
	                    new TextField(userName),
	                    new TextField(userDescription),
	                    new IntegerField(userFollowersCount),
	                    new IntegerField(userFriendsCount),
	                    new TextField(state),
	                    new TextField(county),
	                    new TextField(city),
	                    new StringField(createAt),
	                    new StringField(rawData));
	            tupleList.add(tuple);
	        }
		}
        
	        //INPUT : a tupleList
	        /*
	         * Tuple processing logic
	         * 
	         * */
	        // get newly added fields names
	        HashSet<String> buildInSchema = new HashSet<String>();
	        List<String> newSchema = tupleList.get(0).getSchema().getAttributeNames();
	        List<Integer> newFieldsIndex = new ArrayList<Integer>();
	        int buildInSize = TwitterSchema.TWITTER_SCHEMA.getAttributes().size();
	        for(String attrName : TwitterSchema.TWITTER_SCHEMA.getAttributeNames()){
	        	buildInSchema.add(attrName);
	        }
	        for(int i = 0; i < newSchema.size(); i++){
	        	if(!buildInSchema.contains(newSchema.get(i))){
	        		newFieldsIndex.add(i);
	        	}
	        }
        
	        ArrayList<String> queryList = new ArrayList<String>();
	        for(Tuple tuple : tupleList){
	            IField rawDatafield = tuple.getField(buildInSize - 1);	// last field: rawData
	            String rawTweet = rawDatafield.getValue().toString();
	            // add date format
	            String rawTweetWithDate = "";
	            String[] tempList = rawTweet.split("create_at\":\"");      
	
	            rawTweetWithDate += tempList[0]  
            					+ "create_at\":datetime(\"" + tempList[1].substring(0, 25) + ")"+tempList[1].substring(25)
            					+ "create_at\":date(\"" + tempList[2].substring(0, 11) + ")" + tempList[2].substring(11); 
            
	            // generate final tweet
	            String finalTweet = rawTweetWithDate;
	            for(Integer idx : newFieldsIndex){
	            	IField field = tuple.getField(idx); 
	            	String attachment = "\"" + newSchema.get(idx).toString() + "\":\"" + field.getValue() + "\",";
	            	finalTweet = finalTweet.substring(0, 1) + attachment + finalTweet.substring(1);
	            }
            
	            // generate upsert query
	            String query = "use twitter; upsert into ds_tweet (" + finalTweet +");";
	            
	            //TODO:: remove:  "hashtags":["HappyNewYear"]   , ASX0001: Field type orderedlist can't be promoted to type unorderedlist [HyracksDataException]
	            //TODO:: modify to:  "bounding_box": rectangle("-115.384091,36.129459 -115.062159,36.336251")	
	            System.out.println(query);
	            queryList.add(query);
        	}
        
	        // Testing
	        for(String q : queryList){
	        	runQuery(q);
	        }
		
		}
	
	
	public static void runQuery(String query){
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
			System.out.println("Response: "+response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			throw new TextDBException("AsterixReader::run. ClientProtocolException");
		} catch (IOException e) {
			throw new TextDBException("AsterixReader::run. IOException");
		}
	}
}
