package com.enonic.xp.web.session.impl;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.UnreadableSessionDataException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = SessionDataStore.class, configurationPid = "com.enonic.xp.web.session")
public final class IgniteSessionDataStore
    extends AbstractSessionDataStore
    implements SessionDataStore
{
    private final static Logger LOG = LoggerFactory.getLogger( IgniteSessionDataStore.class );

    public static final String WEB_SESSION_CACHE = "com.enonic.xp.webSessionCache";

    private IgniteCache<String, SessionData> cache;

    private Ignite ignite;

    public IgniteSessionDataStore()
    {
    }

    @Activate
    public void activate( final WebSessionConfig config )
        throws Exception
    {
        this.cache = this.ignite.getOrCreateCache( SessionCacheConfigFactory.create( WEB_SESSION_CACHE, config ) );
    }

    @Override
    public SessionData load( String id )
        throws Exception
    {

        final AtomicReference<SessionData> reference = new AtomicReference<>();
        final AtomicReference<Exception> exception = new AtomicReference<>();

        //ensure the load runs in the context classloader scope
        _context.run( () -> {
            try
            {
                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "Loading session {} from Ignite", id );
                }
                SessionData sd = cache.get( getCacheKey( id ) );

                reference.set( sd );
            }
            catch ( Exception e )
            {
                exception.set( new UnreadableSessionDataException( id, _context, e ) );
            }
        } );

        if ( exception.get() != null )
        {
            throw exception.get();
        }
        return reference.get();
    }

    @Override
    public boolean delete( String id )
        throws Exception
    {
        return cache != null && cache.remove( getCacheKey( id ) );
    }

    @Override
    public void initialize( SessionContext context )
        throws Exception
    {
        _context = context;
    }

    @Override
    public void doStore( String id, SessionData data, long lastSaveTime )
        throws Exception
    {
        this.cache.put( getCacheKey( id ), data );
    }

    @Override
    public boolean isPassivating()
    {
        return true;
    }

    @Override
    public Set<String> doGetExpired( Set<String> candidates )
    {
        if ( candidates == null || candidates.isEmpty() )
        {
            return Collections.emptySet();
        }

        long now = System.currentTimeMillis();
        return candidates.stream().filter( candidate -> {

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "Checking expiry for candidate {}", candidate );
            }

            try
            {
                SessionData sd = load( candidate );

                //if the session no longer exists
                if ( sd == null )
                {
                    if ( LOG.isTraceEnabled() )
                    {
                        LOG.trace( "Session {} does not exist in Ignite", candidate );
                    }
                    return true;
                }
                else
                {
                    if ( _context.getWorkerName().equals( sd.getLastNode() ) )
                    {
                        //we are its manager, add it to the expired set if it is expired now
                        if ( ( sd.getExpiry() > 0 ) && sd.getExpiry() <= now )
                        {
                            if ( LOG.isTraceEnabled() )
                            {
                                LOG.trace( "Session {} managed by {} is expired", candidate, _context.getWorkerName() );
                            }
                            return true;
                        }
                    }
                    else
                    {
                        //if we are not the session's manager, only expire it iff:
                        // this is our first expiryCheck and the session expired a long time ago
                        //or
                        //the session expired at least one graceperiod ago
                        if ( _lastExpiryCheckTime <= 0 )
                        {
                            if ( ( sd.getExpiry() > 0 ) && sd.getExpiry() < ( now - ( 1000L * ( 3 * _gracePeriodSec ) ) ) )
                            {
                                return true;
                            }
                        }
                        else
                        {
                            if ( ( sd.getExpiry() > 0 ) && sd.getExpiry() < ( now - ( 1000L * _gracePeriodSec ) ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            catch ( Exception e )
            {
                LOG.warn( "Error checking if candidate {} is expired so expire it", candidate, e );
                return true;
            }
            return false;
        } ).collect( Collectors.toSet() );
    }

    @Override
    public boolean exists( String id )
        throws Exception
    {
        SessionData sd = load( id );
        if ( sd == null )
        {
            return false;
        }

        if ( sd.getExpiry() <= 0 )
        {
            return true; //never expires
        }
        else
        {
            return sd.getExpiry() > System.currentTimeMillis(); //not expired yet
        }
    }

    public String getCacheKey( String id )
    {
        return _context.getCanonicalContextPath() + "_" + _context.getVhost() + "_" + id;
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }

}