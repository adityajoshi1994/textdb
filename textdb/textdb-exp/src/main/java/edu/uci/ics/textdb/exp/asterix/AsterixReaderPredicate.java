package edu.uci.ics.textdb.exp.asterix;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.uci.ics.textdb.api.dataflow.IOperator;
import edu.uci.ics.textdb.exp.common.PredicateBase;
import edu.uci.ics.textdb.exp.common.PropertyNameConstants;

public class AsterixReaderPredicate extends PredicateBase {

	private Integer limit;	// number of tuples read from asterixDB
	
	AsterixReaderPredicate(){
		this.limit = Integer.MAX_VALUE;
	}
	
	@JsonCreator
	AsterixReaderPredicate(
			@JsonProperty(value = PropertyNameConstants.LIMIT, required = false)
            Integer limit
			){
		this.limit = limit == null || limit < 0 ? Integer.MAX_VALUE : limit;
	}
	
    @JsonProperty(value = PropertyNameConstants.LIMIT)
    public Integer getLimit() {
        return this.limit;
    }

	@Override
	public IOperator newOperator() {
		// TODO Auto-generated method stub
		return new AsterixReader(this);
	}
	
}
