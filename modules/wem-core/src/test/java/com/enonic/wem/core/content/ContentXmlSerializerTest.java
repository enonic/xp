package com.enonic.wem.core.content;


public class ContentXmlSerializerTest
    extends AbstractContentSerializerTest
{
    @Override
    ContentSerializer getSerializer()
    {
        return new ContentXmlSerializer().prettyPrint( true );
    }
}
