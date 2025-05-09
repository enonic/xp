package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationMode;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.app.event.ApplicationClusterEvents;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    private final BundleContext context;

    private final Set<ApplicationKey> localApplicationSet = Collections.newSetFromMap( new ConcurrentHashMap<>() );

    private final ApplicationRegistry registry;

    private final ApplicationRepoService repoService;

    private final EventPublisher eventPublisher;

    private final ApplicationLoader applicationLoader;

    private final AppFilterService appFilterService;

    private final VirtualAppService virtualAppService;

    private final ApplicationAuditLogSupport applicationAuditLogSupport;

    @Activate
    public ApplicationServiceImpl( final BundleContext context, @Reference final ApplicationRegistry applicationRegistry,
                                   @Reference final ApplicationRepoService repoService, @Reference final EventPublisher eventPublisher,
                                   @Reference final AppFilterService appFilterService, @Reference final VirtualAppService virtualAppService,
                                   @Reference final ApplicationAuditLogSupport applicationAuditLogSupport )
    {
        this.context = context;
        this.registry = applicationRegistry;
        this.repoService = repoService;
        this.eventPublisher = eventPublisher;
        this.applicationLoader = new ApplicationLoader( eventPublisher::publish );
        this.appFilterService = appFilterService;
        this.virtualAppService = virtualAppService;
        this.applicationAuditLogSupport = applicationAuditLogSupport;
    }

    @Deactivate
    public void deactivate()
    {
        for ( Application applicationKey : registry.getAll() )
        {
            registry.uninstallApplication( applicationKey.getKey() );
        }
    }

    @Override
    public Application getInstalledApplication( final ApplicationKey key )
    {
        return this.registry.get( key );
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        final Application installedApplication = this.registry.get( key );
        return installedApplication != null ? installedApplication : virtualAppService.get( key );
    }

    @Override
    public Applications getInstalledApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public Applications list()
    {
        return Applications.from( Stream.concat( this.registry.getAll().stream(), virtualAppService.list().stream() )
                                      .collect( Collectors.toMap( Application::getKey, Function.identity(), ( first, second ) -> first ) )
                                      .values() );
    }

    @Override
    public boolean isLocalApplication( final ApplicationKey key )
    {
        return localApplicationSet.contains( key );
    }

    @Override
    public void startApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> {
            final boolean isTriggerEvent = triggerEvent && !localApplicationSet.contains( key );
            doStartApplication( key, isTriggerEvent, true );

            if ( isTriggerEvent )
            {
                applicationAuditLogSupport.startApplication( key );
            }
        } );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> {
            final boolean isTriggerEvent = triggerEvent && !localApplicationSet.contains( key );
            doStopApplication( key, isTriggerEvent );

            if ( isTriggerEvent )
            {
                applicationAuditLogSupport.stopApplication( key );
            }
        } );
    }

    @Override
    public Application installGlobalApplication( final URL url )
    {
        return installGlobalApplication( url, null );
    }

    @Override
    public Application installGlobalApplication( final URL url, final byte[] sha512 )
    {
        return ApplicationHelper.callWithContext( () -> {
            final Application application = doInstallGlobalApplication( applicationLoader.load( url, sha512 ) );
            applicationAuditLogSupport.installApplication( application.getKey(), url );
            return application;
        } );
    }

    @Override
    public Application installGlobalApplication( final ByteSource byteSource, final String applicationName )
    {
        return ApplicationHelper.callWithContext( () -> {
            final Application application = doInstallGlobalApplication( byteSource );
            applicationAuditLogSupport.installApplication( application.getKey() );
            return application;
        } );
    }

    @Override
    public Application installLocalApplication( final ByteSource byteSource, final String applicationName )
    {
        return ApplicationHelper.callWithContext( () -> doInstallLocalApplication( byteSource ) );
    }

    @Override
    public Application installStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        return ApplicationHelper.callWithContext( () -> doInstallStoredApplication( nodeId, params ) );
    }

    @Override
    public void installAllStoredApplications( final ApplicationInstallationParams params )
    {
        ApplicationHelper.runWithContext( () -> doInstallStoredApplications( params ) );
    }

    @Override
    public void uninstallApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        ApplicationHelper.runWithContext( () -> {
            doUninstallApplication( key, triggerEvent );

            applicationAuditLogSupport.uninstallApplication( key );
        } );
    }


    @Override
    public Application createVirtualApplication( final CreateVirtualApplicationParams params )
    {
        return this.virtualAppService.create( params );
    }

    @Override
    public boolean deleteVirtualApplication( final ApplicationKey key )
    {
        return this.virtualAppService.delete( key );
    }

    @Override
    public ApplicationMode getApplicationMode( final ApplicationKey applicationKey )
    {
        requireSchemaAdminRole();

        final boolean hasReal = this.registry.get( applicationKey ) != null;
        final boolean hasVirtual =
            VirtualAppContext.createAdminContext().callWith( () -> this.virtualAppService.get( applicationKey ) ) != null;

        if ( hasReal )
        {
            if ( hasVirtual )
            {
                return ApplicationMode.AUGMENTED;
            }
            else
            {
                return ApplicationMode.BUNDLED;
            }
        }
        else if ( hasVirtual )
        {
            return ApplicationMode.VIRTUAL;
        }

        return null;
    }

    private Application doInstallGlobalApplication( final ByteSource byteSource )
    {
        final ApplicationKey applicationKey = getApplicationKey( byteSource );
        if ( !appFilterService.accept( applicationKey ) )
        {
            throw new ApplicationInstallException( String.format( "Application %s is not permitted on this instance", applicationKey ) );
        }

        final Application application = installOrUpdateApplication( byteSource, applicationKey, false, true );

        LOG.info( "Global Application [{}] installed successfully", applicationKey );

        doPublishInstalledEvent( applicationKey );

        doStartApplication( applicationKey, true, false );

        return application;
    }

    private Application doInstallLocalApplication( final ByteSource byteSource )
    {
        final ApplicationKey applicationKey = getApplicationKey( byteSource );

        final Application application = installOrUpdateApplication( byteSource, applicationKey, true, false );

        LOG.info( "Local application [{}] installed successfully", applicationKey );

        doStartApplication( applicationKey, false, false );

        return application;
    }

    private Application doInstallStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        final Application application = doInstallStoredApplication( nodeId );

        if ( application == null )
        {
            return null;
        }
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
            final Application application = doInstallStoredApplication( applicationNode.id() );
            if ( application == null )
            {
                return;
            }
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
                if ( application == null )
                {
                    continue;
                }
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

        final ApplicationKey applicationKey = getApplicationKey( byteSource );
        if ( !appFilterService.accept( applicationKey ) )
        {
            LOG.info( "Application {} is not permitted on this instance", applicationKey );
            return null;
        }

        return installOrUpdateApplication( byteSource, applicationKey, false, false );
    }

    private void doUninstallApplication( final ApplicationKey applicationKey, final boolean triggerEvent )
    {
        if ( triggerEvent )
        {
            this.eventPublisher.publish( ApplicationClusterEvents.uninstall( applicationKey ) );
        }
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
        if ( triggerEvent )
        {
            this.eventPublisher.publish( ApplicationClusterEvents.start( applicationKey ) );
        }
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
        if ( triggerEvent )
        {
            this.eventPublisher.publish( ApplicationClusterEvents.stop( applicationKey ) );
        }
        this.registry.stopApplication( applicationKey );

        this.eventPublisher.publish( ApplicationEvents.stopped( applicationKey ) );
        if ( triggerEvent )
        {
            this.repoService.updateStartedState( applicationKey, false );
            this.eventPublisher.publish( ApplicationClusterEvents.stopped( applicationKey ) );
        }
    }

    private Application installOrUpdateApplication( final ByteSource byteSource, ApplicationKey applicationKey, final boolean local,
                                                    final boolean updateRepository )
    {
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
        try (InputStream in = byteSource.openStream())
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

    private void requireSchemaAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.SCHEMA_ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }
}
