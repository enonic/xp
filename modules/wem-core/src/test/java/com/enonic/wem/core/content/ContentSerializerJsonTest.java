package com.enonic.wem.core.content;


public class ContentSerializerJsonTest
    extends AbstractContentSerializerTest
{
    @Override
    ContentSerializer getSerializer()
    {
        ContentSerializerJson serializerJson = new ContentSerializerJson( contentTypeFetcher );
        serializerJson.prettyPrint();
        return serializerJson;
    }

}
