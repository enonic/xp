package com.enonic.xp.security;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

@Beta
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

    public static IdProviderKeys from( final String... IdProviderKeys )
    {
        return new IdProviderKeys( parseIdProviderKeys( IdProviderKeys ) );
    }

    public static IdProviderKeys empty()
    {
        return new IdProviderKeys( ImmutableList.<IdProviderKey>of() );
    }

    private static ImmutableList<IdProviderKey> parseIdProviderKeys( final String... idProviderKeys )
    {
        final List<IdProviderKey> idProviderKeyList = Stream.of( idProviderKeys ).map( IdProviderKey::new ).collect( toList() );
        return ImmutableList.copyOf( idProviderKeyList );
    }
}
