package com.enonic.wem.api.content.data;


import org.junit.Test;

import static org.junit.Assert.*;

public class RootDataSetTest
{
    @Test
    public void root()
    {
        RootDataSet rootDataSet = DataSet.newRootDataSet();

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

