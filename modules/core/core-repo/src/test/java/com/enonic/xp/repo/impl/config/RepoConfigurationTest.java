package com.enonic.xp.repo.impl.config;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Maps;

public class RepoConfigurationTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Map<String, String> map;

    @BeforeEach
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
        assertEquals( new File( "/a/b" ), config.getSnapshotsDir() );
    }

}
