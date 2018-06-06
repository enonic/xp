package com.enonic.xp.server.internal.deploy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.server.ServerInfo;

@Component(configurationPid = "com.enonic.xp.server.deploy", service = {DeployDirectoryWatcher.class, FileAlterationListener.class})
public final class DeployDirectoryWatcher
    implements FileAlterationListener
{
    private final static Logger LOGGER = LoggerFactory.getLogger( DeployDirectoryWatcher.class );

    private static final IOFileFilter FILTER =
        FileFilterUtils.and( FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter( ".jar" ) );

    private Map<String, ApplicationKey> applicationKeyByPath = new ConcurrentHashMap<>();

    private Map<ApplicationKey, Stack<String>> pathsByApplicationKey = new ConcurrentHashMap<>();

    private ApplicationService applicationService;

    private FileAlterationMonitor monitor;

    private long interval;

    @Activate
    public void activate( final DeployConfig config )
        throws Exception
    {
        interval = config.interval();
    }

    public void deploy()
        throws Exception
    {
        final FileAlterationObserver observer1 = addListenerDir( getDeployFolder() );
        this.monitor = new FileAlterationMonitor( interval, observer1 );
        this.monitor.start();
    }

    private FileAlterationObserver addListenerDir( final File dir )
        throws Exception
    {
        final FileAlterationObserver observer = new FileAlterationObserver( dir, FILTER );
        observer.addListener( this );

        installApps( dir );
        return observer;
    }

    private void installApps( final File dir )
        throws Exception
    {
        if ( !dir.exists() )
        {
            return;
        }

        final File[] files = dir.listFiles( (FilenameFilter) FILTER );
        if ( files == null )
        {
            return;
        }

        for ( final File file : files )
        {
            installApplication( file );
        }
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        if ( monitor != null )
        {
            monitor.stop();
        }
    }

    @Override
    public void onStart( final FileAlterationObserver fileAlterationObserver )
    {
    }

    @Override
    public void onDirectoryCreate( final File file )
    {
    }

    @Override
    public void onDirectoryChange( final File file )
    {
    }

    @Override
    public void onDirectoryDelete( final File file )
    {
    }

    @Override
    public void onFileCreate( final File file )
    {
        try
        {
            installApplication( file );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to install local application", e );
        }
    }

    @Override
    public void onFileChange( final File file )
    {
        try
        {
            installApplication( file );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to install local application", e );
        }
    }

    @Override
    public void onFileDelete( final File file )
    {
        try
        {
            uninstallApplication( file );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to uninstall local application", e );
        }
    }

    @Override
    public void onStop( final FileAlterationObserver fileAlterationObserver )
    {
    }

    private void installApplication( final File file )
    {
        //Installs the application
        final ByteSource byteSource = Files.asByteSource( file );
        final Application application =
            DeployHelper.runAsAdmin( () -> applicationService.installLocalApplication( byteSource, file.getName() ) );
        final ApplicationKey applicationKey = application.getKey();
        final String path = file.getPath();

        //Stores a mapping fileName -> applicationKey. Needed for uninstallation
        this.applicationKeyByPath.put( path, applicationKey );

        //Updates the mapping applicationKey -> stack<fileName>. Needed in some particular case for uninstallatioon
        this.pathsByApplicationKey.compute( applicationKey, ( applicationKeyParam, fileNameStack ) -> {
            if ( fileNameStack == null )
            {
                fileNameStack = new Stack<>();
            }
            fileNameStack.remove( path );
            fileNameStack.push( path );

            return fileNameStack;
        } );
    }

    private void uninstallApplication( final File file )
    {
        // Removes the mapping fileName -> applicationKey
        final String path = file.getPath();
        final ApplicationKey applicationKey = applicationKeyByPath.remove( path );

        this.pathsByApplicationKey.computeIfPresent( applicationKey, ( applicationKeyParam, fileNameStack ) -> {

            if ( fileNameStack == null )
            {
                return null;
            }

            //Retrieve the file name for the currently installed application
            final String lastInstalledFile = fileNameStack.isEmpty() ? null : fileNameStack.peek();

            //If the file removed is currently installed
            if ( path.equals( lastInstalledFile ) )
            {
                //Uninstall the corresponding application
                DeployHelper.runAsAdmin( () -> this.applicationService.uninstallApplication( applicationKey, false ) );
                fileNameStack.pop();

                // If there is a previous file with the same applicationKey
                if ( !fileNameStack.isEmpty() )
                {
                    //Installs this previous application
                    final String previousInstalledFile = fileNameStack.peek();
                    final ByteSource byteSource = Files.asByteSource( new File( previousInstalledFile ) );
                    DeployHelper.runAsAdmin( () -> applicationService.installLocalApplication( byteSource, previousInstalledFile ) );
                }
            }
            else
            {
                fileNameStack.remove( path );
            }

            return fileNameStack.isEmpty() ? null : fileNameStack;
        } );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    private static File getDeployFolder()
    {
        final File homeDir = ServerInfo.get().getHomeDir();
        return new File( homeDir, "deploy" );
    }
}
