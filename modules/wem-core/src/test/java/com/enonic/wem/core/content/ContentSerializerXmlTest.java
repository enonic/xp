package com.enonic.wem.core.content;


public class ContentSerializerXmlTest
    extends AbstractContentSerializerTest
{
    @Override
    ContentSerializer getSerializer()
    {
        return new ContentSerializerXml( contentTypeFetcher ).prettyPrint( true );
    }
}
