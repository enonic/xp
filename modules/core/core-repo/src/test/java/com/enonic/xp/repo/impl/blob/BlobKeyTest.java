package com.enonic.xp.repo.impl.blob;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlobKeyTest
{
    @Test
    public void testNewFromString()
    {
        final BlobKey key = new BlobKey( "0001" );
        assertEquals( "0001", key.toString() );
    }

    @Test
    public void testNewFromBytes()
    {
        final BlobKey key = new BlobKey( new byte[]{(byte) 0, (byte) 1} );
        assertEquals( "0001", key.toString() );
    }

    @Test
    public void testEquals()
    {
        final BlobKey key1 = new BlobKey( "0001" );
        final BlobKey key2 = new BlobKey( "0001" );

        assertTrue( key1.equals( key2 ) );
        assertEquals( key1.hashCode(), key2.hashCode() );
    }
}
