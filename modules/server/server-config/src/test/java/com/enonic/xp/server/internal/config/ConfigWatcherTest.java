package com.enonic.xp.server.internal.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

public class ConfigWatcherTest
{
    @TempDir
    public Path temporaryFolder;

    private ConfigInstaller installer;

    private ConfigWatcher watcher;

    private File dir1;

    private File dir2;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.installer = Mockito.mock( ConfigInstaller.class );
        this.watcher = new ConfigWatcher();
        this.watcher.setInstaller( this.installer );

        this.dir1 = Files.createDirectory(this.temporaryFolder.resolve( "dir1" ) ).toFile();
        this.dir2 = Files.createDirectory(this.temporaryFolder.resolve( "dir2" ) ).toFile();

        this.watcher.configPaths = new ConfigPaths( this.dir1.getAbsolutePath() + "," + this.dir2.getAbsolutePath() );
    }

    private File touchFile( final File dir, final String name )
        throws Exception
    {
        final File file = new File( dir, name );
        com.google.common.io.Files.touch( file );
        return file;
    }

    @Test
    public void initFiles()
        throws Exception
    {
        final File file1 = touchFile( this.dir1, "com.foo.bar.cfg" );
        final File file2 = touchFile( this.dir2, "com.foo.bar.cfg" );

        this.watcher.activate();
        Mockito.verify( this.installer, Mockito.times( 2 ) ).updateConfig( file1 );
        Mockito.verify( this.installer, Mockito.times( 0 ) ).updateConfig( file2 );
        this.watcher.deactivate();
    }

    @Test
    public void watchFiles()
        throws Exception
    {
        this.watcher.activate();

        final File file1 = touchFile( this.dir1, "com.foo.bar.cfg" );
        Thread.sleep( 600 );
        Mockito.verify( this.installer, Mockito.times( 1 ) ).updateConfig( file1 );

        final File file2 = touchFile( this.dir2, "com.foo.bar.cfg" );
        Thread.sleep( 600 );
        Mockito.verify( this.installer, Mockito.times( 2 ) ).updateConfig( file1 );

        com.google.common.io.Files.touch( file1 );
        Thread.sleep( 600 );
        Mockito.verify( this.installer, Mockito.times( 3 ) ).updateConfig( file1 );

        file1.delete();
        Thread.sleep( 600 );
        Mockito.verify( this.installer, Mockito.times( 1 ) ).updateConfig( file2 );

        file2.delete();
        Thread.sleep( 600 );
        Mockito.verify( this.installer, Mockito.times( 1 ) ).deleteConfig( file2.getName() );

        this.watcher.deactivate();
    }
}
