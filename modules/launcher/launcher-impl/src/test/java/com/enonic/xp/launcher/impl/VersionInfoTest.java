package com.enonic.xp.launcher.impl;

import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VersionInfoTest
{
    @Test
    void testGetInfo()
    {
        final VersionInfo info = VersionInfo.get();
        assertNotNull( info );
        assertNotNull( info.getAsMap() );
        assertNotNull( info.getBuildBranch() );
        assertNotNull( info.getBuildHash() );
        assertNotNull( info.getBuildTimestamp() );
    }

    @Test
    void testInfoProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "xp.build.branch", "master" );
        props.setProperty( "xp.build.hash", "12345678" );
        props.setProperty( "xp.build.timestamp", "2015-12-11T20:11:10" );

        final VersionInfo info = new VersionInfo( props );
        assertEquals( "master", info.getBuildBranch() );
        assertEquals( "12345678", info.getBuildHash() );
        assertEquals( "2015-12-11T20:11:10", info.getBuildTimestamp() );

        final Map<String, String> map = info.getAsMap();
        assertEquals( 3, map.size() );
        assertEquals( "master", map.get( "xp.build.branch" ) );
        assertEquals( "12345678", map.get( "xp.build.hash" ) );
        assertEquals( "2015-12-11T20:11:10", map.get( "xp.build.timestamp" ) );
    }
}
