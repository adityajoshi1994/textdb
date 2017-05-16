package edu.uci.ics.textdb.api;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import edu.uci.ics.textdb.api.constants.JsonConstants;
import edu.uci.ics.textdb.api.field.DateField;
import edu.uci.ics.textdb.api.field.DoubleField;
import edu.uci.ics.textdb.api.field.IDField;
import edu.uci.ics.textdb.api.field.IntegerField;
import edu.uci.ics.textdb.api.field.ListField;
import edu.uci.ics.textdb.api.field.TextField;
import edu.uci.ics.textdb.api.schema.Attribute;
import edu.uci.ics.textdb.api.schema.AttributeType;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.span.Span;
import edu.uci.ics.textdb.api.tuple.Tuple;
import edu.uci.ics.textdb.api.utils.TestUtils;
import junit.framework.Assert;

public class JsonSerializationTest {
    
    @Test
    public void testAttributeType() {
        for (AttributeType attributeType : Arrays.asList(AttributeType.values())) {
            TestUtils.testJsonSerialization(attributeType);
        }
    }
    
    @Test
    public void testAttribute() {
        Attribute attribute = new Attribute("attrName", AttributeType.TEXT);
        TestUtils.testJsonSerialization(attribute);
    }
    
    @Test
    public void testSchema() {
        Schema schema = new Schema(Arrays.asList(
                new Attribute("_id", AttributeType._ID_TYPE),
                new Attribute("text", AttributeType.TEXT),
                new Attribute("payload", AttributeType.LIST)));
        TestUtils.testJsonSerialization(schema);
    }
    
    @Test
    public void testSpan() {
        Span span = new Span("attrName", 0, 10, "key", "value", 0);
        TestUtils.testJsonSerialization(span);
    }
    
    @Test
    public void testSpanList() {
        ListField<Span> spanListField = new ListField<Span>(Arrays.asList(
                new Span("attrName", 0, 10, "key1", "value1", 0),
                new Span("attrName", 11, 20, "key2", "value2", 1)));
        TestUtils.testJsonSerialization(spanListField);
    }
    
    @Test
    public void testTextField() {
        TextField textField = new TextField("text field test");
        JsonNode jsonNode = TestUtils.testJsonSerialization(textField);
        Assert.assertTrue(jsonNode.get(JsonConstants.FIELD_VALUE).isTextual());
    }
    
    @Test
    public void testIntegerField() {
        IntegerField integerField = new IntegerField(100);
        JsonNode jsonNode = TestUtils.testJsonSerialization(integerField);
        Assert.assertTrue(jsonNode.get(JsonConstants.FIELD_VALUE).isInt());
    }
    
    @Test
    public void testDoubleField() {
        DoubleField doubleField = new DoubleField(11.11);
        JsonNode jsonNode = TestUtils.testJsonSerialization(doubleField);
        Assert.assertTrue(jsonNode.get(JsonConstants.FIELD_VALUE).isFloatingPointNumber());
    }
    
    @Test
    public void testDateField() {
        DateField dateField = new DateField(new Date());
        TestUtils.testJsonSerialization(dateField);
    } 

    @Test
    public void testTuple() {
        Schema schema = new Schema(Arrays.asList(
                new Attribute("_id", AttributeType._ID_TYPE),
                new Attribute("text", AttributeType.TEXT)));
        Tuple tuple = new Tuple(schema, Arrays.asList(
                IDField.newRandomID(), new TextField("tuple test text")));
        TestUtils.testJsonSerialization(tuple);
    }
    
    
    
}
