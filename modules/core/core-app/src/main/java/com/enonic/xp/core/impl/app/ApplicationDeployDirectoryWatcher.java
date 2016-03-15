package com.enonic.xp.core.impl.app;

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

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(immediate = true, configurationPid = "com.enonic.xp.app.deploy")
public class ApplicationDeployDirectoryWatcher
    implements FileAlterationListener
{
    private static final String DEPLOY_PATH_PROPERTY_KEY = "deploy.path";

    private static final String DEPLOY_INTERVAL_PROPERTY_KEY = "deploy.interval";

    private static final IOFileFilter FILTER =
        FileFilterUtils.and( FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter( ".jar" ) );

    private Map<String, ApplicationKey> applicationKeyByPath = new ConcurrentHashMap<>();

    private Map<ApplicationKey, Stack<String>> pathsByApplicationKey = new ConcurrentHashMap<>();

    private ApplicationService applicationService;

    private Configuration config;

    private FileAlterationMonitor monitor;


    @Activate
    public void activate( final Map<String, String> map )
        throws Exception
    {
        config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();
        config = new ConfigInterpolator().interpolate( this.config );

        final File deployFolder = new File( config.get( DEPLOY_PATH_PROPERTY_KEY ) );
        final FileAlterationObserver observer = new FileAlterationObserver( deployFolder, FILTER );
        observer.addListener( this );

        if ( deployFolder.exists() )
        {
            for ( File deployedApplicationFile : deployFolder.listFiles( (FilenameFilter) FILTER ) )
            {
                installApplication( deployedApplicationFile );
            }
        }

        final long interval = config.get( DEPLOY_INTERVAL_PROPERTY_KEY, Long.class );
        monitor = new FileAlterationMonitor( interval, observer );
        monitor.start();
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
        installApplication( file );
    }

    @Override
    public void onFileChange( final File file )
    {
        installApplication( file );
    }

    @Override
    public void onFileDelete( final File file )
    {
        uninstallApplication( file );
    }

    @Override
    public void onStop( final FileAlterationObserver fileAlterationObserver )
    {
    }

    private void installApplication( final File file )
    {
        //Installs the application
        final ByteSource byteSource = Files.asByteSource( file );
        final Application application = ApplicationHelper.runAsAdmin( () -> applicationService.installLocalApplication( byteSource ) );
        final ApplicationKey applicationKey = application.getKey();
        final String path = file.getPath();

        //Stores a mapping fileName -> applicationKey. Needed for uninstallation
        applicationKeyByPath.put( path, applicationKey );

        //Updates the mapping applicationKey -> stack<fileName>. Needed in some particular case for uninstallatioon
        pathsByApplicationKey.compute( applicationKey, ( applicationKeyParam, fileNameStack ) -> {
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

        pathsByApplicationKey.computeIfPresent( applicationKey, ( applicationKeyParam, fileNameStack ) -> {

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
                ApplicationHelper.runAsAdmin( () -> this.applicationService.uninstallApplication( applicationKey, false ) );
                fileNameStack.pop();

                // If there is a previous file with the same applicationKey
                if ( !fileNameStack.isEmpty() )
                {
                    //Installs this previous application
                    final String previousInstalledFile = fileNameStack.peek();
                    final ByteSource byteSource = Files.asByteSource( new File( previousInstalledFile ) );
                    ApplicationHelper.runAsAdmin( () -> applicationService.installLocalApplication( byteSource ) );
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
}
