package com.enonic.wem.core.content.type;

public class ContentTypeSerializerJsonTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeSerializerJson();
    }
}
