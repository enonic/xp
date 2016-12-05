package com.enonic.xp.server.internal.config;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.Files;

public class ConfigWatcherTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ConfigInstaller installer;

    private ConfigWatcher watcher;

    private File dir1;

    private File dir2;

    @Before
    public void setup()
        throws Exception
    {
        this.installer = Mockito.mock( ConfigInstaller.class );
        this.watcher = new ConfigWatcher();
        this.watcher.setInstaller( this.installer );

        this.dir1 = this.temporaryFolder.newFolder( "dir1" );
        this.dir2 = this.temporaryFolder.newFolder( "dir2" );

        this.watcher.configPaths = new ConfigPaths( this.dir1.getAbsolutePath() + "," + this.dir2.getAbsolutePath() );
    }

    private File touchFile( final File dir, final String name )
        throws Exception
    {
        final File file = new File( dir, name );
        Files.touch( file );
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

        Files.touch( file1 );
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
