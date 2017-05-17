package edu.uci.ics.textdb.api.schema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.uci.ics.textdb.api.constants.JsonConstants;

public class Schema {
    private List<Attribute> attributes;
    private Map<String, Integer> attributeNameVsIndex;

    public Schema(Attribute... attributes) {
        this(Arrays.asList(attributes));
    }
    
    @JsonCreator
    public Schema(
            @JsonProperty(value = JsonConstants.ATTRIBUTES, required = true)
            List<Attribute> attributes) {
        this.attributes = attributes;
        populateAttributeNameVsIndexMap();
    }

    private void populateAttributeNameVsIndexMap() {
        attributeNameVsIndex = new HashMap<String, Integer>();
        for (int count = 0; count < attributes.size(); count++) {
            String attributeName = attributes.get(count).getAttributeName();
            attributeNameVsIndex.put(attributeName.toLowerCase(), count);
        }
    }

    @JsonProperty(value = JsonConstants.ATTRIBUTES)
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    @JsonIgnore
    public List<String> getAttributeNames() {
        return attributes.stream().map(attr -> attr.getAttributeName()).collect(Collectors.toList());
    }

    public Integer getIndex(String attributeName) {
        return attributeNameVsIndex.get(attributeName.toLowerCase());
    }
    
    public Attribute getAttribute(String attributeName) {
        Integer attrIndex = getIndex(attributeName);
        if (attrIndex == null) {
            return null;
        }
        return attributes.get(attrIndex);
    }

    @JsonIgnore
    public boolean containsField(String attributeName) {
        return attributeNameVsIndex.keySet().contains(attributeName.toLowerCase());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((attributeNameVsIndex == null) ? 0 : attributeNameVsIndex.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schema other = (Schema) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (attributeNameVsIndex == null) {
            if (other.attributeNameVsIndex != null)
                return false;
        } else if (!attributeNameVsIndex.equals(other.attributeNameVsIndex))
            return false;
        return true;
    }
}
