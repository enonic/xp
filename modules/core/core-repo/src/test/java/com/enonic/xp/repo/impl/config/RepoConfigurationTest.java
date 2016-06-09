package com.enonic.xp.repo.impl.config;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Maps;

public class RepoConfigurationTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Map<String, String> map;

    @Before
    public void setup()
    {
        this.map = Maps.newHashMap();
        System.setProperty( "xp.home", this.temporaryFolder.getRoot().getAbsolutePath() );
    }

    private RepoConfiguration createConfig()
    {
        final RepoConfigurationImpl config = new RepoConfigurationImpl();
        config.activate( this.map );
        return config;
    }

    @Test
    public void testSnapshotsDir()
    {
        this.map.put( "snapshots.dir", "/a/b" );

        final RepoConfiguration config = createConfig();
        Assert.assertEquals( new File( "/a/b" ), config.getSnapshotsDir() );
    }

}
