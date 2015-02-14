package com.enonic.xp.core.impl.schema;

import java.util.List;

import com.enonic.xp.schema.mixin.MixinProvider;

import static org.junit.Assert.*;

public class SchemaActivatorMixinTest
    extends AbstractSchemaActivatorTest
{
    @Override
    protected void validateProviders()
        throws Exception
    {
        final List<MixinProvider> list1 = getServices( null, MixinProvider.class );
        assertEquals( 1, list1.size() );

        final List<MixinProvider> list2 = getServices( "module1", MixinProvider.class );
        assertEquals( 1, list2.size() );

        final List<MixinProvider> list3 = getServices( "module2", MixinProvider.class );
        assertEquals( 0, list3.size() );

        final List<MixinProvider> list4 = getServices( "not-module", MixinProvider.class );
        assertEquals( 0, list4.size() );
    }

    @Override
    protected void validateNoProviders()
        throws Exception
    {
        final List<MixinProvider> list = getServices( null, MixinProvider.class );
        assertEquals( 0, list.size() );
    }
}
