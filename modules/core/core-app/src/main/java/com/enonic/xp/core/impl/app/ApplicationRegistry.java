package com.enonic.xp.core.impl.app;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.BundleApplicationUrlResolver;

final class ApplicationRegistry
{
    private final ConcurrentMap<ApplicationKey, Application> applications;

    private final BundleContext context;

    public ApplicationRegistry( final BundleContext context )
    {
        this.context = context;
        this.applications = Maps.newConcurrentMap();
    }

    public ApplicationKeys getKeys()
    {
        return findApplicationKeys();
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

    public void invalidate( final ApplicationKey key )
    {
        this.applications.remove( key );
    }

    public Application get( final ApplicationKey key )
    {
        return this.applications.computeIfAbsent( key, this::createApp );
    }

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
        return bundle != null ? createApp( bundle ) : null;
    }

    private Application createApp( final Bundle bundle )
    {
        final ApplicationUrlResolver urlResolver = createUrlResolver( bundle );
        return new ApplicationImpl( bundle, urlResolver );
    }

    private boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationHelper.isApplication( bundle );
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

    private ApplicationUrlResolver createUrlResolver( final Bundle bundle )
    {
        return new BundleApplicationUrlResolver( bundle );
    }
}
