package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private ApplicationRegistry registry;

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
    public Applications getApplications( final ApplicationKeys keys )
    {
        final ImmutableList.Builder<Application> applicationList = ImmutableList.builder();
        for ( final ApplicationKey key : keys )
        {
            final Application application = this.registry.get( key );
            if ( application != null )
            {
                applicationList.add( application );
            }
        }
        return Applications.from( applicationList.build() );
    }

    @Override
    public Applications getAllApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public ClassLoader getClassLoader( final Application application )
    {
        return new BundleClassLoader( application.getBundle() );
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

    @Reference
    public void setRegistry( final ApplicationRegistry registry )
    {
        this.registry = registry;
    }
}
