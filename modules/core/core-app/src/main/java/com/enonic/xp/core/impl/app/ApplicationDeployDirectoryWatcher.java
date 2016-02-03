package com.enonic.xp.core.impl.app;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.home.HomeDir;

@Component(immediate = true)
public class ApplicationDeployDirectoryWatcher
    implements FileAlterationListener
{
    private static final long DEPLOY_DIRECTORY_CHECK_INTERVAL = 1000l;

    private static final IOFileFilter FILTER =
        FileFilterUtils.and( FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter( ".jar" ) );

    private Map<String, ApplicationKey> applicationKeyByFile = new ConcurrentHashMap<>();

    private ApplicationService applicationService;


    @Activate
    public void activate()
        throws Exception
    {
        File deployFolder = new File( HomeDir.get().toFile(), "deploy" );
        FileAlterationObserver observer = new FileAlterationObserver( deployFolder, FILTER );
        observer.addListener( this );

        if ( deployFolder.exists() )
        {
            for ( File deployedApplicationFile : deployFolder.listFiles( (FilenameFilter) FILTER ) )
            {
                installApplication( deployedApplicationFile );
            }
        }

        new FileAlterationMonitor( DEPLOY_DIRECTORY_CHECK_INTERVAL, observer ).start();
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
        final ByteSource byteSource = Files.asByteSource( file );

        final Application application = ApplicationHelper.runAsAdmin( () -> applicationService.installApplication( byteSource ) );
        applicationKeyByFile.put( file.getName(), application.getKey() );
    }

    private void uninstallApplication( final File file )
    {
        final ApplicationKey applicationKey = applicationKeyByFile.remove( file.getName() );
        if ( applicationKey != null )
        {
            ApplicationHelper.runAsAdmin( () -> this.applicationService.uninstallApplication( applicationKey ) );
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
