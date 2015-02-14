package com.enonic.xp.core.data;


import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

import com.enonic.xp.core.data.PropertyArrayJson;
import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.support.JsonTestHelper;

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
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
}
