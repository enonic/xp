package com.enonic.wem.core.content.schema.mixin;

import static org.junit.Assert.*;

public class MixinJsonSerializerTest
    extends AbstractMixinSerializerTest
{
    @Override
    MixinSerializer getSerializer()
    {
        final MixinJsonSerializer mixinJsonSerializer = new MixinJsonSerializer();
        mixinJsonSerializer.prettyPrint();
        return mixinJsonSerializer;
    }

    @Override
    void assertSerializedResult( final String fileNameForExpected, final String actualSerialization )
    {
        assertEquals( loadJsonAsString( fileNameForExpected + ".json" ), actualSerialization );
    }
}
