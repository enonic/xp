package com.enonic.xp.vfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
