package com.enonic.xp.server.internal.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        final Path dir1 = Files.createDirectory( this.temporaryFolder.resolve( "dir1" ) );
        final Path dir2 = Files.createDirectory( this.temporaryFolder.resolve( "dir2" ) );

        final ConfigPaths paths = create( dir1.toAbsolutePath() + "," + dir2.toAbsolutePath() );
        assertEquals( 2, Lists.newArrayList( paths ).size() );

        final Path file = dir2.resolve( "test.txt" );
        Files.createFile( file );

        assertEquals( file.toFile(), paths.resolve( "test.txt" ) );
        assertNull( paths.resolve( "unknown.txt" ) );
    }

    private ConfigPaths create( final String value )
    {
        final Properties props = new Properties();
        props.put( "xp.config.paths", value );

        return new ConfigPaths( props );
    }
}
