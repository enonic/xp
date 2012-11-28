package com.enonic.wem.core.content;


import static org.junit.Assert.*;

public class ContentXmlSerializerTest
    extends AbstractContentSerializerTest
{
    @Override
    ContentSerializer getSerializer()
    {
        return new ContentXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
