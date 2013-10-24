package com.enonic.wem.api.data;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class RootDataSetTest
{

    @Test(expected = UnsupportedOperationException.class)
    public void setParent_throws_exception()
    {
        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setParent( DataSet.newDataSet().name( "myDataSet" ).build() );
    }

    @Test
    public void root()
    {
        RootDataSet rootDataSet = new RootDataSet();

        // is root
        assertEquals( true, rootDataSet.isRoot() );

        // is a DataSet
        assertEquals( true, rootDataSet.isDataSet() );
        assertEquals( false, rootDataSet.isProperty() );

        // have no DataId
        assertEquals( null, rootDataSet.getDataId() );

        // name is blank
        assertEquals( "", rootDataSet.getName() );

        // no parent
        assertEquals( null, rootDataSet.getParent() );

        // is not a part of an array
        assertEquals( false, rootDataSet.isArray() );
        assertEquals( null, rootDataSet.getArray() );
        assertEquals( -1, rootDataSet.getArrayIndex() );
    }

    @Test
    public void valueEquals_given_RootDataSet_with_equal_DataSet_then_true_is_returned()
    {
        RootDataSet rootA = new RootDataSet();
        DataSet setA = new DataSet( "a" );
        setA.setProperty( "p1", new Value.String( "v1" ) );
        rootA.add( setA );

        RootDataSet rootB = new RootDataSet();
        DataSet setB = new DataSet( "a" );
        setB.setProperty( "p1", new Value.String( "v1" ) );
        rootB.add( setB );

        assertTrue( rootA.valueEquals( rootB ) );
    }

    @Test
    public void valueEquals_given_RootDataSet_with_DataSet_having_equal_data_but_unequal_name_then_false_is_returned()
    {
        RootDataSet rootA = new RootDataSet();
        DataSet setA = new DataSet( "a" );
        setA.setProperty( "p1", new Value.String( "v1" ) );
        rootA.add( setA );

        RootDataSet rootB = new RootDataSet();
        DataSet setB = new DataSet( "b" );
        setB.setProperty( "p1", new Value.String( "v1" ) );
        rootB.add( setB );

        assertFalse( rootA.valueEquals( rootB ) );
    }

    @Test
    public void valueEquals_given_RootDataSet_with_DataSet_having_unequal_data_but_equal_name_then_false_is_returned()
    {
        RootDataSet rootA = new RootDataSet();
        DataSet setA = new DataSet( "a" );
        setA.setProperty( "p1", new Value.String( "v1" ) );
        rootA.add( setA );

        RootDataSet rootB = new RootDataSet();
        DataSet setB = new DataSet( "a" );
        setB.setProperty( "p1", new Value.String( "v2" ) );
        rootB.add( setB );

        assertFalse( rootA.valueEquals( rootB ) );
    }
}
