package com.enonic.xp.server.internal.config;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public final class ConfigWatcher
    extends FileAlterationListenerAdaptor
{
    private FileAlterationMonitor monitor;

    private ConfigInstaller installer;

    private final ConfigPaths configPaths;

    public ConfigWatcher()
    {
        this( ConfigPaths.get() );
    }

    public ConfigWatcher( final ConfigPaths configPaths )
    {
        this.configPaths = configPaths;
    }

    @Activate
    public void activate()
        throws Exception
    {
        this.monitor = new FileAlterationMonitor( 500 );
        addObservers();
        this.monitor.start();
        updateConfigs();
    }

    private void addObservers()
    {
        for ( final File path : this.configPaths )
        {
            this.monitor.addObserver( newObserver( path ) );
        }
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        this.monitor.stop();
    }

    @Override
    public void onFileCreate( final File file )
    {
        updateConfig( file );
    }

    @Override
    public void onFileChange( final File file )
    {
        updateConfig( file );
    }

    @Override
    public void onFileDelete( final File file )
    {
        deleteConfig( file );
    }

    private void updateConfig( final File file )
    {
        updateConfig( file.getName() );
    }

    private void updateConfig( final String fileName )
    {
        final File file = this.configPaths.resolve( fileName );
        if ( file == null )
        {
            return;
        }

        this.installer.updateConfig( file );
    }

    private void deleteConfig( final File file )
    {

        deleteConfig( file.getName() );
    }

    private void deleteConfig( final String fileName )
    {
        final File file = this.configPaths.resolve( fileName );
        if ( file != null )
        {
            this.installer.updateConfig( file );
            return;
        }

        this.installer.deleteConfig( fileName );
    }

    private FileAlterationObserver newObserver( final File path )
    {
        final FileAlterationObserver observer = new FileAlterationObserver( path, this::isConfigFile );
        observer.addListener( this );
        return observer;
    }

    private void updateConfigs()
    {
        for ( final File baseDir : this.configPaths )
        {
            updateConfigs( baseDir );
        }
    }

    private void updateConfigs( final File baseDir )
    {
        final File[] list = baseDir.listFiles( this::isConfigFile );
        if ( list == null )
        {
            return;
        }

        for ( final File file : list )
        {
            updateConfig( file );
        }
    }

    private boolean isConfigFile( final File file )
    {
        return file.getName().endsWith( ".cfg" ) && file.isFile();
    }

    @Reference
    public void setInstaller( final ConfigInstaller installer )
    {
        this.installer = installer;
    }
}
