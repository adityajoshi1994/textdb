package edu.uci.ics.textdb.exp.comparablematcher;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NumberMatchingType {
    EQUAL_TO("="),

    GREATER_THAN(">"),

    GREATER_THAN_OR_EQUAL_TO(">="),

    LESS_THAN("<"),

    LESS_THAN_OR_EQUAL_TO("<="),

    NOT_EQUAL_TO("!=");
    
    private final String name;
    
    private NumberMatchingType(String name) {
        this.name = name;
    }
    
    // use the name string instead of enum string in JSON
    @JsonValue
    public String getName() {
        return this.name;
    }
    
}
