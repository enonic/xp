package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private ModuleRegistry registry;

    @Override
    public Module getModule( final ApplicationKey key )
        throws ModuleNotFoundException
    {
        final Module module = this.registry.get( key );
        if ( module == null )
        {
            throw new ModuleNotFoundException( key );
        }
        return module;
    }

    @Override
    public Applications getModules( final ApplicationKeys keys )
    {
        final ImmutableList.Builder<Module> moduleList = ImmutableList.builder();
        for ( final ApplicationKey key : keys )
        {
            final Module module = this.registry.get( key );
            if ( module != null )
            {
                moduleList.add( module );
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
    public ClassLoader getClassLoader( final Module module )
    {
        return new BundleClassLoader( module.getBundle() );
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

    private void startModule( final Module module )
    {
        try
        {
            module.getBundle().start();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void stopModule( final Module module )
    {
        try
        {
            module.getBundle().stop();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Reference
    public void setRegistry( final ModuleRegistry registry )
    {
        this.registry = registry;
    }
}
