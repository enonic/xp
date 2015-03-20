package com.enonic.xp;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.server.VersionInfo;

public class VersionInfoTest
{
    @Test
    public void testEmpty()
    {
        System.setProperty( "xp.version", "" );
        System.setProperty( "xp.build.hash", "" );
        System.setProperty( "xp.build.number", "" );

        final VersionInfo info = VersionInfo.get();
        Assert.assertEquals( "0.0.0-SNAPSHOT", info.getVersion() );
        Assert.assertEquals( "N/A", info.getBuildHash() );
        Assert.assertEquals( "N/A", info.getBuildNumber() );
        Assert.assertTrue( info.isSnapshotVersion() );
    }

    @Test
    public void testPropertiesSet()
    {
        System.setProperty( "xp.version", "5.0.0" );
        System.setProperty( "xp.build.hash", "123" );
        System.setProperty( "xp.build.number", "789" );

        final VersionInfo info = VersionInfo.get();
        Assert.assertEquals( "5.0.0", info.getVersion() );
        Assert.assertEquals( "123", info.getBuildHash() );
        Assert.assertEquals( "789", info.getBuildNumber() );
        Assert.assertFalse( info.isSnapshotVersion() );
    }
}
