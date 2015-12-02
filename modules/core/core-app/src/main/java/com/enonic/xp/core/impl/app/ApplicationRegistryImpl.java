package com.enonic.xp.core.impl.app;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@Component(immediate = true)
public final class ApplicationRegistryImpl
    implements ApplicationRegistry, ApplicationInvalidator
{
    private final ConcurrentMap<ApplicationKey, Application> applications;

    private BundleContext context;

    public ApplicationRegistryImpl()
    {
        this.applications = Maps.newConcurrentMap();
    }

    private ApplicationKeys findApplicationKeys()
    {
        final List<ApplicationKey> list = Lists.newArrayList();
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( isApplication( bundle ) )
            {
                list.add( ApplicationKey.from( bundle ) );
            }
        }

        return ApplicationKeys.from( list );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.applications.remove( key );
    }

    @Activate
    public void start( final BundleContext context )
    {
        this.context = context;
        this.applications.clear();
    }

    @Override
    public Application get( final ApplicationKey key )
    {
        return this.applications.computeIfAbsent( key, this::createApp );
    }

    @Override
    public Collection<Application> getAll()
    {
        final List<Application> list = Lists.newArrayList();

        for ( final ApplicationKey key : findApplicationKeys() )
        {
            final Application app = get( key );
            if ( app != null )
            {
                list.add( app );
            }
        }

        return list;
    }

    private Application createApp( final ApplicationKey key )
    {
        final Bundle bundle = findBundle( key.getName() );
        return bundle != null ? new ApplicationImpl( bundle ) : null;
    }

    private boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationImpl.isApplication( bundle );
    }

    private Bundle findBundle( final String name )
    {
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( bundle.getSymbolicName().equals( name ) )
            {
                return bundle;
            }
        }

        return null;
    }
}
