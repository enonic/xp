package com.enonic.xp.core.impl.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationInvalidationLevel;
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
    implements ApplicationService
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    private final ConcurrentMap<ApplicationKey, Boolean> localApplicationSet = new ConcurrentHashMap<>();

    private final ApplicationRegistry registry = new ApplicationRegistry();

    private BundleContext context;

    private ApplicationRepoService repoService;

    private EventPublisher eventPublisher;

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        this.registry.activate( context );
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

    private Application doInstallGlobalApplicationFromUrl( final URL url )
    {
        final ByteSource byteSource = loadApplication( url, true );

        return this.doInstallGlobalApplication( byteSource );
    }

    private Application doInstallGlobalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, true, true );

        LOG.info( "Global Application [{}] installed successfully", application.getKey() );

        publishInstalledEvent( application );

        if ( checkApplicationValidity( application ) )
        {
            doStartApplication( application.getKey(), true );
        }

        return application;
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

    private Application doInstallLocalApplication( final ByteSource byteSource )
    {
        final Application application = installOrUpdateApplication( byteSource, false, false );

        LOG.info( "Local application [{}] installed successfully", application.getKey() );

        if ( checkApplicationValidity( application ) )
        {
            doStartApplication( application.getKey(), false );
        }

        return application;
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

    private Application doInstallStoredApplication( final NodeId nodeId, final ApplicationInstallationParams params )
    {
        final Application application = doInstallStoredApplication( nodeId );

        LOG.info( "Stored application [{}] installed successfully", application.getKey() );

        if ( params.isTriggerEvent() )
        {
            publishInstalledEvent( application );
        }

        if ( params.isStart() && checkApplicationValidity( application ) )
        {
            doStartApplication( application.getKey(), params.isTriggerEvent() );
        }

        return application;
    }

    @Override
    @Deprecated
    public void installAllStoredApplications()
    {
        final ApplicationInstallationParams params = ApplicationInstallationParams.create().
            triggerEvent( false ).
            build();
        installAllStoredApplications( params );
    }

    @Override
    public void installAllStoredApplications( final ApplicationInstallationParams params )
    {
        ApplicationHelper.runWithContext( () -> doInstallStoredApplications( params ) );
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
                final Application installedApp = doInstallStoredApplication( applicationNode.id() );

                LOG.info( "Stored application [{}] installed successfully", installedApp.getKey() );

                if ( params.isTriggerEvent() )
                {
                    publishInstalledEvent( installedApp );
                }

                if ( params.isStart() && storedApplicationIsStarted( applicationNode ) && checkApplicationValidity( installedApp ) )
                {
                    doStartApplication( installedApp.getKey(), params.isTriggerEvent() );
                }
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

        if ( Boolean.TRUE.equals( local ) )
        {
            try
            {
                reinstallGlobalApplicationIfExists( key, application );
            }
            catch ( Exception e )
            {
                LOG.warn( "Cannot reinstall global application [{}]", application.getKey(), e );
            }
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

    @Override
    public void publishUninstalledEvent( final ApplicationKey applicationKey )
    {
        final Node node = this.repoService.getApplicationNode( applicationKey );
        if ( node != null )
        {
            this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( applicationKey ) );
        }
    }

    private void reinstallGlobalApplicationIfExists( final ApplicationKey key, final Application application )
    {
        final Node applicationNode = this.repoService.getApplicationNode( key );

        if ( applicationNode != null )
        {
            final Application installedApplication = doInstallStoredApplication( applicationNode.id() );

            LOG.info( "Application [{}] installed successfully", application.getKey() );

            if ( Boolean.TRUE.equals( storedApplicationIsStarted( applicationNode ) ) && checkApplicationValidity( installedApplication ) )
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
            final Version systemVersion = getSystemVersion();
            if ( !application.includesSystemVersion( systemVersion ) )
            {
                throw new ApplicationInvalidVersionException( application, systemVersion );
            }

            application.getBundle().start();
            LOG.info( "Application [{}] started successfully", application.getKey() );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private boolean checkApplicationValidity( final Application application )
    {
        final Version systemVersion = getSystemVersion();
        if ( !application.includesSystemVersion( systemVersion ) )
        {
            LOG.warn( "Application [{}] has an invalid system version range [{}]. Current system version is [{}]", application.getKey(),
                      application.getSystemVersion(), systemVersion );
            return false;
        }
        return true;
    }

    private Version getSystemVersion()
    {
        return this.context.getBundle().getVersion();
    }

    private void doStopApplication( final Application application )
    {
        try
        {
            application.getBundle().stop();
            LOG.info( "Application [{}] stopped successfully", application.getKey() );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private Application doInstallStoredApplication( final NodeId nodeId )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        if ( byteSource == null )
        {
            throw new ApplicationInstallException( "Cannot install application with id [" + nodeId + "], source not found" );
        }

        return installOrUpdateApplication( byteSource, true, false );
    }

    private void doUninstallApplication( final Application application )
    {
        final Bundle bundle = application.getBundle();

        try
        {
            LOG.info( "Application [{}] uninstalled successfully", application.getKey() );
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

    private Application installOrUpdateApplication( final ByteSource byteSource, final boolean global, final boolean updateRepository )
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

        if ( updateRepository && global )
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
                float totalRead = 0;
                int lastPct = 0;
                int currentPct;
                byte[] buffer = new byte[8192];
                os = new ByteArrayOutputStream();

                while ( ( bytesRead = is.read( buffer ) ) != -1 )
                {
                    os.write( buffer, 0, bytesRead );
                    totalRead += bytesRead;

                    currentPct = (int) ( ( totalRead / totalLength ) * 100 );

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
            throw new RuntimeException( "Failed to load application from " + url );
        }
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
            LOG.warn( "Failed to uninstall bundle", e );
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

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        this.registry.invalidate( key, level );
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addInvalidator( final ApplicationInvalidator invalidator )
    {
        this.registry.addInvalidator( invalidator );
    }

    public void removeInvalidator( final ApplicationInvalidator invalidator )
    {
        this.registry.removeInvalidator( invalidator );
    }
}
