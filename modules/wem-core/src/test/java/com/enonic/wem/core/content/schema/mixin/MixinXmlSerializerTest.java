package com.enonic.wem.core.content.schema.mixin;

import static org.junit.Assert.*;

public class MixinXmlSerializerTest
    extends AbstractMixinSerializerTest
{
    @Override
    MixinSerializer getSerializer()
    {
        return new MixinXmlSerializer().prettyPrint( true );
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( getXmlAsString( fileNameForExpected + ".xml" ), actualSerialization );
    }
}
