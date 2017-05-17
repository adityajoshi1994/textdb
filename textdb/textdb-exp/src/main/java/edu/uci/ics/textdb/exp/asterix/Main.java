package edu.uci.ics.textdb.exp.asterix;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import edu.uci.ics.textdb.api.field.IDField;
import edu.uci.ics.textdb.api.field.IField;
import edu.uci.ics.textdb.api.field.IntegerField;
import edu.uci.ics.textdb.api.field.StringField;
import edu.uci.ics.textdb.api.field.TextField;
import edu.uci.ics.textdb.api.tuple.Tuple;

public class Main {
	public static void main(String[] args){
		int limit = 11;
		AsterixReader asterixReader = new AsterixReader(new AsterixReaderPredicate());
		asterixReader.open();
		asterixReader.setLimit(limit);
		asterixReader.processTuples();
		String tweets = ;
		ArrayList<Tuple>
		for(Tuple t:tupleList){
			
			List<IField> tupleFieldList = new ArrayList<>();
            // Generate the new UUID.
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
            
            return new Tuple(outputSchema, tupleFieldList);
			
		}
		
	}
}
