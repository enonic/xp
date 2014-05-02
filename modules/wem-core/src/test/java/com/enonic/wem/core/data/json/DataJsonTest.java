package com.enonic.wem.core.data.json;


import java.io.IOException;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class DataJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public DataJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    @Test
    public void deserialize_serialization_of_Property()
        throws IOException
    {
        PropertyJson propertyJson = new PropertyJson( Property.newString( "A", "1" ) );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( propertyJson );

        System.out.println( expectedSerialization );

        // de-serialize
        DataJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, DataJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_DataSet()
        throws IOException
    {
        DataSet dataSet = new DataSet( "mySet" );
        dataSet.setProperty( "Long", Value.newLong( 1 ) );
        dataSet.setProperty( "Double", Value.newDouble( 1.1 ) );
        dataSet.setProperty( "DateMidnight", Value.newDateMidnight( new DateMidnight( 2012, 12, 12 ) ) );
        dataSet.setProperty( "HtmlPart", Value.newHtmlPart( "<div></div>" ) );
        DataSetJson dataSetJson = new DataSetJson( dataSet );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( dataSetJson );

        System.out.println( expectedSerialization );

        // de-serialize
        DataJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, DataJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
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
}
