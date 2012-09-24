package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.ContentTypeSerializer;

public class ContentTypeSerializerJsonTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeSerializerJson();
    }
}
