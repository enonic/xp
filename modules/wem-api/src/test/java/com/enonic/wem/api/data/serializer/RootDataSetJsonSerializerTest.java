package com.enonic.wem.api.data.serializer;


import org.junit.Assert;
import org.junit.Test;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.SerializingTestHelper;

import static junit.framework.Assert.assertEquals;

public class RootDataSetJsonSerializerTest
{
    private SerializingTestHelper testHelper;

    private RootDataSetJsonSerializer serializer;

    public RootDataSetJsonSerializerTest()
    {
        testHelper = new SerializingTestHelper( this, true );
        serializer = new RootDataSetJsonSerializer( testHelper.objectMapper() );
        serializer.prettyPrint();
    }

    private void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        Assert.assertEquals( "Serialization not as expected", testHelper.loadJsonAsString( fileNameForExpected + ".json" ),
                             actualSerialization );
    }

    @Test
    public void single_property()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "myProp", new Value.String( "a" ) );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_single", serialized );
    }

    @Test
    public void property_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( "myProp", new Value.String( "a" ) );
        rootDataSet.addProperty( "myProp", new Value.String( "b" ) );
        rootDataSet.addProperty( "myProp", new Value.String( "c" ) );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_array", serialized );
    }

    @Test
    public void single_dataSet()
    {
        RootDataSet rootDataSet = new RootDataSet();
        DataSet dataSet = new DataSet( "mySet" );
        dataSet.addProperty( "myProp", new Value.String( "a" ) );
        rootDataSet.add( dataSet );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "dataSet_single", serialized );
    }

    @Test
    public void dataSet_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        DataSet dataSet1 = new DataSet( "mySet" );
        dataSet1.addProperty( "myProp", new Value.String( "a" ) );
        rootDataSet.add( dataSet1 );
        DataSet dataSet2 = new DataSet( "mySet" );
        dataSet2.addProperty( "myProp", new Value.String( "b" ) );
        rootDataSet.add( dataSet2 );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "dataSet_array", serialized );
    }

    @Test
    public void property_with_DataSet()
    {
        RootDataSet value = new RootDataSet();
        value.setProperty( "a", new Value.String( "1" ) );
        value.setProperty( "set.b", new Value.String( "2" ) );

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( "myData", new Value.Data( value ) );

        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_with_DataSet", serializer.toString( rootDataSet ) );

        RootDataSet parsedRootDataSet = serializer.parse( serialized );
        RootDataSet parsedData = parsedRootDataSet.getProperty( "myData" ).getData();
        assertEquals( "1", parsedData.getProperty( "a" ).getString() );
        assertEquals( "2", parsedData.getProperty( "set.b" ).getString() );
    }
}
