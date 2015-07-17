package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private ApplicationRegistry registry;

    @Override
    public Application getModule( final ApplicationKey key )
        throws ModuleNotFoundException
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            throw new ModuleNotFoundException( key );
        }
        return application;
    }

    @Override
    public Applications getModules( final ApplicationKeys keys )
    {
        final ImmutableList.Builder<Application> moduleList = ImmutableList.builder();
        for ( final ApplicationKey key : keys )
        {
            final Application application = this.registry.get( key );
            if ( application != null )
            {
                moduleList.add( application );
            }
        }
        return Applications.from( moduleList.build() );
    }

    @Override
    public Applications getAllModules()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public ClassLoader getClassLoader( final Application application )
    {
        return new BundleClassLoader( application.getBundle() );
    }

    @Override
    public void startModule( final ApplicationKey key )
    {
        startModule( getModule( key ) );

    }

    @Override
    public void stopModule( final ApplicationKey key )
    {
        stopModule( getModule( key ) );
    }

    private void startModule( final Application application )
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

    private void stopModule( final Application application )
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
