package com.enonic.xp.server.internal.config;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigPathsTest
{
    @TempDir
    public Path temporaryFolder;

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
        final ConfigPaths paths = create( this.temporaryFolder.toFile().getAbsolutePath() );
        assertEquals( 1, Lists.newArrayList( paths ).size() );

        final File file = Files.createFile(this.temporaryFolder.resolve( "test.txt" ) ).toFile();
        assertEquals( file, paths.resolve( "test.txt" ) );
        assertNull( paths.resolve( "unknown.txt" ) );
    }

    @Test
    public void testMultiple()
        throws Exception
    {
        final File dir1 = Files.createDirectory(this.temporaryFolder.resolve( "dir1" ) ).toFile();
        final File dir2 = Files.createDirectory(this.temporaryFolder.resolve( "dir2" ) ).toFile();

        final ConfigPaths paths = create( dir1.getAbsolutePath() + "," + dir2.getAbsolutePath() );
        assertEquals( 2, Lists.newArrayList( paths ).size() );

        final File file = new File( dir2, "test.txt" );
        com.google.common.io.Files.touch( file );

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
