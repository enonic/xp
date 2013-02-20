package com.enonic.wem.core.content.schema.type;

import static org.junit.Assert.*;

public class ContentTypeJsonSerializerTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();
        contentTypeJsonSerializer.prettyPrint();
        return contentTypeJsonSerializer;
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }
}
