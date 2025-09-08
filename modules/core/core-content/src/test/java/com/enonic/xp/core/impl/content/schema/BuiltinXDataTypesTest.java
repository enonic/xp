package com.enonic.xp.core.impl.content.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDatas;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuiltinXDataTypesTest
{
    @Test
    public void testGetAll()
    {
        final XDatas xdatas = new BuiltinXDataTypes().getAll();
        assertEquals( 3, xdatas.getSize() );

        assertSchema( xdatas.get( 0 ), XDataName.from( ApplicationKey.MEDIA_MOD + ":imageInfo" ), false );
        assertSchema( xdatas.get( 1 ), XDataName.from( ApplicationKey.MEDIA_MOD + ":cameraInfo" ), false );
        assertSchema( xdatas.get( 2 ), XDataName.from( ApplicationKey.BASE + ":gpsInfo" ), false );
    }

    private void assertSchema( final XData schema, final XDataName name, final boolean hasIcon )
    {
        assertEquals( name.toString(), schema.getName().toString() );
        assertEquals( hasIcon, schema.getIcon() != null );
    }
}
