package com.enonic.xp.web.jetty.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.jetty.session.AbstractSessionDataStore;
import org.eclipse.jetty.session.SessionData;

/**
 * Minimal in-memory {@link org.eclipse.jetty.session.SessionDataStore} standing in for a distributed store
 * (e.g. Hazelcast) in tests: passivating, and - when paired with a
 * {@link org.eclipse.jetty.session.NullSessionCache} - the only place sessions survive between requests.
 */
public final class InMemorySessionDataStore
    extends AbstractSessionDataStore
{
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();

    @Override
    public boolean isPassivating()
    {
        return true;
    }

    @Override
    public boolean doExists( final String id )
    {
        return this.sessions.containsKey( id );
    }

    @Override
    public void doStore( final String id, final SessionData data, final long lastSaveTime )
    {
        this.sessions.put( id, data );
    }

    @Override
    public SessionData doLoad( final String id )
    {
        return this.sessions.get( id );
    }

    @Override
    public boolean delete( final String id )
    {
        return this.sessions.remove( id ) != null;
    }

    @Override
    public Set<String> doCheckExpired( final Set<String> candidates, final long time )
    {
        return candidates.stream().filter( id -> {
            final SessionData data = this.sessions.get( id );
            return data == null || data.isExpiredAt( time );
        } ).collect( Collectors.toSet() );
    }

    @Override
    public Set<String> doGetExpired( final long before )
    {
        return this.sessions.entrySet()
            .stream()
            .filter( entry -> entry.getValue().isExpiredAt( before ) )
            .map( Map.Entry::getKey )
            .collect( Collectors.toSet() );
    }

    @Override
    public void doCleanOrphans( final long time )
    {
    }
}
