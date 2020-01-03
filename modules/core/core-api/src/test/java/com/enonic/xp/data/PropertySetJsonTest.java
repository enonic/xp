package com.enonic.xp.data;


import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertySetJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public PropertySetJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this );
    }

    @Test
    public void deserialize_serialization_of_Property()
        throws IOException
    {
        PropertyTree tree = new PropertyTree();
        tree.addString( "myProp", "a" );
        tree.addString( "myProp", "b" );
        tree.addString( "myProp", "c" );
        PropertyArrayJson propertyArrayJson = PropertyArrayJson.toJson( tree.getRoot().getPropertyArray( "myProp" ) );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( propertyArrayJson );

        System.out.println( expectedSerialization );

        // de-serialize
        PropertyArrayJson parsedData = jsonTestHelper.objectReader().forType( PropertyArrayJson.class ).readValue( expectedSerialization );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_DataSet()
        throws IOException
    {
        PropertyTree tree = new PropertyTree();
        PropertySet dataSet = tree.addSet( "mySet" );
        dataSet.setLong( "Long", 1L );
        dataSet.setDouble( "Double", 1.1 );
        dataSet.setLocalDate( "DateMidnight", LocalDate.of( 2012, 12, 12 ) );
        dataSet.setXml( "Xml", "<div></div>" );
        PropertyArrayJson dataSetJson = PropertyArrayJson.toJson( tree.getRoot().getPropertyArray( "mySet" ) );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( dataSetJson );

        System.out.println( expectedSerialization );

        // de-serialize
        PropertyArrayJson parsedData = jsonTestHelper.objectReader().forType( PropertyArrayJson.class ).readValue( expectedSerialization );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }
}
