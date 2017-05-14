package edu.uci.ics.textdb.exp.asterix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uci.ics.textdb.api.field.IntegerField;
import edu.uci.ics.textdb.api.field.StringField;
import edu.uci.ics.textdb.api.field.TextField;
import edu.uci.ics.textdb.api.tuple.Tuple;
import edu.uci.ics.textdb.perftest.utils.PerfTestUtils;
import edu.uci.ics.textdb.storage.DataWriter;
import edu.uci.ics.textdb.storage.RelationManager;
import edu.uci.ics.textdb.storage.constants.LuceneAnalyzerConstants;

public class TwitterSample {
    
    public static String twitterFilePath = PerfTestUtils.getResourcePath("/sample-data-files/twitter/tweets.json");
    public static String twitterClimateTable = "twitter";
    
//    public static void main(String[] args) throws Exception {
//        writeTwitterIndex();
//    }
    
    public static List<Tuple> getTweetTupleList(String jsonTweets) throws Exception{
    	ArrayList<Tuple> tupleList = new ArrayList<Tuple>();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonTweets);
        for(JsonNode dsTweetNode : jsonNode){
	        for (JsonNode tweet : dsTweetNode) {
	            try {
	                String text = tweet.get("text").asText();
	                Long id = tweet.get("id").asLong();
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
	            } catch (RuntimeException e) {
	                e.printStackTrace();
	                continue;
	            }
	        }
        }
    	return tupleList;
    }
    
    // Assuming input always have the correct schema
    public static void writeTwitterIndex(List<Tuple> tupleList) throws Exception {
    	if(tupleList == null || tupleList.size() == 0)
    		return;
        RelationManager relationManager = RelationManager.getRelationManager();
        relationManager.deleteTable(twitterClimateTable);
        relationManager.createTable(twitterClimateTable, "../index/twitter/", TwitterSchema.TWITTER_SCHEMA, 
                LuceneAnalyzerConstants.standardAnalyzerString());
        
        DataWriter dataWriter = relationManager.getTableDataWriter(twitterClimateTable);
        dataWriter.open();
        
        for(Tuple tuple : tupleList){
        	dataWriter.insertTuple(tuple);
        }
        dataWriter.close();
        System.out.println("write twitter data finished");
        System.out.println(tupleList.size() + " tweets written");
    }

}
