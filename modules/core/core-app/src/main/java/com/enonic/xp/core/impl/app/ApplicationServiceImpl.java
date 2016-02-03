package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.ApplicationInstallException;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
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
    private ApplicationRegistry registry;

    private BundleContext context;

    private ApplicationRepoService repoService;

    private EventPublisher eventPublisher;

    private final static Logger LOG = LoggerFactory.getLogger( ApplicationServiceImpl.class );

    @Activate
    public void activate( final BundleContext context )
    {
        this.registry = new ApplicationRegistry( context );
        this.context = context;
        this.installStoredApplications();
    }

    private void installStoredApplications()
    {
        ApplicationHelper.runAsAdmin( this::doInstallStoredApplications );
    }

    private void doInstallStoredApplications()
    {
        LOG.info( "Searching for installed applications" );

        final Nodes applications = repoService.getApplications();

        LOG.info( "Found [" + applications.getSize() + "] installed applications" );

        for ( final Node application : applications )
        {
            final Application installedApp;
            try
            {
                installedApp = doInstallApplication( application.id() );
                if ( getStartedState( application ) )
                {
                    doStartApplication( installedApp.getKey(), false );
                }

                LOG.info( "Application [{}] installed successfully", installedApp.getKey() );
            }
            catch ( Exception e )
            {
                LOG.error( "Cannot install application [{}]", application.name(), e );
            }
        }
    }

    @Override
    public Application getInstalledApplication( final ApplicationKey key )
        throws ApplicationNotFoundException
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            throw new ApplicationNotFoundException( key );
        }
        return application;
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
    public void startApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStartApplication( key, triggerEvent );
    }

    @Override
    public void stopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStopApplication( key, triggerEvent );
    }


    @Override
    public Application installApplication( final ByteSource byteSource )
    {
        final Application application = ApplicationHelper.callWithContext( () -> doInstallApplication( byteSource, true ) );
        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), true );

        return application;
    }

    @Override
    public Application installApplication( final NodeId nodeId )
    {
        final Application application = ApplicationHelper.callWithContext( () -> doInstallApplication( nodeId ) );
        LOG.info( "Application [{}] installed successfully", application.getKey() );

        doStartApplication( application.getKey(), false );

        return application;
    }

    @Override
    public void uninstallApplication( final ApplicationKey key )
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            LOG.warn( "Trying to uninstall bundle with key: [{}] but no such bundle installed", key );
            return;
        }

        doUninstallApplication( application, true );
    }

    private void doStartApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStartApplication( this.registry.get( key ) );

        if ( triggerEvent )
        {
            ApplicationHelper.callWithContext( () -> this.repoService.updateStartedState( key, true ) );
            this.eventPublisher.publish( ApplicationEvents.started( key ) );
        }

    }

    private void doStopApplication( final ApplicationKey key, final boolean triggerEvent )
    {
        doStopApplication( this.registry.get( key ) );

        if ( triggerEvent )
        {
            ApplicationHelper.callWithContext( () -> this.repoService.updateStartedState( key, false ) );
            this.eventPublisher.publish( ApplicationEvents.stopped( key ) );
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

    private Application doInstallApplication( final ByteSource byteSource, final boolean triggerEvent )
    {
        final Application application = installOrUpdateApplication( byteSource );

        if ( triggerEvent )
        {
            final Node node = this.repoService.getApplicationNode( application.getKey().getName() );
            this.eventPublisher.publish( ApplicationEvents.installed( node ) );
        }
        return application;
    }

    private Application doInstallApplication( final NodeId nodeId )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        return installOrUpdateApplication( byteSource );
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
            this.repoService.deleteApplicationNode( application );
            this.eventPublisher.publish( ApplicationEvents.uninstalled( application.getKey() ) );
        }
    }

    private Application installOrUpdateApplication( final ByteSource byteSource )
    {
        final String applicationName = getApplicationName( byteSource );

        final Application application;

        if ( applicationExists( applicationName ) )
        {
            application = doUpdateApplication( applicationName, byteSource );
            repoService.updateApplicationNode( application, byteSource );
        }
        else
        {
            application = doInstallApplication( byteSource, applicationName );
            repoService.createApplicationNode( application, byteSource );
        }

        return application;
    }

    private Boolean getStartedState( final Node node )
    {
        final PropertyTree data = node.data();
        return data.getBoolean( ApplicationPropertyNames.STARTED );
    }

    private boolean applicationExists( final String applicationName )
    {
        final Node existingNode = this.repoService.getApplicationNode( applicationName );

        return existingNode != null;
    }

    private Application doInstallApplication( final ByteSource byteSource, final String applicationName )
    {
        final Application existingApp = this.registry.get( ApplicationKey.from( applicationName ) );

        if ( existingApp != null )
        {
            LOG.info( "Application [" + applicationName + "] exists in registry but not in repo, uninstalling existing" );
            uninstallBundle( existingApp.getKey().getName() );
        }

        final Bundle bundle = doInstallBundle( byteSource, applicationName );

        return this.registry.get( ApplicationKey.from( bundle ) );
    }

    private Application doUpdateApplication( final String applicationName, final ByteSource source )
    {
        uninstallBundle( applicationName );

        this.registry.invalidate( ApplicationKey.from( applicationName ) );

        final Bundle bundle = doInstallBundle( source, applicationName );

        return this.registry.get( ApplicationKey.from( bundle ) );
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
