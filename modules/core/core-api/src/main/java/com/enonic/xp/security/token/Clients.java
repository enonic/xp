package com.enonic.xp.security.token;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.support.AbstractImmutableEntityList;

/**
 * The clients registered with an id provider.
 */
@NullMarked
public final class Clients
    extends AbstractImmutableEntityList<Client>
{
    private static final Clients EMPTY = new Clients( ImmutableList.of() );

    private Clients( final ImmutableList<Client> list )
    {
        super( list );
    }

    /**
     * No clients.
     */
    public static Clients empty()
    {
        return EMPTY;
    }

    /**
     * The clients given, in that order.
     */
    public static Clients from( final Client... clients )
    {
        return fromInternal( ImmutableList.copyOf( clients ) );
    }

    /**
     * The clients given, in that order.
     */
    public static Clients from( final Iterable<Client> clients )
    {
        return clients instanceof Clients g ? g : fromInternal( ImmutableList.copyOf( clients ) );
    }

    /**
     * Collects a stream of clients.
     */
    public static Collector<Client, ?, Clients> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Clients::fromInternal );
    }

    private static Clients fromInternal( final ImmutableList<Client> list )
    {
        return list.isEmpty() ? EMPTY : new Clients( list );
    }
}
