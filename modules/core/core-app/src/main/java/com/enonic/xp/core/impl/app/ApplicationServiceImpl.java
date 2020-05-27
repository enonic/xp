package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    private final BundleContext context;

    private final Set<ApplicationKey> localApplicationSet = Collections.newSetFromMap( new ConcurrentHashMap<>() );

    private final ApplicationRegistry registry;

    private final ApplicationRepoService repoService;

    private final EventPublisher eventPublisher;

    private final ApplicationLoader applicationLoader;

    @Activate
    public ApplicationServiceImpl( final BundleContext context, @Reference final ApplicationRegistry applicationRegistry,
                                   @Reference final ApplicationRepoService repoService, @Reference final EventPublisher eventPublisher )
    {
        this.context = context;
        this.registry = applicationRegistry;
        this.repoService = repoService;
        this.eventPublisher = eventPublisher;
        this.applicationLoader = new ApplicationLoader( eventPublisher::publish );
    }

    @Override
    public Application getInstalledApplication( final ApplicationKey key )
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
        return registry.getAll();
    }

    @Override
    public boolean isLocalApplication( final ApplicationKey key )
    {
        return localApplicationSet.contains( key );
    }

    @Override
    public void startApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doStartApplication( key, triggerEvent && !localApplicationSet.contains( key ), true ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doStopApplication( key, triggerEvent && !localApplicationSet.contains( key ) ) );
    }

    @Override
    public Application installGlobalApplication( final URL url )
    {
        return ApplicationHelper.callWithContext( () -> doInstallGlobalApplicationFromUrl( url ) );
    }

    @Override
    public Application installGlobalApplication( final ByteSource byteSource, final String applicationName )
    {
        return ApplicationHelper.callWithContext( () -> {
            try
            {
                return doInstallGlobalApplication( byteSource );
            }
            catch ( ApplicationInstallException e )
            {
                throw new GlobalApplicationInstallException( "'" + applicationName + "': " + e.getMessage() );
            }
        } );
    }

    @Override
    public Application installLocalApplication( final ByteSource byteSource, final String applicationName )
    {
        return ApplicationHelper.callWithContext( () -> {
            try
            {
                return doInstallLocalApplication( byteSource );
            }
            catch ( ApplicationInstallException e )
            {
                throw new LocalApplicationInstallException( "'" + applicationName + "': " + e.getMessage() );
            }
        } );
    }

    @Override
    @Deprecated
    public Application installStoredApplication( final NodeId nodeId )
    {
        final ApplicationInstallationParams params = ApplicationInstallationParams.create().
            triggerEvent( false ).
            build();
        return installStoredApplication( nodeId, params );
    }

    @Override
    public Application installStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        return ApplicationHelper.callWithContext( () -> doInstallStoredApplication( nodeId, params ) );
    }

    @Override
    @Deprecated
    public void installAllStoredApplications()
    {
        installAllStoredApplications( ApplicationInstallationParams.create().triggerEvent( false ).build() );
    }

    @Override
    public void installAllStoredApplications( final ApplicationInstallationParams params )
    {
        ApplicationHelper.runWithContext( () -> doInstallStoredApplications( params ) );
    }

    @Override
    public void uninstallApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> doUninstallApplication( key, triggerEvent ) );
    }

    @Override
    public void publishUninstalledEvent( final ApplicationKey applicationKey )
    {
        final Node node = this.repoService.getApplicationNode( applicationKey );
        if ( node != null )
        {
            doPublishUninstalledEvent( applicationKey );
        }
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
    }

    private Application doInstallGlobalApplicationFromUrl( final URL url )
    {
        final ByteSource byteSource = applicationLoader.load( url );

        return doInstallGlobalApplication( byteSource );
    }

    private Application doInstallGlobalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, false, true );

        final ApplicationKey applicationKey = application.getKey();
        LOG.info( "Global Application [{}] installed successfully", applicationKey );

        doPublishInstalledEvent( applicationKey );

        doStartApplication( applicationKey, true, false );

        return application;
    }

    private Application doInstallLocalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, true, false );

        final ApplicationKey applicationKey = application.getKey();
        LOG.info( "Local application [{}] installed successfully", applicationKey );

        doStartApplication( applicationKey, false, false );

        return application;
    }

    private Application doInstallStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        final Application application = doInstallStoredApplication( nodeId );

        final ApplicationKey applicationKey = application.getKey();
        LOG.info( "Stored application [{}] installed successfully", applicationKey );

        if ( params.isTriggerEvent() )
        {
            doPublishInstalledEvent( applicationKey );
        }

        if ( params.isStart() )
        {
            doStartApplication( applicationKey, params.isTriggerEvent(), false );
        }

        return application;
    }

    private void reinstallStoredApplicationIfExists( final ApplicationKey applicationKey )
    {
        final Node applicationNode = this.repoService.getApplicationNode( applicationKey );

        if ( applicationNode != null )
        {
            doInstallStoredApplication( applicationNode.id() );
            LOG.info( "Stored application [{}] installed successfully", applicationKey );

            if ( storedApplicationIsStarted( applicationNode ) )
            {
                doStartApplication( applicationKey, false, false );
            }
        }
    }

    private void doInstallStoredApplications( final ApplicationInstallationParams params )
    {
        LOG.info( "Searching for installed applications" );

        final Nodes applicationNodes = repoService.getApplications();

        LOG.info( "Found [" + applicationNodes.getSize() + "] installed applications" );

        for ( final Node applicationNode : applicationNodes )
        {
            try
            {
                final Application application = doInstallStoredApplication( applicationNode.id() );

                final ApplicationKey applicationKey = application.getKey();
                LOG.info( "Stored application [{}] installed successfully", applicationKey );

                if ( params.isTriggerEvent() )
                {
                    doPublishInstalledEvent( applicationNode );
                }

                if ( params.isStart() && storedApplicationIsStarted( applicationNode ) )
                {
                    doStartApplication( applicationKey, params.isTriggerEvent(), false );
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot install application [{}]", applicationNode.name(), e );
            }
        }
    }

    private Application doInstallStoredApplication( final NodeId nodeId )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        if ( byteSource == null )
        {
            throw new ApplicationInstallException( "Cannot install application with id [" + nodeId + "], source not found" );
        }

        return installOrUpdateApplication( byteSource, false, false );
    }

    private void doUninstallApplication( final ApplicationKey applicationKey, final boolean triggerEvent )
    {
        registry.uninstallApplication( applicationKey );

        final boolean wasLocal = localApplicationSet.remove( applicationKey );

        this.eventPublisher.publish( ApplicationEvents.uninstalled( applicationKey ) );

        if ( wasLocal )
        {
            try
            {
                reinstallStoredApplicationIfExists( applicationKey );
            }
            catch ( Exception e )
            {
                LOG.warn( "Cannot reinstall global application [{}]", applicationKey, e );
            }
        }
        else
        {
            this.repoService.deleteApplicationNode( applicationKey );
        }

        if ( triggerEvent )
        {
            doPublishUninstalledEvent( applicationKey );
        }
    }

    private void doStartApplication( final ApplicationKey applicationKey, final boolean triggerEvent, final boolean throwOnInvalidVersion )
    {
        final boolean started = this.registry.startApplication( applicationKey, throwOnInvalidVersion );
        if ( started )
        {
            LOG.info( "Application [{}] started successfully", applicationKey );

            this.eventPublisher.publish( ApplicationEvents.started( applicationKey ) );
            if ( triggerEvent )
            {
                this.repoService.updateStartedState( applicationKey, true );
                this.eventPublisher.publish( ApplicationClusterEvents.started( applicationKey ) );
            }
        }
    }

    private void doStopApplication( final ApplicationKey applicationKey, final boolean triggerEvent )
    {
        this.registry.stopApplication( applicationKey );

        this.eventPublisher.publish( ApplicationEvents.stopped( applicationKey ) );
        if ( triggerEvent )
        {
            this.repoService.updateStartedState( applicationKey, false );
            this.eventPublisher.publish( ApplicationClusterEvents.stopped( applicationKey ) );
        }
    }

    private Application installOrUpdateApplication( final ByteSource byteSource, final boolean local, final boolean updateRepository )
    {
        final ApplicationKey applicationKey = getApplicationKey( byteSource );

        final Application existingApplication = this.registry.get( applicationKey );

        if ( existingApplication != null )
        {
            final boolean ignoreLocalUpdate = localApplicationSet.contains( applicationKey ) && !local;

            if ( ignoreLocalUpdate )
            {
                LOG.warn( "Application '{}' installed locally. Local application has higher priority", applicationKey );
                return existingApplication;
            }
            else
            {
                this.registry.uninstallApplication( applicationKey );
            }
        }

        final Application application;
        try (final InputStream in = byteSource.openStream())
        {
            LOG.debug( "Installing application {} bundle", applicationKey );
            final Bundle bundle;
            try
            {
                bundle = context.installBundle( applicationKey.getName(), in );
            }
            catch ( BundleException e )
            {
                throw new ApplicationInstallException( "Could not install application bundle: '" + applicationKey + "'", e );
            }
            LOG.info( "Installed application {} bundle {}", applicationKey, bundle.getBundleId() );

            application = registry.installApplication( bundle );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to install bundle", e );
        }

        if ( local )
        {
            localApplicationSet.add( applicationKey );
        }

        this.eventPublisher.publish( ApplicationEvents.installed( applicationKey ) );

        if ( updateRepository )
        {
            if ( alreadyInRepo( applicationKey ) )
            {
                repoService.updateApplicationNode( application, byteSource );
            }
            else
            {
                repoService.createApplicationNode( application, byteSource );
            }
        }

        return application;
    }

    private void doPublishInstalledEvent( final ApplicationKey key )
    {
        final Node applicationNode = this.repoService.getApplicationNode( key );
        if ( applicationNode != null )
        {
            doPublishInstalledEvent( applicationNode );
        }
        else
        {
            LOG.warn( "Could not find application {} in repository", key );
        }
    }

    private void doPublishInstalledEvent( final Node applicationNode )
    {
        this.eventPublisher.publish( ApplicationClusterEvents.installed( applicationNode ) );
    }

    private void doPublishUninstalledEvent( final ApplicationKey key )
    {
        this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( key ) );
    }

    private boolean alreadyInRepo( final ApplicationKey applicationKey )
    {
        return repoService.getApplicationNode( applicationKey ) != null;
    }

    private boolean storedApplicationIsStarted( final Node node )
    {
        final PropertyTree data = node.data();
        return Boolean.TRUE.equals( data.getBoolean( ApplicationPropertyNames.STARTED ) );
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
            throw new ApplicationInstallException( "Cannot install application", e );
        }
        return ApplicationKey.from( applicationName );
    }
}
