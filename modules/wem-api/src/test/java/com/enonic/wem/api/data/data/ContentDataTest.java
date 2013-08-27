package com.enonic.wem.api.data.data;


import org.junit.Test;

import static org.junit.Assert.*;

public class ContentDataTest
{
    @Test(expected = UnsupportedOperationException.class)
    public void setParent_throws_exception()
    {
        ContentData contentData = new ContentData();
        contentData.setParent( DataSet.newDataSet().name( "myDataSet" ).build() );
    }

    @Test
    public void root()
    {
        ContentData contentData = new ContentData();

        // is root
        assertEquals( true, contentData.isRoot() );

        // is a DataSet
        assertEquals( true, contentData.isDataSet() );
        assertEquals( false, contentData.isProperty() );

        // have no DataId
        assertEquals( null, contentData.getDataId() );

        // name is blank
        assertEquals( "", contentData.getName() );

        // no parent
        assertEquals( null, contentData.getParent() );

        // is not a part of an array
        assertEquals( false, contentData.isArray() );
        assertEquals( null, contentData.getArray() );
        assertEquals( -1, contentData.getArrayIndex() );
    }

}

