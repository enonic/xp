package com.enonic.wem.api;

import org.junit.Test;

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
        final Icon icon1 = Icon.from( null, "image/gif" );
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
        assertArrayEquals( icon1.getData(), icon2.getData() );
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
    public void testCopyOf()
        throws Exception
    {
        final Icon icon1 = Icon.from( new byte[]{1, 2, 3, 4, 5}, "image/gif" );
        final Icon iconCopy = Icon.copyOf( icon1 );
        assertNotSame( icon1, iconCopy );
        assertEquals( icon1, iconCopy );
        assertFalse( icon1.toString().equals( iconCopy.toString() ) );
    }
}
