package com.enonic.xp.server.internal.config;

import java.io.File;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import static org.junit.Assert.*;

public class ConfigPathsTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCurrent()
    {
        final ConfigPaths paths = ConfigPaths.get();
        assertNotNull( paths );
    }

    @Test
    public void testEmpty()
    {
        final ConfigPaths paths = create( "" );
        assertEquals( 0, Lists.newArrayList( paths ).size() );
        assertNull( paths.resolve( "test.txt" ) );
    }

    @Test
    public void testSingle()
        throws Exception
    {
        final ConfigPaths paths = create( this.temporaryFolder.getRoot().getAbsolutePath() );
        assertEquals( 1, Lists.newArrayList( paths ).size() );

        final File file = this.temporaryFolder.newFile( "test.txt" );
        assertEquals( file, paths.resolve( "test.txt" ) );
        assertNull( paths.resolve( "unknown.txt" ) );
    }

    @Test
    public void testMultiple()
        throws Exception
    {
        final File dir1 = this.temporaryFolder.newFolder( "dir1" );
        final File dir2 = this.temporaryFolder.newFolder( "dir2" );

        final ConfigPaths paths = create( dir1.getAbsolutePath() + "," + dir2.getAbsolutePath() );
        assertEquals( 2, Lists.newArrayList( paths ).size() );

        final File file = new File( dir2, "test.txt" );
        Files.touch( file );

        assertEquals( file, paths.resolve( "test.txt" ) );
        assertNull( paths.resolve( "unknown.txt" ) );
    }

    private ConfigPaths create( final String value )
    {
        final Properties props = new Properties();
        props.put( "xp.config.paths", value );

        return new ConfigPaths( props );
    }
}
