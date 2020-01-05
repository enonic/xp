package com.enonic.xp.web.session.impl.ignite;

import java.util.Set;
import java.util.stream.Collectors;

import javax.cache.Cache;

import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.UnreadableSessionDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session data stored in Ignite
 */
public class IgniteSessionDataStore
    extends AbstractSessionDataStore
{
    private static final Logger LOG = LoggerFactory.getLogger( IgniteSessionDataStore.class );

    private final Cache<String, IgniteSessionData> igniteCache;

    private String contextString;

    private String currentNode;

    public IgniteSessionDataStore( final Cache<String, IgniteSessionData> igniteCache )
    {
        this.igniteCache = igniteCache;
    }

    @Override
    public void initialize( final SessionContext context )
        throws Exception
    {
        super.initialize( context );
        contextString = context.getCanonicalContextPath() + "_" + context.getVhost();
        currentNode = context.getWorkerName();
    }

    @Override
    public SessionData doLoad( String id )
        throws UnreadableSessionDataException
    {
        LOG.trace( "Loading session {} from Ignite", id );

        final IgniteSessionData igniteSessionData = igniteCache.get( getCacheKey( id ) );
        if ( igniteSessionData == null )
        {
            return null;
        }
        try
        {
            return igniteSessionData.toSessionData();
        }
        catch ( Exception e )
        {
            throw new UnreadableSessionDataException( id, _context, e );
        }
    }

    @Override
    public boolean delete( String id )
    {
        LOG.trace( "Deleting session {} from Ignite", id );

        return igniteCache.remove( getCacheKey( id ) );
    }

    @Override
    public void doStore( String id, SessionData data, long lastSaveTime )
    {
        LOG.trace( "Store session {} in Ignite", id );

        igniteCache.put( getCacheKey( id ), new IgniteSessionData( data ) );
    }

    @Override
    public boolean isPassivating()
    {
        return true;
    }

    @Override
    public Set<String> doGetExpired( Set<String> candidates )
    {
        long now = System.currentTimeMillis();
        return candidates.stream().filter( c -> checkCandidate( c, now ) ).collect( Collectors.toSet() );
    }

    private boolean checkCandidate( final String candidate, long now )
    {
        LOG.trace( "Checking expiry for candidate {}", candidate );

        final SessionData data;
        try
        {
            data = load( candidate );
        }
        catch ( Exception e )
        {
            LOG.warn( "Session {} load threw Exception. Expire it.", candidate );
            return true;
        }

        if ( data == null )
        {
            LOG.debug( "Session {} does not exist.", candidate );
            return true;
        }
        else
        {
            final long expiry = data.getExpiry();

            LOG.debug( "Session expiry for candidate {} is {}", candidate, expiry );

            if ( expiry <= 0 )
            {
                return false; //never expires
            }

            final String managerNode = data.getLastNode();

            final boolean expired;
            if ( currentNode.equals( managerNode ) )
            {
                expired = checkExpiredOnSessionManager( now, expiry );
                if ( expired )
                {
                    LOG.debug( "Session {} managed by {} is expired", candidate, managerNode );
                }
            }
            else
            {
                expired = checkOnSessionAssistant( now, expiry );
                if ( expired )
                {
                    LOG.debug( "Session {} managed by {} is expired by assistant {}", candidate, managerNode, currentNode );
                }
            }
            return expired;
        }
    }

    private boolean checkExpiredOnSessionManager( final long now, final long expiry )
    {
        return expiry < now;
    }

    private boolean checkOnSessionAssistant( final long now, final long expiry )
    {
        final boolean isFirstExpiryCheck = _lastExpiryCheckTime == 0;
        final int gracePeriodMultiplier = isFirstExpiryCheck ? 3 : 1;
        final long effectiveGracePeriodMillis = gracePeriodMultiplier * _gracePeriodSec * 1000L;
        return expiry < now - effectiveGracePeriodMillis;
    }

    @Override
    public boolean exists( String id )
        throws Exception
    {
        final SessionData sd = load( id );
        if ( sd == null )
        {
            return false;
        }

        long expiry = sd.getExpiry();
        if ( expiry <= 0 )
        {
            return true; //never expires
        }

        return expiry > System.currentTimeMillis(); //not expired yet
    }

    private String getCacheKey( String id )
    {
        return contextString + "_" + id;
    }
}
