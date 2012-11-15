package com.enonic.wem.core.content.type;

public class ContentTypeXmlSerializerTest
    extends AbstractContentTypeSerializerTest
{
    @Override
    ContentTypeSerializer getSerializer()
    {
        return new ContentTypeXmlSerializer().prettyPrint( true );
    }
}
