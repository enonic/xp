package com.enonic.xp.core.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;

import com.enonic.xp.ApplicationInstallException;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService, ApplicationInvalidator
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    private final ConcurrentMap<ApplicationKey, Boolean> localApplicationSet = Maps.newConcurrentMap();

    private ApplicationRegistry registry;

    private BundleContext context;

    private ApplicationRepoService repoService;

    private EventPublisher eventPublisher;

    @Activate
    public void activate( final BundleContext context )
    {
        this.registry = new ApplicationRegistry( context );
        this.context = context;
        ApplicationHelper.runAsAdmin( this::installAllStoredApplications );
    }

    @Override
    public Application getInstalledApplication( final ApplicationKey key )
        throws ApplicationNotFoundException
    {
        return this.registry.get( key );
    }

    @Override
    public ApplicationKeys getInstalledApplicationKeys()
    {
        return this.registry.getKeys();
    }

    @Override
    public Applications getInstalledApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public boolean isLocalApplication( final ApplicationKey key )
    {
        return doIsLocalApplication( key );
    }

    private boolean doIsLocalApplication( final ApplicationKey key )
    {
        return Boolean.TRUE.equals( localApplicationSet.get( key ) );
    }

    @Override
    public void startApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doStartApplication( key, triggerEvent && !doIsLocalApplication( key ) ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doStopApplication( key, triggerEvent && !doIsLocalApplication( key ) ) );
    }

    @Override
    public Application installGlobalApplication( final URL url )
    {
        return ApplicationHelper.callWithContext( () -> doInstallGlobalApplicationFromUrl( url ) );
    }

    @Override
    public Application installGlobalApplication( final ByteSource byteSource )
    {
        return ApplicationHelper.callWithContext( () -> doInstallGlobalApplication( byteSource ) );
    }

    private Application doInstallGlobalApplicationFromUrl( final URL url )
    {
        final ByteSource byteSource = loadApplication( url, true );

        return this.doInstallGlobalApplication( byteSource );
    }

    private Application doInstallGlobalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, true );

        LOG.info( "Application [{}] installed successfully", application.getKey() );

        publishInstalledEvent( application );

        doStartApplication( application.getKey(), true );

        return application;
    }

    @Override
    public Application installLocalApplication( final ByteSource byteSource )
    {
        return ApplicationHelper.callWithContext( () -> doInstallLocalApplication( byteSource ) );
    }

    private Application doInstallLocalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, false );

        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), false );

        return application;
    }

    @Override
    public Application installStoredApplication( final NodeId nodeId )
    {
        return ApplicationHelper.callWithContext( () -> doInstallStoredApplication( nodeId ) );
    }

    private Application doInstallStoredApplication( final NodeId nodeId )
    {
        final Application application = doInstallApplication( nodeId, true );

        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), false );

        return application;
    }

    private void installAllStoredApplications()
    {
        LOG.info( "Searching for installed applications" );

        final Nodes applicationNodes = repoService.getApplications();

        LOG.info( "Found [" + applicationNodes.getSize() + "] installed applications" );

        for ( final Node applicationNode : applicationNodes )
        {
            final Application installedApp;
            try
            {
                installedApp = doInstallApplication( applicationNode.id(), true );

                if ( storedApplicationIsStarted( applicationNode ) )
                {
                    doStartApplication( installedApp.getKey(), false );
                }

                LOG.info( "Application [{}] installed successfully", installedApp.getKey() );
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot install application [{}]", applicationNode.name(), e );
            }
        }
    }

    @Override
    public void uninstallApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doUninstallApplication( key, triggerEvent ) );
    }

    private void doUninstallApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            LOG.warn( "Trying to uninstall bundle with key: [{}] but no such bundle installed", key );
            return;
        }

        doUninstallApplication( application );

        final Boolean local = localApplicationSet.remove( key );

        if ( local )
        {
            reinstallGlobalApplicationIfExists( key, application );
        }

        if ( triggerEvent )
        {
            this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( application.getKey() ) );
        }

    }

    private void publishInstalledEvent( final Application application )
    {
        final Node node = this.repoService.getApplicationNode( application.getKey() );
        this.eventPublisher.publish( ApplicationClusterEvents.installed( node ) );
    }

    private void reinstallGlobalApplicationIfExists( final ApplicationKey key, final Application application )
    {
        final Node applicationNode = this.repoService.getApplicationNode( key );

        if ( applicationNode != null )
        {
            doInstallApplication( applicationNode.id(), true );

            LOG.info( "Application [{}] installed successfully", application.getKey() );

            if ( Boolean.TRUE.equals( storedApplicationIsStarted( applicationNode ) ) )
            {
                doStartApplication( application.getKey(), false );
            }
        }
    }

    private void doStartApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStartApplication( this.registry.get( key ) );

        if ( triggerEvent )
        {
            this.repoService.updateStartedState( key, true );
            this.eventPublisher.publish( ApplicationClusterEvents.started( key ) );
        }

    }

    private void doStopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStopApplication( this.registry.get( key ) );

        if ( triggerEvent )
        {
            this.repoService.updateStartedState( key, false );
            this.eventPublisher.publish( ApplicationClusterEvents.stopped( key ) );
        }
    }

    private void doStartApplication( final Application application )
    {
        try
        {
            application.getBundle().start();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void doStopApplication( final Application application )
    {
        try
        {
            application.getBundle().stop();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private Application doInstallApplication( final NodeId nodeId, final boolean global )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        if ( byteSource == null )
        {
            throw new ApplicationInstallException( "Cannot install application with id [" + nodeId + "], source not found" );
        }

        return installOrUpdateApplication( byteSource, global );
    }

    private void doUninstallApplication( final Application application )
    {
        final Bundle bundle = application.getBundle();

        try
        {
            bundle.uninstall();
        }
        catch ( BundleException e )
        {
            throw new ApplicationInstallException( "Cannot uninstall bundle " + application.getKey(), e );
        }

        this.registry.invalidate( application.getKey() );

        final boolean localApp = this.doIsLocalApplication( application.getKey() );

        if ( !localApp )
        {
            this.repoService.deleteApplicationNode( application );
        }
    }

    private Application installOrUpdateApplication( final ByteSource byteSource, final boolean global )
    {
        final ApplicationKey applicationKey = getApplicationKey( byteSource );

        final boolean update = applicationBundleInstalled( applicationKey );

        final Application application;

        if ( update )
        {
            application = handleUpdate( applicationKey, byteSource, global );
        }
        else
        {
            application = handleInstall( byteSource, applicationKey, global );
        }

        if ( global && alreadyInRepo( applicationKey ) )
        {
            repoService.updateApplicationNode( application, byteSource );
        }
        else if ( global )
        {
            repoService.createApplicationNode( application, byteSource );
        }

        return application;
    }

    private ByteSource loadApplication( final URL url, final boolean triggerEvent )
    {
        try
        {
            URLConnection connection = url.openConnection();

            ByteArrayOutputStream os = null;

            try (final InputStream is = connection.getInputStream())
            {
                int totalLength = connection.getContentLength();
                int bytesRead;
                int totalRead = 0;
                int lastPct = 0;
                int currentPct;
                byte[] buffer = new byte[8192];
                os = new ByteArrayOutputStream();

                while ( ( bytesRead = is.read( buffer ) ) != -1 )
                {
                    os.write( buffer, 0, bytesRead );
                    totalRead += bytesRead;

                    currentPct = totalRead * 100 / totalLength;

                    if ( triggerEvent && lastPct != currentPct )
                    {
                        this.eventPublisher.publish( ApplicationEvents.progress( url.toString(), currentPct ) );
                        lastPct = currentPct;
                    }
                }
                os.flush();

                return ByteSource.wrap( os.toByteArray() );
            }
            finally
            {
                if ( os != null )
                {
                    os.close();
                }
            }
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to load application from " + url, e );
            return null;
        }
    }

    private boolean alreadyInRepo( final ApplicationKey applicationKey )
    {
        return repoService.getApplicationNode( applicationKey ) != null;
    }

    private boolean storedApplicationIsStarted( final Node node )
    {
        final PropertyTree data = node.data();
        return data.getBoolean( ApplicationPropertyNames.STARTED );
    }

    private boolean applicationBundleInstalled( final ApplicationKey applicationKey )
    {
        return this.localApplicationSet.containsKey( applicationKey );
    }

    private Application handleInstall( final ByteSource byteSource, final ApplicationKey applicationKey, final boolean global )
    {
        final Bundle bundle = doInstallBundle( byteSource, applicationKey );

        localApplicationSet.compute( applicationKey, ( key, present ) -> !global );

        return this.registry.get( ApplicationKey.from( bundle ) );
    }

    private Application handleUpdate( final ApplicationKey applicationKey, final ByteSource source, final boolean global )
    {
        final boolean ignoreLocalUpdate = doIsLocalApplication( applicationKey ) && global;

        if ( ignoreLocalUpdate )
        {
            LOG.warn( "Application : '" + applicationKey + "' installed locally, installed version will be overrided by local" );
            return this.registry.get( applicationKey );
        }
        else
        {
            uninstallBundle( applicationKey );

            this.registry.invalidate( applicationKey );

            final Bundle bundle = doInstallBundle( source, applicationKey );

            localApplicationSet.compute( applicationKey, ( key, present ) -> !global );

            return this.registry.get( ApplicationKey.from( bundle ) );
        }
    }

    private void uninstallBundle( final ApplicationKey applicationKey )
    {
        try
        {
            final Bundle bundle = this.context.getBundle( applicationKey.getName() );

            if ( bundle != null )
            {
                bundle.uninstall();
            }
        }
        catch ( BundleException e )
        {
            e.printStackTrace();
        }
    }

    private ApplicationKey getApplicationKey( final ByteSource byteSource )
    {
        final String applicationName;

        try
        {
            applicationName = ApplicationNameResolver.resolve( byteSource );
        }
        catch ( Exception e )
        {
            throw new ApplicationInstallException( "Cannot install application: " + e.getMessage(), e );
        }
        return ApplicationKey.from( applicationName );
    }

    private Bundle doInstallBundle( final ByteSource source, final ApplicationKey applicationKey )
    {
        try (final InputStream in = source.openStream())
        {
            return this.context.installBundle( applicationKey.getName(), in );
        }
        catch ( BundleException e )
        {
            throw new ApplicationInstallException( "Could not install application bundle:   '" + applicationKey + "'", e );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to install bundle", e );
        }
    }


    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.registry.invalidate( key );
    }

    @Reference
    public void setRepoService( final ApplicationRepoService repoService )
    {
        this.repoService = repoService;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
