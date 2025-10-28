package com.enonic.xp;

import org.junit.jupiter.api.Test;

import com.enonic.xp.server.VersionInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionInfoTest
{
    @Test
    void testDefault()
    {
        VersionInfo.setDefault();
        final VersionInfo info = VersionInfo.get();
        assertEquals( "0.0.0-SNAPSHOT", info.getVersion() );
        assertEquals( "0.0.0-SNAPSHOT", info.toString() );
        assertTrue( info.isSnapshot() );
    }

    @Test
    void testSnapshot()
    {
        VersionInfo.set( "1.1.1-SNAPSHOT" );
        final VersionInfo info = VersionInfo.get();
        assertEquals( "1.1.1-SNAPSHOT", info.getVersion() );
        assertEquals( "1.1.1-SNAPSHOT", info.toString() );
        assertTrue( info.isSnapshot() );
    }

    @Test
    void testRelease()
    {
        VersionInfo.set( "1.1.1" );
        final VersionInfo info = VersionInfo.get();
        assertEquals( "1.1.1", info.getVersion() );
        assertEquals( "1.1.1", info.toString() );
        assertFalse( info.isSnapshot() );
    }
}
