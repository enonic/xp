package com.enonic.xp.core.impl.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.server.RunMode;

final class ApplicationRegistry
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplicationRegistry.class );

    private final ConcurrentMap<ApplicationKey, Application> applications = new ConcurrentHashMap<>();

    private BundleContext context;

    private final ApplicationFactory factory = new ApplicationFactory( RunMode.get() );

    private final List<ApplicationInvalidator> invalidators = new CopyOnWriteArrayList<>();

    public void activate( final BundleContext context )
    {
        this.context = context;
    }

    public ApplicationKeys getKeys()
    {
        return findApplicationKeys();
    }

    private ApplicationKeys findApplicationKeys()
    {
        final List<ApplicationKey> list = new ArrayList<>();
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
        invalidate( key, ApplicationInvalidationLevel.FULL );
    }

    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        this.applications.remove( key );

        for ( final ApplicationInvalidator invalidator : this.invalidators )
        {
            try
            {
                invalidator.invalidate( key, level );
            }
            catch ( Exception e )
            {
                LOG.error( "Error invalidating application [" + invalidator.getClass().getSimpleName() + "]", e );
            }
        }
    }

    public Application get( final ApplicationKey key )
    {
        return this.applications.computeIfAbsent( key, this::createApp );
    }

    public Collection<Application> getAll()
    {
        final List<Application> list = new ArrayList<>();

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
            final String symbolicName = bundle.getSymbolicName();
            if ( symbolicName != null && symbolicName.equals( name ) )
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
}
