package com.enonic.xp.core.impl.app;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    private final Set<ApplicationKey> localApplicationSet = Collections.newSetFromMap( new ConcurrentHashMap<>() );

    private final ApplicationRegistry registry;

    private final ApplicationRepoService repoService;

    private final EventPublisher eventPublisher;

    private final ApplicationLoader applicationLoader;

    private final AppFilterService appFilterService;

    private final VirtualAppService virtualAppService;

    private final ApplicationAuditLogSupport applicationAuditLogSupport;

    @Activate
    public ApplicationServiceImpl( @Reference final ApplicationRegistry applicationRegistry,
                                   @Reference final ApplicationRepoService repoService, @Reference final EventPublisher eventPublisher,
                                   @Reference final AppFilterService appFilterService, @Reference final VirtualAppService virtualAppService,
                                   @Reference final ApplicationAuditLogSupport applicationAuditLogSupport )
    {
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
            registry.uninstall( applicationKey.getKey() );
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
    public ApplicationKeys getInstalledApplicationKeys()
    {
        return getInstalledApplications().getApplicationKeys();
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
    public void startApplication( final ApplicationKey key, final boolean unused )
    {
        final boolean global = !localApplicationSet.contains( key );
        ApplicationHelper.runWithContext( () -> {
            if ( global )
            {
                this.eventPublisher.publish( ApplicationClusterEvents.start( key ) );
            }

            doStartApplication( key );

            if ( global )
            {
                this.repoService.updateStartedState( key, true );
                this.eventPublisher.publish( ApplicationClusterEvents.started( key ) );
            }
            if ( global )
            {
                applicationAuditLogSupport.startApplication( key );
            }
        } );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean unused )
    {
        final boolean global = !localApplicationSet.contains( key );
        ApplicationHelper.runWithContext( () -> {
            if ( global )
            {
                this.eventPublisher.publish( ApplicationClusterEvents.stop( key ) );
            }

            doStopApplication( key );

            if ( global )
            {
                this.repoService.updateStartedState( key, false );
                this.eventPublisher.publish( ApplicationClusterEvents.stopped( key ) );
            }

            if ( global )
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
        return installGlobalApplication( applicationLoader.load( url, sha512 ), null );
    }

    @Override
    public Application installGlobalApplication( final ByteSource byteSource, final String unused )
    {
        return ApplicationHelper.callWithContext( () -> {
            final Application application = doInstallGlobalApplication( byteSource );
            applicationAuditLogSupport.installApplication( application.getKey() );
            return application;
        } );
    }

    @Override
    public Application installLocalApplication( final ByteSource byteSource, final String unused )
    {
        return ApplicationHelper.callWithContext( () -> doInstallLocalApplication( byteSource ) );
    }

    @Override
    @Deprecated
    public Application installStoredApplication( final NodeId nodeId )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public Application installStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void installAllStoredApplications( ApplicationInstallationParams params )
    {
        ApplicationHelper.runWithContext( this::doInstallStoredApplications );
    }

    @Override
    public void installAllStoredApplications()
    {
        ApplicationHelper.runWithContext( this::doInstallStoredApplications );
    }

    @Override
    public void uninstallApplication( final ApplicationKey key, final boolean unused )
    {
        final boolean global = !localApplicationSet.remove( key );
        ApplicationHelper.runWithContext( () -> {
            if ( global )
            {
                this.eventPublisher.publish( ApplicationClusterEvents.uninstall( key ) );
            }

            doUninstallApplication( key );

            if ( global )
            {
                this.repoService.deleteApplicationNode( key );
            }
            else
            {
                doReinstallStoredApplication( key );
            }
            applicationAuditLogSupport.uninstallApplication( key );
            if ( global )
            {
                this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( key ) );
            }
        } );
    }

    @Override
    public void publishUninstalledEvent( final ApplicationKey key )
    {
        this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( key ) );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
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
        final AppInfo appInfo = getAppInfo( byteSource );

        final ApplicationKey applicationKey = ApplicationKey.from( appInfo.name );

        final Application localApp = findLocal( applicationKey );
        if ( localApp != null )
        {
            throw new ApplicationInstallException( String.format( "Local application %s exists on this instance", applicationKey ) );
        }

        if ( !appFilterService.accept( applicationKey ) )
        {
            throw new ApplicationInstallException( String.format( "Application %s is not permitted on this instance", applicationKey ) );
        }

        final Node applicationNode = repoService.upsertApplicationNode( appInfo, byteSource );

        this.eventPublisher.publish( ApplicationClusterEvents.install( applicationKey, applicationNode.id() ) );

        final Application application = doInstallApplication( byteSource, applicationKey );

        LOG.info( "Global Application [{}] installed successfully", applicationKey );

        this.eventPublisher.publish( ApplicationClusterEvents.installed( applicationKey, applicationNode.id() ) );

        this.eventPublisher.publish( ApplicationClusterEvents.start( applicationKey ) );

        final boolean started = tryStartApplication( applicationKey );
        if ( started )
        {
            this.repoService.updateStartedState( applicationKey, true );
            this.eventPublisher.publish( ApplicationClusterEvents.started( applicationKey ) );
        }
        return application;
    }

    private void doInstallStoredApplication( final NodeId nodeId )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        if ( byteSource == null )
        {
            throw new ApplicationInstallException( "Cannot install application with id [" + nodeId + "], source not found" );
        }
        final ApplicationKey applicationKey = ApplicationKey.from( getAppInfo( byteSource ).name );

        final Application localApp = findLocal( applicationKey );
        if ( localApp != null )
        {
            return;
        }

        if ( !appFilterService.accept( applicationKey ) )
        {
            LOG.info( "Application {} is not permitted on this instance", applicationKey );
            return;
        }

        doInstallApplication( byteSource, applicationKey );

        LOG.info( "Stored application [{}] installed successfully", applicationKey );
    }

    private void doInstallStoredApplications()
    {
        LOG.info( "Searching for installed applications" );

        final Nodes applicationNodes = repoService.getApplications();

        LOG.info( "Found [{}] installed applications", applicationNodes.getSize() );

        for ( final Node applicationNode : applicationNodes )
        {
            try
            {
                doInstallAndStartStoredApplication( applicationNode );
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot install stored application [{}]", applicationNode.name(), e );
            }
        }
    }

    private void doReinstallStoredApplication( final ApplicationKey applicationKey )
    {
        try
        {
            final Node applicationNode = this.repoService.getApplicationNode( applicationKey );
            if ( applicationNode != null )
            {
                doInstallAndStartStoredApplication( applicationNode );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Cannot reinstall stored application [{}]", applicationKey, e );
        }
    }

    private Application doInstallLocalApplication( final ByteSource byteSource )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( getAppInfo( byteSource ).name );

        final Application application = doInstallApplication( byteSource, applicationKey );
        localApplicationSet.add( applicationKey );

        LOG.info( "Local application [{}] installed successfully", applicationKey );

        tryStartApplication( applicationKey );
        return application;
    }

    private void doInstallAndStartStoredApplication( final Node applicationNode )
    {
        doInstallStoredApplication( applicationNode.id() );
        final boolean started = Boolean.TRUE.equals( applicationNode.data().getBoolean( ApplicationPropertyNames.STARTED ) );
        if ( started )
        {
            tryStartApplication( ApplicationKey.from( applicationNode.name().toString() ) );
        }
    }

    private Application doInstallApplication( final ByteSource byteSource, final ApplicationKey applicationKey )
    {
        this.registry.uninstall( applicationKey );
        final Application application = this.registry.install( applicationKey, byteSource );
        this.eventPublisher.publish( ApplicationEvents.installed( applicationKey ) );
        return application;
    }

    private void doUninstallApplication( final ApplicationKey applicationKey )
    {
        registry.uninstall( applicationKey );
        this.eventPublisher.publish( ApplicationEvents.uninstalled( applicationKey ) );
    }

    private void doStartApplication( final ApplicationKey applicationKey )
    {
        this.registry.start( applicationKey );
        this.eventPublisher.publish( ApplicationEvents.started( applicationKey ) );
    }

    private void doStopApplication( final ApplicationKey applicationKey )
    {
        this.registry.stop( applicationKey );
        this.eventPublisher.publish( ApplicationEvents.stopped( applicationKey ) );
    }

    private boolean tryStartApplication( final ApplicationKey applicationKey )
    {
        try
        {
            doStartApplication( applicationKey );
            return true;
        }
        catch ( ApplicationInvalidVersionException e )
        {
            LOG.warn( "Application [{}] has an invalid system version range [{}]. Current system version is [{}]", applicationKey,
                      e.getAppSystemVersionRange(), e.getSystemVersion() );
        }
        return false;
    }

    private Application findLocal( final ApplicationKey applicationKey )
    {
        final boolean ignoreLocalUpdate = localApplicationSet.contains( applicationKey );
        if ( ignoreLocalUpdate )
        {
            final Application existingApplication = this.registry.get( applicationKey );
            if ( existingApplication != null )
            {
                LOG.info( "Ignoring global application [{}] install/update since local application exists", applicationKey );
                return existingApplication;
            }
        }
        return null;
    }

    private AppInfo getAppInfo( final ByteSource byteSource )
    {
        try
        {
            return AppInfoResolver.resolve( byteSource );
        }
        catch ( Exception e )
        {
            throw new ApplicationInstallException( "Cannot install application", e );
        }
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

    @Override
    public void onEvent( final Event event )
    {
        if ( !event.isLocalOrigin() && ApplicationClusterEvents.EVENT_TYPE.equals( event.getType() ) )
        {
            event.getValueAs( String.class, ApplicationClusterEvents.EVENT_TYPE_KEY ).ifPresent( eventSubType -> {
                switch ( eventSubType )
                {
                    case ApplicationClusterEvents.INSTALL:
                        handleInstallEvent( event );
                        break;
                    case ApplicationClusterEvents.UNINSTALL:
                        handleEvent( event, this::doUninstallApplication );
                        break;
                    case ApplicationClusterEvents.START:
                        handleEvent( event, this::doStartApplication );
                        break;
                    case ApplicationClusterEvents.STOP:
                        handleEvent( event, this::doStopApplication );
                        break;
                    default:
                        LOG.debug( "Ignoring {} {}", ApplicationClusterEvents.EVENT_TYPE, eventSubType );
                        break;
                }
            } );
        }
    }

    private void handleInstallEvent( final Event event )
    {
        event.getValueAs( String.class, ApplicationClusterEvents.NODE_ID_PARAM )
            .map( NodeId::from )
            .ifPresent( nodeId -> ApplicationHelper.runAsAdmin( () -> doInstallStoredApplication( nodeId ) ) );
    }

    private void handleEvent( final Event event, final Consumer<ApplicationKey> callback )
    {
        event.getValueAs( String.class, ApplicationClusterEvents.APPLICATION_KEY_PARAM )
            .map( ApplicationKey::from )
            .filter( key -> !localApplicationSet.contains( key ) )
            .ifPresent( applicationKey -> ApplicationHelper.runAsAdmin( () -> callback.accept( applicationKey ) ) );
    }
}
