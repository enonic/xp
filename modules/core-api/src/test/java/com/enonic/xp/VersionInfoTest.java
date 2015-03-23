package com.enonic.xp;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.server.VersionInfo;

public class VersionInfoTest
{
    @Test
    public void testDefault()
    {
        VersionInfo.setDefault();
        final VersionInfo info = VersionInfo.get();
        Assert.assertEquals( "0.0.0-SNAPSHOT", info.getVersion() );
        Assert.assertEquals( "0.0.0-SNAPSHOT", info.toString() );
        Assert.assertTrue( info.isSnapshot() );
    }

    @Test
    public void testSnapshot()
    {
        VersionInfo.set( "1.1.1-SNAPSHOT" );
        final VersionInfo info = VersionInfo.get();
        Assert.assertEquals( "1.1.1-SNAPSHOT", info.getVersion() );
        Assert.assertEquals( "1.1.1-SNAPSHOT", info.toString() );
        Assert.assertTrue( info.isSnapshot() );
    }

    @Test
    public void testRelease()
    {
        VersionInfo.set( "1.1.1" );
        final VersionInfo info = VersionInfo.get();
        Assert.assertEquals( "1.1.1", info.getVersion() );
        Assert.assertEquals( "1.1.1", info.toString() );
        Assert.assertFalse( info.isSnapshot() );
    }
}
