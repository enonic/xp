package com.enonic.wem.core.content.type;

public class ContentTypeSerializerXmlTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeSerializerXml().prettyPrint( true );
    }
}
