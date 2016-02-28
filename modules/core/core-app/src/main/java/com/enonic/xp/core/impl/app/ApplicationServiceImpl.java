package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;
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

    private final ConcurrentMap<String, Boolean> localApplicationSet = Maps.newConcurrentMap();

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
        return Boolean.TRUE.equals( localApplicationSet.get( key.getName() ) );
    }

    @Override
    public void startApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStartApplication( key, triggerEvent && !isLocalApplication( key ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStopApplication( key, triggerEvent && !isLocalApplication( key ) );
    }


    @Override
    public Application installGlobalApplication( final ByteSource byteSource )
    {
        final Application application = ApplicationHelper.callWithContext( () -> doInstallApplication( byteSource, true, true ) );

        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), true );

        return application;
    }

    @Override
    public Application installLocalApplication( final ByteSource byteSource )
    {
        final Application application = ApplicationHelper.callWithContext( () -> doInstallApplication( byteSource, false, false ) );

        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), false );

        return application;
    }

    @Override
    public Application installStoredApplication( final NodeId nodeId )
    {
        final Application application = ApplicationHelper.callWithContext( () -> doInstallApplication( nodeId, true, false ) );

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
                installedApp = doInstallApplication( applicationNode.id(), true, false );
                if ( getStartedState( applicationNode ) )
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
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            LOG.warn( "Trying to uninstall bundle with key: [{}] but no such bundle installed", key );
            return;
        }

        doUninstallApplication( application, triggerEvent );

        ifUninstalledLocalReplaceWithGlobal( key, application );
    }

    private void ifUninstalledLocalReplaceWithGlobal( final ApplicationKey key, final Application application )
    {
        final Boolean local = localApplicationSet.remove( key.getName() );

        if ( Boolean.TRUE.equals( local ) )
        {
            reinstallGlobalApplicationIfExists( key, application );
        }
    }

    private void reinstallGlobalApplicationIfExists( final ApplicationKey key, final Application application )
    {
        final Node applicationNode = this.repoService.getApplicationNode( key.getName() );

        if ( applicationNode != null )
        {
            doInstallApplication( applicationNode.id(), true, false );

            LOG.info( "Application [{}] installed successfully", application.getKey() );

            if ( Boolean.TRUE.equals( getStartedState( applicationNode ) ) )
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
            ApplicationHelper.callWithContext( () -> this.repoService.updateStartedState( key, true ) );
            this.eventPublisher.publish( ApplicationClusterEvents.started( key ) );
        }

    }

    private void doStopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStopApplication( this.registry.get( key ) );

        if ( triggerEvent )
        {
            ApplicationHelper.callWithContext( () -> this.repoService.updateStartedState( key, false ) );
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

    private Application doInstallApplication( final ByteSource byteSource, final boolean global, final boolean notifyCluster )
    {
        final Application application = installOrUpdateApplication( byteSource, global );

        if ( notifyCluster )
        {
            final Node node = this.repoService.getApplicationNode( application.getKey().getName() );
            this.eventPublisher.publish( ApplicationClusterEvents.installed( node ) );
        }
        return application;
    }

    private Application doInstallApplication( final NodeId nodeId, final boolean global, final boolean notifyCluster )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        if ( byteSource == null )
        {
            throw new ApplicationInstallException( "Cannot install application with id [" + nodeId + "], source not found" );
        }

        return installOrUpdateApplication( byteSource, global );
    }

    private void doUninstallApplication( final Application application, final boolean triggerEvent )
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

        if ( triggerEvent )
        {
            ApplicationHelper.callWithContext( () -> {
                this.repoService.deleteApplicationNode( application );
                return null;
            } );

            this.eventPublisher.publish( ApplicationClusterEvents.uninstalled( application.getKey() ) );
        }
    }

    private Application installOrUpdateApplication( final ByteSource byteSource, final boolean global )
    {
        final String applicationName = getApplicationName( byteSource );

        final boolean update = applicationBundleInstalled( applicationName );

        final Application application;

        if ( update )
        {
            application = doUpdateApplication( applicationName, byteSource, global );
        }
        else
        {
            application = doInstallApplication( byteSource, applicationName );
        }

        localApplicationSet.compute( applicationName, ( key, present ) -> !global );

        if ( global && alreadyInRepo( applicationName ) )
        {
            repoService.updateApplicationNode( application, byteSource );
        }
        else if ( global )
        {
            repoService.createApplicationNode( application, byteSource );
        }

        return application;
    }

    private boolean alreadyInRepo( final String applicationName )
    {
        return repoService.getApplicationNode( applicationName ) != null;
    }

    private Boolean getStartedState( final Node node )
    {
        final PropertyTree data = node.data();
        return data.getBoolean( ApplicationPropertyNames.STARTED );
    }

    private boolean applicationBundleInstalled( final String applicationName )
    {
        return this.localApplicationSet.containsKey( applicationName );

        // final Application existingApp = this.registry.get( ApplicationKey.from( applicationName ) );
        // return existingApp != null;
    }

    private Application doInstallApplication( final ByteSource byteSource, final String applicationName )
    {
        //final Application existingApp = this.registry.get( ApplicationKey.from( applicationName ) );

        //if ( existingApp != null )
        //{
        //    LOG.info( "Application [" + applicationName + "] exists in registry but not in repo, uninstalling existing" );
        //    uninstallBundle( existingApp.getKey().getName() );
        // }

        final Bundle bundle = doInstallBundle( byteSource, applicationName );

        return this.registry.get( ApplicationKey.from( bundle ) );
    }

    private Application doUpdateApplication( final String applicationName, final ByteSource source, final boolean global )
    {
        final Boolean installedLocally = localApplicationSet.get( applicationName );

        final boolean ignoreLocalUpdate = installedLocally && global;

        if ( ignoreLocalUpdate )
        {
            LOG.warn( "Application : '" + applicationName + "' installed locally, installed version will be overrided by local" );
            return this.registry.get( ApplicationKey.from( applicationName ) );
        }
        else
        {
            uninstallBundle( applicationName );

            this.registry.invalidate( ApplicationKey.from( applicationName ) );

            final Bundle bundle = doInstallBundle( source, applicationName );

            return this.registry.get( ApplicationKey.from( bundle ) );
        }
    }

    private void uninstallBundle( final String applicationName )
    {
        try
        {
            final Bundle bundle = this.context.getBundle( applicationName );

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

    private String getApplicationName( final ByteSource byteSource )
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
        return applicationName;
    }

    private Bundle doInstallBundle( final ByteSource source, final String symbolicName )
    {
        try (final InputStream in = source.openStream())
        {
            return this.context.installBundle( symbolicName, in );
        }
        catch ( BundleException e )
        {
            throw new ApplicationInstallException( "Could not install application bundle:   '" + symbolicName + "'", e );
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
