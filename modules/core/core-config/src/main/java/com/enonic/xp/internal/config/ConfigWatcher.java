package com.enonic.xp.internal.config;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.annotations.VisibleForTesting;

@Component(immediate = true)
public final class ConfigWatcher
    extends FileAlterationListenerAdaptor
{
    private final static FileFilter FILTER =
        FileFilterUtils.and( FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter( ".cfg" ) );

    private FileAlterationMonitor monitor;

    private ConfigInstaller installer;

    @VisibleForTesting
    ConfigPaths configPaths = ConfigPaths.get();

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
        final FileAlterationObserver observer = new FileAlterationObserver( path, FILTER );
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
        final File[] list = baseDir.listFiles( FILTER );
        if ( list == null )
        {
            return;
        }

        for ( final File file : list )
        {
            updateConfig( file );
        }
    }

    @Reference
    public void setInstaller( final ConfigInstaller installer )
    {
        this.installer = installer;
    }
}
