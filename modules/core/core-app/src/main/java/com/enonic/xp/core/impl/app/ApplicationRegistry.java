package com.enonic.xp.core.impl.app;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.server.RunMode;

final class ApplicationRegistry
{
    private final ConcurrentMap<ApplicationKey, Application> applications;

    private final BundleContext context;

    private final ApplicationFactory factory;

    private final List<ApplicationInvalidator> invalidators;

    private final ConfigurationAdmin configurationAdmin;

    public ApplicationRegistry( final BundleContext context, final ConfigurationAdmin configurationAdmin )
    {
        this.context = context;
        this.applications = Maps.newConcurrentMap();
        this.factory = new ApplicationFactory( RunMode.get(), this::loadConfig );
        this.invalidators = Lists.newCopyOnWriteArrayList();
        this.configurationAdmin = configurationAdmin;
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

        for ( final ApplicationInvalidator invalidator : this.invalidators )
        {
            invalidator.invalidate( key );
        }
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
        return bundle != null ? this.factory.create( bundle ) : null;
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

    public void addInvalidator( final ApplicationInvalidator invalidator )
    {
        this.invalidators.add( invalidator );
    }

    public void removeInvalidator( final ApplicationInvalidator invalidator )
    {
        this.invalidators.remove( invalidator );
    }

    private Map<String, String> loadConfig( final String pid )
    {
        final Map<String, String> result = Maps.newHashMap();
        final Configuration config = loadConfigObject( pid );
        if ( config == null )
        {
            return result;
        }

        final Dictionary<String, Object> props = config.getProperties();
        if ( props == null )
        {
            return result;
        }

        final Iterator<String> keysIter = Iterators.forEnumeration( props.keys() );
        keysIter.forEachRemaining( key -> result.put( key, props.get( key ).toString() ) );
        return result;
    }

    private Configuration loadConfigObject( final String pid )
    {
        try
        {
            return null; // this.configurationAdmin.getConfiguration( pid );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }
}
