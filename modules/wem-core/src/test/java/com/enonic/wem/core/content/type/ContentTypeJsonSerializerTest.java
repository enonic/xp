package com.enonic.wem.core.content.type;

public class ContentTypeJsonSerializerTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeJsonSerializer();
    }
}
