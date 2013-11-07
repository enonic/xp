package com.enonic.wem.admin.json.data;


import java.io.IOException;

import org.joda.time.DateMidnight;
import org.junit.Ignore;
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
        PropertyJson propertyJson = new PropertyJson( new Property.String( "A", "1" ) );

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
        dataSet.setProperty( "Long", new Value.Long( 1 ) );
        dataSet.setProperty( "Double", new Value.Double( 1.1 ) );
        dataSet.setProperty( "DateMidnight", new Value.DateMidnight( new DateMidnight( 2012, 12, 12 ) ) );
        dataSet.setProperty( "HtmlPart", new Value.HtmlPart( "<div></div>" ) );
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
    @Ignore
    public void deserialize_serialization_of_RootDataSet()
        throws IOException
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "Long", new Value.Long( 1 ) );
        rootDataSet.setProperty( "Double", new Value.Double( 1.1 ) );
        rootDataSet.setProperty( "DateMidnight", new Value.DateMidnight( new DateMidnight( 2012, 12, 12 ) ) );
        rootDataSet.setProperty( "HtmlPart", new Value.HtmlPart( "<div></div>" ) );
        RootDataSetJson dataSetJson = new RootDataSetJson( rootDataSet );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( dataSetJson );

        System.out.println( expectedSerialization );

        // de-serialize
        DataJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, DataJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }
}
