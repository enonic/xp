package com.enonic.xp.core.impl.schema.mixin;

import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
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

        assertSchema( mixins.get( 0 ), MixinName.from( ModuleKey.MEDIA_MOD + ":image-info" ), false );
        assertSchema( mixins.get( 1 ), MixinName.from( ModuleKey.MEDIA_MOD + ":photo-info" ), false );
        assertSchema( mixins.get( 2 ), MixinName.from( ModuleKey.BASE + ":gps-info" ), false );
    }

    @Test
    public void testBuiltinLoadByModule()
    {
        Mixins mixins = new BuiltinMixinsLoader().loadByModule( ModuleKey.MEDIA_MOD );
        assertEquals( 2, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( ModuleKey.MEDIA_MOD + ":image-info" ), false );
        assertSchema( mixins.get( 1 ), MixinName.from( ModuleKey.MEDIA_MOD + ":photo-info" ), false );

        mixins = new BuiltinMixinsLoader().loadByModule( ModuleKey.BASE );
        assertEquals( 1, mixins.getSize() );

        assertSchema( mixins.get( 0 ), MixinName.from( ModuleKey.BASE + ":gps-info" ), false );
    }

    private void assertSchema( final Mixin schema, final MixinName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
