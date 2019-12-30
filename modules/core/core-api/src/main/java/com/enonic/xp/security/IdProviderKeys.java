package com.enonic.xp.security;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

@PublicApi
public final class IdProviderKeys
    extends AbstractImmutableEntityList<IdProviderKey>
{
    private IdProviderKeys( final ImmutableList<IdProviderKey> list )
    {
        super( list );
    }

    public static IdProviderKeys from( final IdProviderKey... idProviderKeys )
    {
        return new IdProviderKeys( ImmutableList.copyOf( idProviderKeys ) );
    }

    public static IdProviderKeys from( final Iterable<? extends IdProviderKey> idProviderKeys )
    {
        return new IdProviderKeys( ImmutableList.copyOf( idProviderKeys ) );
    }

    public static IdProviderKeys from( final String... idProviderKeys )
    {
        return new IdProviderKeys( parseIdProviderKeys( idProviderKeys ) );
    }

    public static IdProviderKeys empty()
    {
        return new IdProviderKeys( ImmutableList.of() );
    }

    private static ImmutableList<IdProviderKey> parseIdProviderKeys( final String... idProviderKeys )
    {
        final List<IdProviderKey> idProviderKeyList = Stream.of( idProviderKeys ).map( IdProviderKey::new ).collect( toList() );
        return ImmutableList.copyOf( idProviderKeyList );
    }
}
