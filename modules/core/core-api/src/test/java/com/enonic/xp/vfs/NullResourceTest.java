package com.enonic.xp.vfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NullResourceTest
{
    @Test
    void does_not_exist()
    {
        final NullResource fisk = new NullResource( "/fisk" );
        assertFalse( fisk.exists() );
    }

    @Test
    void path()
    {
        final NullResource fisk = new NullResource( "/fisk" );
        assertEquals( "/fisk", fisk.getPath().getPath() );
    }

    @Test
    void name()
    {
        final NullResource fisk = new NullResource( "/fisk/ost" );
        assertEquals( "ost", fisk.getName() );
    }

}
