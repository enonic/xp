package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.schema.xdata.MixinDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltinXDataTypesTest
{
    @Test
    void testGetAll()
    {
        final MixinDescriptors xdatas = new BuiltinMixinTypes().getAll();
        assertEquals( 3, xdatas.getSize() );

        assertSchema( xdatas.get( 0 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":imageInfo" ), false );
        assertSchema( xdatas.get( 1 ), MixinName.from( ApplicationKey.MEDIA_MOD + ":cameraInfo" ), false );
        assertSchema( xdatas.get( 2 ), MixinName.from( ApplicationKey.BASE + ":gpsInfo" ), false );
    }

    private void assertSchema( final MixinDescriptor schema, final MixinName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
