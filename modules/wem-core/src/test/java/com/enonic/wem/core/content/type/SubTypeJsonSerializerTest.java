package com.enonic.wem.core.content.type;

import static org.junit.Assert.*;

public class SubTypeJsonSerializerTest
    extends AbstractSubTypeSerializerTest
{
    @Override
    SubTypeSerializer getSerializer()
    {
        final SubTypeJsonSerializer subTypeJsonSerializer = new SubTypeJsonSerializer();
        subTypeJsonSerializer.prettyPrint();
        return subTypeJsonSerializer;
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }
}
