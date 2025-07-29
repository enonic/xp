package com.enonic.xp.security;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class IdProviderKeys
    extends AbstractImmutableEntitySet<IdProviderKey>
{
    private static final IdProviderKeys EMPTY = new IdProviderKeys( ImmutableSet.of() );

    private IdProviderKeys( final ImmutableSet<IdProviderKey> list )
    {
        super( list );
    }

    public static IdProviderKeys empty()
    {
        return EMPTY;
    }

    public static IdProviderKeys from( final IdProviderKey... idProviderKeys )
    {
        return fromInternal( ImmutableSet.copyOf( idProviderKeys ) );
    }

    public static IdProviderKeys from( final Iterable<IdProviderKey> idProviderKeys )
    {
        return idProviderKeys instanceof IdProviderKeys i ? i : fromInternal( ImmutableSet.copyOf( idProviderKeys ) );
    }

    public static IdProviderKeys from( final String... idProviderKeys )
    {
        return Arrays.stream( idProviderKeys ).map( IdProviderKey::new ).collect( collector() );
    }

    public static Collector<IdProviderKey, ?, IdProviderKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), IdProviderKeys::fromInternal );
    }

    private static IdProviderKeys fromInternal( final ImmutableSet<IdProviderKey> idProviderKeys )
    {
        return idProviderKeys.isEmpty() ? EMPTY : new IdProviderKeys( idProviderKeys );
    }
}
