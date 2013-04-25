package com.enonic.wem.api.content.data;


import org.junit.Test;

import static org.junit.Assert.*;

public class ContentDataTest
{
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

