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
        rootDataSet.setProperty( "myProp", Value.newString( "a" ) );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_single", serialized );
    }

    @Test
    public void property_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( "myProp", Value.newString( "a" ) );
        rootDataSet.addProperty( "myProp", Value.newString( "b" ) );
        rootDataSet.addProperty( "myProp", Value.newString( "c" ) );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_array", serialized );
    }

    @Test
    public void single_dataSet()
    {
        RootDataSet rootDataSet = new RootDataSet();
        DataSet dataSet = new DataSet( "mySet" );
        dataSet.addProperty( "myProp", Value.newString( "a" ) );
        rootDataSet.add( dataSet );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "dataSet_single", serialized );
    }

    @Test
    public void dataSet_array()
    {
        RootDataSet rootDataSet = new RootDataSet();
        DataSet dataSet1 = new DataSet( "mySet" );
        dataSet1.addProperty( "myProp", Value.newString( "a" ) );
        rootDataSet.add( dataSet1 );
        DataSet dataSet2 = new DataSet( "mySet" );
        dataSet2.addProperty( "myProp", Value.newString( "b" ) );
        rootDataSet.add( dataSet2 );
        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "dataSet_array", serialized );
    }

    @Test
    public void property_with_DataSet()
    {
        RootDataSet regionMain = new RootDataSet();
        regionMain.setProperty( "a", Value.newString( "1" ) );

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.addProperty( "regionMain", new Value.Data( regionMain ) );

        String serialized = serializer.toString( rootDataSet );
        assertSerializedResult( "property_with_DataSet", serializer.toString( rootDataSet ) );

        RootDataSet parsedRootDataSet = serializer.parse( serialized );
        RootDataSet parsedData = parsedRootDataSet.getProperty( "regionMain" ).getData();
        assertEquals( "1", parsedData.getProperty( "a" ).getString() );
    }
}
