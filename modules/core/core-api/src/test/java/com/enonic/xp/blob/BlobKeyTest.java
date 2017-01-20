package com.enonic.xp.blob;

import org.junit.Test;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteSource;

import static org.junit.Assert.*;

public class BlobKeyTest
{
    @Test
    public void fromString()
    {
        final BlobKey key = BlobKey.from( "123" );
        assertNotNull( key );
        assertEquals( "123", key.toString() );
    }

    @Test
    public void fromHashCode()
    {
        final HashCode code = HashCode.fromBytes( new byte[]{(byte) 0x01, (byte) 0xA1, (byte) 0xB1} );

        final BlobKey key = BlobKey.from( code );
        assertNotNull( key );
        assertEquals( "01a1b1", key.toString() );
    }

    @Test
    public void fromByteSource()
    {
        final ByteSource source = ByteSource.wrap( new byte[]{(byte) 1, (byte) 2, (byte) 3} );

        final BlobKey key = BlobKey.from( source );
        assertNotNull( key );
        assertEquals( "7037807198c22a7d2b0807371d763779a84fdfcf", key.toString() );
    }

    @Test
    public void testEquals()
    {
        final BlobKey key1 = BlobKey.from( "0001" );
        final BlobKey key2 = BlobKey.from( "0001" );

        assertTrue( key1.equals( key2 ) );
        assertEquals( key1.hashCode(), key2.hashCode() );
    }
}
