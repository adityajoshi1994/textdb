package edu.uci.ics.textdb.exp.asterix;

import org.junit.Before;
import org.junit.Test;


public class AsterixReaderTest {
	private AsterixReader asterixReader;
	
	@Before
	public void setup(){
		asterixReader = new AsterixReader(new AsterixReaderPredicate());
	}
	
	
	@Test
	public void writeTweets(){
		int limit = 1;
		asterixReader.open();
		asterixReader.setLimit(limit);
		asterixReader.processTuples();
		assert(asterixReader.getTweetTupleList().size() == limit);
		asterixReader.close();
	}
}
