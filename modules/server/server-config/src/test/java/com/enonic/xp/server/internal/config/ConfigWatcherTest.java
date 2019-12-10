package com.enonic.xp.server.internal.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

class ConfigWatcherTest
{
    @TempDir
    Path temporaryFolder;

    private ConfigInstaller installer;

    private ConfigWatcher watcher;

    private File dir1;

    private File dir2;

    @BeforeEach
    void setup()
        throws Exception
    {
        dir1 = Files.createDirectory( temporaryFolder.resolve( "dir1" ) ).toFile();
        dir2 = Files.createDirectory( temporaryFolder.resolve( "dir2" ) ).toFile();

        installer = Mockito.mock( ConfigInstaller.class );
        watcher = new ConfigWatcher( new ConfigPaths( dir1.getAbsolutePath() + "," + dir2.getAbsolutePath() ) );
        watcher.setInstaller( installer );

    }

    private File createFile( final File dir, final String name )
        throws Exception
    {
        return Files.createFile( dir.toPath().resolve( name ) ).toFile();
    }

    @Test
    void initFiles()
        throws Exception
    {
        final File file1 = createFile( dir1, "com.foo.bar.cfg" );
        final File file2 = createFile( dir2, "com.foo.bar.cfg" );

        watcher.activate();
        Mockito.verify( installer, Mockito.times( 2 ) ).updateConfig( file1 );
        Mockito.verify( installer, Mockito.times( 0 ) ).updateConfig( file2 );
        watcher.deactivate();
    }

    @Test
    void watchFiles()
        throws Exception
    {
        watcher.activate();

        final File file1 = createFile( dir1, "com.foo.bar.cfg" );
        Thread.sleep( 600 );
        Mockito.verify( installer, Mockito.times( 1 ) ).updateConfig( file1 );

        final File file2 = createFile( dir2, "com.foo.bar.cfg" );
        Thread.sleep( 600 );
        Mockito.verify( installer, Mockito.times( 2 ) ).updateConfig( file1 );

        Files.setLastModifiedTime( file1.toPath(), FileTime.fromMillis( System.currentTimeMillis() ) );
        Thread.sleep( 600 );
        Mockito.verify( installer, Mockito.times( 3 ) ).updateConfig( file1 );

        Files.delete( file1.toPath() );
        Thread.sleep( 600 );
        Mockito.verify( installer, Mockito.times( 1 ) ).updateConfig( file2 );

        Files.delete( file2.toPath() );
        Thread.sleep( 600 );
        Mockito.verify( installer, Mockito.times( 1 ) ).deleteConfig( file2.getName() );

        watcher.deactivate();
    }
}
