package com.enonic.xp.vfs;

import org.junit.Test;

import static org.junit.Assert.*;

public class NullResourceTest
{
    @Test
    public void does_not_exist()
        throws Exception
    {
        final NullResource fisk = new NullResource( "/fisk" );
        assertFalse( fisk.exists() );
    }

    @Test
    public void path()
        throws Exception
    {
        final NullResource fisk = new NullResource( "/fisk" );
        assertEquals( "/fisk", fisk.getPath().getPath() );
    }

    @Test
    public void name()
        throws Exception
    {
        final NullResource fisk = new NullResource( "/fisk/ost" );
        assertEquals( "ost", fisk.getName() );
    }

}