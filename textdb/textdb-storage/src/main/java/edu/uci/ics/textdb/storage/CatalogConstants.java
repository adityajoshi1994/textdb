package edu.uci.ics.textdb.storage;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.textdb.api.exception.StorageException;
import edu.uci.ics.textdb.api.field.IntegerField;
import edu.uci.ics.textdb.api.field.StringField;
import edu.uci.ics.textdb.api.schema.Attribute;
import edu.uci.ics.textdb.api.schema.AttributeType;
import edu.uci.ics.textdb.api.schema.Schema;
import edu.uci.ics.textdb.api.tuple.Tuple;
import edu.uci.ics.textdb.api.utils.Utils;

/**
 * CatalogConstants stores the schema and the initial tuples of the catalog manager
 * 
 * 
 * ============================================
 * 
 * Initial tuples for the table catalog:
 * 
 *  tableName    |    tableDirectory    |    luceneAnalyzer
 * 
 * tableCatalog       ../catalog/table       standardLuceneAnalyzer
 * schemaCatalog      ../catalog/schema      standardLuceneAnalyzer
 *   
 * ============================================
 *   
 *  Initial tuples for the schema catalog. 
 *    tableName    |    attributeName    |  attributeType  |  attributePosition
 *  
 *   tableCatalog       tableName           string                0
 *   tableCatalog    tableDirectory         string                1
 *   tableCatalog     luceneAnalyzer        string                2
 *   schemaCatalog      tableName           string                0
 *   schemaCatalog    attributeName         string                1
 *   schemaCatalog    attributeType         string                2
 *   schemaCatalog  attributePosition       string                3
 *     
 * ============================================
 * 
 * @author Zuozhi Wang
 *
 */
public class CatalogConstants {

    public static final String TABLE_CATALOG = "tableCatalog";
    public static final String SCHEMA_CATALOG = "schemaCatalog";

    public static final String TABLE_CATALOG_DIRECTORY = Paths.get(Utils.getTextdbHomePath(), "catalog", "table").toString();
    public static final String SCHEMA_CATALOG_DIRECTORY = Paths.get(Utils.getTextdbHomePath(), "catalog", "schema").toString();

    // Schema for the "table catalog" table
    public static final String TABLE_NAME = "tableName";
    public static final String TABLE_DIRECTORY = "tableDirectory";
    public static final String TABLE_LUCENE_ANALYZER = "luceneAnalyzer";

    public static final Attribute TABLE_NAME_ATTR = new Attribute(TABLE_NAME, AttributeType.STRING);
    public static final Attribute TABLE_DIRECTORY_ATTR = new Attribute(TABLE_DIRECTORY, AttributeType.STRING);
    public static final Attribute TABLE_LUCENE_ANALYZER_ATTR = new Attribute(TABLE_LUCENE_ANALYZER,
            AttributeType.STRING);

    public static final Schema TABLE_CATALOG_SCHEMA = new Schema(TABLE_NAME_ATTR, TABLE_DIRECTORY_ATTR,
            TABLE_LUCENE_ANALYZER_ATTR);
    public static final Schema TABLE_CATALOG_SCHEMA_WITH_ID = Utils.getSchemaWithID(TABLE_CATALOG_SCHEMA);

    // Schema for "schema catalog" table
    public static final String ATTR_NAME = "attributeName";
    public static final String ATTR_TYPE = "attributeType";
    public static final String ATTR_POSITION = "attributePosition";

    public static final Attribute ATTR_NAME_ATTR = new Attribute(ATTR_NAME, AttributeType.STRING);
    public static final Attribute ATTR_TYPE_ATTR = new Attribute(ATTR_TYPE, AttributeType.STRING);
    public static final Attribute ATTR_POSITION_ATTR = new Attribute(ATTR_POSITION, AttributeType.INTEGER);

    public static final Schema SCHEMA_CATALOG_SCHEMA = new Schema(TABLE_NAME_ATTR, ATTR_NAME_ATTR, ATTR_TYPE_ATTR,
            ATTR_POSITION_ATTR);
    public static final Schema SCHEMA_CATALOG_SCHEMA_WITH_ID = Utils.getSchemaWithID(SCHEMA_CATALOG_SCHEMA);

    
    // DataStore for table catalog and schema catalog
    public static final DataStore TABLE_CATALOG_DATASTORE = 
            new DataStore(TABLE_CATALOG_DIRECTORY, TABLE_CATALOG_SCHEMA_WITH_ID);
    public static final DataStore SCHEMA_CATALOG_DATASTORE = 
            new DataStore(SCHEMA_CATALOG_DIRECTORY, SCHEMA_CATALOG_SCHEMA_WITH_ID);


    /**
     * Gets the tuple to be inserted to the table catalog.
     * 
     * @param tableName
     * @param tableDirectory
     * @param luceneAnalyzerStr
     * @return
     * @throws StorageException
     */
    public static Tuple getTableCatalogTuple(String tableName, String tableDirectory, String luceneAnalyzerStr) {
        return new Tuple(TABLE_CATALOG_SCHEMA, 
                new StringField(tableName), 
                new StringField(tableDirectory),
                new StringField(luceneAnalyzerStr));
    }
    
    /**
     * Gets the tuples to be inserted to the schema catalog.
     * 
     * @param tableName
     * @param tableDirectory
     * @param luceneAnalyzerStr
     * @return
     * @throws StorageException
     */
    public static List<Tuple> getSchemaCatalogTuples(String tableName, Schema tableSchema) {
        List<Tuple> schemaCatalogTuples = new ArrayList<>();
        for (int i = 0; i < tableSchema.getAttributes().size(); i++) {
            Attribute attr = tableSchema.getAttributes().get(i);
            Tuple schemaTuple = new Tuple(SCHEMA_CATALOG_SCHEMA, 
                    new StringField(tableName),
                    new StringField(attr.getAttributeName()),
                    new StringField(attr.getAttributeType().toString().toLowerCase()),
                    new IntegerField(i));
            schemaCatalogTuples.add(schemaTuple);
        }
        return schemaCatalogTuples;
    }

}
