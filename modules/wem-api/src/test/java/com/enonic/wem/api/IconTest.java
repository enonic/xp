package com.enonic.wem.api;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;

import static org.junit.Assert.*;

public class IconTest
{

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_empty_fails()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{}, "image/gif" );
    }

    @Test(expected = NullPointerException.class)
    public void testCreate_null_data_fails()
        throws Exception
    {
        final Icon icon1 = Icon.from( (byte[]) null, "image/gif" );
    }

    @Test(expected = NullPointerException.class)
    public void testCreate_no_mimetype_fails()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{1, 2, 3, 4, 5}, null );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final Icon icon2 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final Icon icon3 = Icon.from( new byte[]{1, 2, 3, 4, 6}, "image/gif" );
        final Icon icon4 = Icon.from( new byte[]{1, 2, 3, 4, 6}, "image/png" );

        assertEquals( icon1, icon1 );
        assertEquals( icon1, icon2 );
        assertFalse( icon1.equals( icon3 ) );
        assertFalse( icon3.equals( icon4 ) );
        assertFalse( icon1.equals( icon1.toString() ) );

        assertEquals( icon1.getMimeType(), icon2.getMimeType() );
        assertEquals( icon1.getSize(), icon2.getSize() );
        assertArrayEquals( icon1.toByteArray(), icon2.toByteArray() );
    }

    @Test
    public void testHashCode()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final Icon icon2 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final Icon icon3 = Icon.from( new byte[]{1, 2, 3, 4, 6}, "image/gif" );
        final Icon icon4 = Icon.from( new byte[]{1, 2, 3, 4, 6}, "image/png" );

        assertTrue( icon1.hashCode() == icon2.hashCode() );
        assertFalse( icon1.hashCode() == icon3.hashCode() );
        assertFalse( icon3.hashCode() == icon4.hashCode() );
    }

    @Test
    public void testFromInputStream()
        throws Exception
    {
        final byte[] data = new byte[]{1, 2, 3, 4, 5};
        final InputStream is = new ByteArrayInputStream( data );
        final Icon icon1 = Icon.from( is, "image/gif" );

        assertArrayEquals( icon1.toByteArray(), data );
    }

    @Test
    public void testAsInputStream()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final InputStream is = icon1.asInputStream();

        assertArrayEquals( ByteStreams.toByteArray( is ), new byte[]{1, 2, 3, 4, 5} );
    }
}
