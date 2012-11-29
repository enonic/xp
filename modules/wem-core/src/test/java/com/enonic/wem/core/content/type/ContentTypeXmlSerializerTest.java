package com.enonic.wem.core.content.type;

import static org.junit.Assert.*;

public class ContentTypeXmlSerializerTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
