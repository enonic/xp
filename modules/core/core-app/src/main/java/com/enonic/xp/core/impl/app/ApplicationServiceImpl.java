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

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
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
    }

    @Override
    public Application getApplication( final ApplicationKey key )
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
    public ApplicationKeys getApplicationKeys()
    {
        return this.registry.getKeys();
    }

    @Override
    public Applications getAllApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public void startApplication( final ApplicationKey key )
    {
        startApplication( getApplication( key ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key )
    {
        stopApplication( getApplication( key ) );
    }

    @Override
    public Application installApplication( final ByteSource byteSource )
    {
        return installOrUpdateApplication( byteSource );
    }

    @Override
    public Application installApplication( final NodeId nodeId )
    {
        final ByteSource byteSource = this.repoService.getApplicationSource( nodeId );

        return installOrUpdateApplication( byteSource );
    }

    private Application installOrUpdateApplication( final ByteSource byteSource )
    {
        final String applicationName = getApplicationName( byteSource );

        final Application application;
        final Node node;

        if ( applicationExists( applicationName ) )
        {
            application = doUpdateApplication( applicationName, byteSource );
            node = repoService.updateApplicationNode( application, byteSource );
            LOG.info( "Application [{}] updated successfully", application.getKey() );
        }
        else
        {
            application = doInstallApplication( byteSource, applicationName );
            node = repoService.createApplicationNode( application, byteSource );
            LOG.info( "Application [{}] installed successfully", application.getKey() );
        }

        this.eventPublisher.publish( ApplicationEvents.installed( node ) );

        return application;
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
            else
            {
                LOG.info( "Cannot uninstall application [" + applicationName + "], not installed" );
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

    private void startApplication( final Application application )
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

    private void stopApplication( final Application application )
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
