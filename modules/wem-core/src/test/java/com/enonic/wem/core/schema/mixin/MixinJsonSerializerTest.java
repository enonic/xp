package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.support.JsonTestHelper;

import static org.junit.Assert.*;

public class MixinJsonSerializerTest
    extends AbstractMixinSerializerTest
{
    private JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

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

    @Override
    String getSerializedString( final String fileName )
    {
        return jsonTestHelper.loadTestFile( fileName + ".json" );
    }
}
