package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteProcessor;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.app.mapper.IconByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class IconByteSourceTest
{
    @Test
    void testIcon()
        throws Exception
    {
        final byte[] iconSource = new byte[]{0, 1};
        final Icon icon = Icon.from( iconSource, "image/png", Instant.now() );

        final IconByteSource iconByteSource = new IconByteSource( icon );

        assertEquals( "IconStream(2)", iconByteSource.toString() );
        assertEquals( 2, iconByteSource.size() );
        assertFalse( iconByteSource.isEmpty() );
        assertEquals( 2, iconByteSource.openStream().available() );
        assertEquals( 2, iconByteSource.openBufferedStream().available() );
        assertEquals( 0, iconByteSource.read()[0] );
        assertEquals( 1, iconByteSource.read()[1] );

        final ByteProcessor<String> processor = new ByteProcessor()
        {
            @Override
            public boolean processBytes( final byte[] buf, final int off, final int len )
            {
                assertEquals( 0, buf[0] );
                assertEquals( 1, buf[1] );

                return true;
            }

            @Override
            public String getResult()
            {
                return null;
            }
        };
        iconByteSource.read( processor );
    }
}
