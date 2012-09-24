package com.enonic.wem.core.content.type;

import com.enonic.wem.api.content.type.ContentTypeSerializer;

public class ContentTypeSerializerXmlTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeSerializerXml().prettyPrint( true );
    }
}
