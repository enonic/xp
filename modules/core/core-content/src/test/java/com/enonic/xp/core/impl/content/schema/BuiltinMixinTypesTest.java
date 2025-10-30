package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltinMixinTypesTest
{
    @Test
    void testGetAll()
    {
        final MixinDescriptors descriptors = new BuiltinMixinTypes().getAll();
        assertEquals( 3, descriptors.getSize() );

        assertSchema( descriptors.get( 0 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":imageInfo" ), false );
        assertSchema( descriptors.get( 1 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":cameraInfo" ), false );
        assertSchema( descriptors.get( 2 ), MixinName.from( ApplicationKey.BASE + ":gpsInfo" ), false );
    }

    private void assertSchema( final MixinDescriptor schema, final MixinName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
