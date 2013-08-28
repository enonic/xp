package com.enonic.wem.api.data;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
}
