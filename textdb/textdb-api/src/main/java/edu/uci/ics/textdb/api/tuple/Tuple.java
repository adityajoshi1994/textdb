package edu.uci.ics.textdb.api.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.uci.ics.textdb.api.constants.JsonConstants;
import edu.uci.ics.textdb.api.field.IField;
import edu.uci.ics.textdb.api.schema.Schema;

/**
 * @author chenli
 * @author sandeepreddy602
 * @author zuozhi
 * 
 * Created on 3/25/16.
 */
@JsonDeserialize(using = TupleJsonDeserializer.class)
public class Tuple {
    private final Schema schema;
    private final List<IField> fields;

    public Tuple(Schema schema, IField... fields) {
        this(schema, Arrays.asList(fields));
    }
    
    @JsonCreator
    public Tuple(
            @JsonProperty(value = JsonConstants.SCHEMA, required = true)
            Schema schema, 
            @JsonProperty(value = JsonConstants.FIELDS, required = true)
            List<IField> fields) {
        this.schema = schema;
        this.fields = fields;
    }
    
    @JsonProperty(value = JsonConstants.SCHEMA)
    public Schema getSchema() {
        return schema;
    }
    
    @JsonProperty(value = JsonConstants.FIELDS)
    public List<IField> getFields() {
        return new ArrayList<>(this.fields);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IField> T getField(int index) {
        return (T) fields.get(index);
    }
    
    public <T extends IField> T getField(int index, Class<T> fieldClass) {
        return getField(index);
    }

    public <T extends IField> T getField(String attributeName) {
        return getField(schema.getIndex(attributeName));
    }
    
    public <T extends IField> T getField(String attributeName, Class<T> fieldClass) {
        return getField(schema.getIndex(attributeName));
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple other = (Tuple) obj;
        if (fields == null) {
            if (other.fields != null)
                return false;
        } else if (!fields.equals(other.fields))
            return false;
        if (schema == null) {
            if (other.schema != null)
                return false;
        } else if (!schema.equals(other.schema))
            return false;
        return true;
    }

    public String toString() {
        return "Tuple [schema=" + schema + ", fields=" + fields + "]";
    }
    
}
