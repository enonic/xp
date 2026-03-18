package com.enonic.xp.blob;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlobKeyTest
{
    @Test
    void fromString()
    {
        final BlobKey key = BlobKey.from( "123" );
        assertNotNull( key );
        assertEquals( "123", key.toString() );
    }

    @Test
    void fromByteSource()
    {
        final ByteSource source = ByteSource.wrap( new byte[]{(byte) 1, (byte) 2, (byte) 3} );

        final BlobKey key = BlobKey.sha256( source );
        assertNotNull( key );
        assertEquals( "sha256:039058c6f2c0cb492c533b0a4d14ef77cc0f78abccced5287d84a1a2011cfb81", key.toString() );
    }

    @Test
    void testEquals()
    {
        final BlobKey key1 = BlobKey.from( "0001" );
        final BlobKey key2 = BlobKey.from( "0001" );

        assertTrue( key1.equals( key2 ) );
        assertEquals( key1.hashCode(), key2.hashCode() );
    }
}
