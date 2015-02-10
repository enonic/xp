package com.enonic.wem.core.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class BuiltinMixinProviderTest
{
    @Test
    public void testBuiltin()
    {
        final Mixins mixins = new BuiltinMixinProvider().get();
        assertEquals( 1, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( "media:image-info" ), false );
    }

    private void assertSchema( final Mixin schema, final MixinName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
