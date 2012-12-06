package com.enonic.wem.core.content.type;

import static org.junit.Assert.*;

public class SubTypeXmlSerializerTest
    extends AbstractSubTypeSerializerTest
{
    @Override
    SubTypeSerializer getSerializer()
    {
        return new SubTypeXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
