package com.enonic.xp.server;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildInfoTest
{
    @Test
    void testInfo()
    {
        final Properties props = new Properties();
        props.setProperty( "xp.build.branch", "master" );
        props.setProperty( "xp.build.hash", "123456" );
        props.setProperty( "xp.build.shortHash", "123" );
        props.setProperty( "xp.build.timestamp", "2015-11-11T22:11:00" );

        final BuildInfo info = new BuildInfo( props );
        assertEquals( "master", info.getBranch() );
        assertEquals( "123456", info.getHash() );
        assertEquals( "123", info.getShortHash() );
        assertEquals( "2015-11-11T22:11:00", info.getTimestamp() );
    }
}
