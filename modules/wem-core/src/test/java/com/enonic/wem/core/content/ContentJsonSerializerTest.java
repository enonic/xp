package com.enonic.wem.core.content;


public class ContentJsonSerializerTest
    extends AbstractContentSerializerTest
{
    @Override
    ContentSerializer getSerializer()
    {
        ContentJsonSerializer serializerJson = new ContentJsonSerializer();
        serializerJson.prettyPrint();
        return serializerJson;
    }

}
