package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class BuiltinMixinsLoaderTest
{
    @Test
    public void testBuiltin()
    {
        final Mixins mixins = new BuiltinMixinsLoader().load();
        assertEquals( 3, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":imageInfo" ), false );
        assertSchema( mixins.get( 1 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":cameraInfo" ), false );
        assertSchema( mixins.get( 2 ), MixinName.from( ApplicationKey.BASE + ":gpsInfo" ), false );
    }

    @Test
    public void testBuiltinLoadByApplication()
    {
        Mixins mixins = new BuiltinMixinsLoader().loadByApplication( ApplicationKey.MEDIA_MOD );
        assertEquals( 2, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":imageInfo" ), false );
        assertSchema( mixins.get( 1 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":cameraInfo" ), false );

        mixins = new BuiltinMixinsLoader().loadByApplication( ApplicationKey.BASE );
        assertEquals( 1, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( ApplicationKey.BASE + ":gpsInfo" ), false );
    }

    private void assertSchema( final Mixin schema, final MixinName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
