package com.enonic.xp.server.internal.deploy;

import java.io.File;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
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
import com.enonic.xp.home.HomeDir;

@Component(configurationPid = "com.enonic.xp.server.deploy", service = {DeployDirectoryWatcher.class})
public final class DeployDirectoryWatcher
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DeployDirectoryWatcher.class );

    private final Map<String, ApplicationKey> applicationKeyByPath = new ConcurrentHashMap<>();

    private final Map<ApplicationKey, Stack<String>> pathsByApplicationKey = new ConcurrentHashMap<>();

    private final ApplicationService applicationService;

    private final long interval;

    private volatile FileAlterationMonitor monitor;

    @Activate
    public DeployDirectoryWatcher( @Reference final ApplicationService applicationService, final DeployConfig config )
    {
        this.applicationService = applicationService;
        this.interval = config.interval();
    }

    public void deploy()
        throws Exception
    {
        final FileAlterationObserver observer = FileAlterationObserver.builder()
            .setFile( HomeDir.get().toPath().resolve( "deploy" ).toFile() )
            .setFileFilter( DeployDirectoryWatcher::isJarFile )
            .get();
        observer.addListener( new Listener() );
        bootstrap( observer );
        this.monitor = new FileAlterationMonitor( interval, observer );
        this.monitor.start();
    }

    private static boolean isJarFile( final File file )
    {
        return file.getName().endsWith( ".jar" ) && file.isFile();
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

    private void bootstrap( FileAlterationObserver observer )
    {
        final File dir = observer.getDirectory();
        if ( !dir.exists() )
        {
            return;
        }

        final File[] files = dir.listFiles( DeployDirectoryWatcher::isJarFile );
        if ( files == null )
        {
            return;
        }

        for ( final File file : files )
        {
            try
            {
                installApplication( file );
            }
            catch ( Exception e )
            {
                LOGGER.error( "Failed to install local application [{}]", file.getName(), e );
            }
        }
    }

    private class Listener
        extends FileAlterationListenerAdaptor
    {
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
    }

    private void installApplication( final File file )
    {
        //Installs the application
        final ByteSource byteSource = Files.asByteSource( file );
        final Application application = DeployHelper.runAsAdmin( () -> applicationService.installLocalApplication( byteSource ) );
        final ApplicationKey applicationKey = application.getKey();
        final String path = file.getPath();

        //Stores a mapping fileName -> applicationKey. Needed for uninstallation
        this.applicationKeyByPath.put( path, applicationKey );

        //Updates the mapping applicationKey -> stack<fileName>. Needed in some particular case for uninstallation
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

            //Retrieve the file name for the currently installed application
            final String lastInstalledFile = fileNameStack.isEmpty() ? null : fileNameStack.peek();

            //If the file removed is currently installed
            if ( path.equals( lastInstalledFile ) )
            {
                //Uninstall the corresponding application
                DeployHelper.runAsAdmin( () -> this.applicationService.uninstallApplication( applicationKey ) );
                fileNameStack.pop();

                // If there is a previous file with the same applicationKey
                if ( !fileNameStack.isEmpty() )
                {
                    //Installs this previous application
                    final String previousInstalledFile = fileNameStack.peek();
                    final ByteSource byteSource = Files.asByteSource( new File( previousInstalledFile ) );
                    DeployHelper.runAsAdmin( () -> {
                        try
                        {
                            applicationService.installLocalApplication( byteSource );
                        }
                        catch ( Exception e )
                        {
                            LOGGER.warn( "Failed to reinstall local application [{}]", previousInstalledFile, e );
                        }
                    } );
                }
            }
            else
            {
                fileNameStack.remove( path );
            }

            return fileNameStack.isEmpty() ? null : fileNameStack;
        } );
    }
}
