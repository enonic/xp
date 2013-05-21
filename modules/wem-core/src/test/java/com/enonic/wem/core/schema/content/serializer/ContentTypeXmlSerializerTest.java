package com.enonic.wem.core.schema.content.serializer;

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
        assertEquals( loadTestXml( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
