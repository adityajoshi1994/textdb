package edu.uci.ics.textdb.exp.asterix;



public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AsterixReader reader = new AsterixReader();
		try {
			reader.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
