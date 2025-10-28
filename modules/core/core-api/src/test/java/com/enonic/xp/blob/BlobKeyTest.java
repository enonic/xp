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

        final BlobKey key = BlobKey.from( source );
        assertNotNull( key );
        assertEquals( "7037807198c22a7d2b0807371d763779a84fdfcf", key.toString() );
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
