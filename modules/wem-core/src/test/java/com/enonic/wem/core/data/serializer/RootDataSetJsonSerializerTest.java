package com.enonic.wem.core.data.serializer;


import org.junit.Test;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.SerializingTestHelper;

import static org.junit.Assert.*;

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
        assertEquals( "Serialization not as expected", testHelper.loadJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
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
}
