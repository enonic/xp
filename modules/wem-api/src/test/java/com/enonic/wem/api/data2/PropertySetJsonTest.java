package com.enonic.wem.api.data2;


import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

import com.enonic.wem.api.support.JsonTestHelper;

import static org.junit.Assert.*;

public class PropertySetJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public PropertySetJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
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
        PropertyArrayJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, PropertyArrayJson.class );

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
        dataSet.setHtmlPart( "HtmlPart", "<div></div>" );
        PropertyArrayJson dataSetJson = PropertyArrayJson.toJson( tree.getRoot().getPropertyArray( "mySet" ) );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( dataSetJson );

        System.out.println( expectedSerialization );

        // de-serialize
        PropertyArrayJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, PropertyArrayJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    /*@Test
    public void deserialize_serialization_of_RootDataSet()
        throws IOException
    {
        RootDataSet dataPropertyValue = new RootDataSet();
        dataPropertyValue.setProperty( "a", Value.newString( "1" ) );
        dataPropertyValue.setProperty( "b", Value.newString( "2" ) );
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "mydata", Value.newData( dataPropertyValue ) );
        RootDataSetJson rootDataSetJson = new RootDataSetJson( rootDataSet );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( rootDataSetJson );

        System.out.println( expectedSerialization );

        // de-serialize
        RootDataSetJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, RootDataSetJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    */
}
