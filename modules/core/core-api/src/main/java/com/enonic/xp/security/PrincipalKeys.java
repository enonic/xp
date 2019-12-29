package com.enonic.xp.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

import static java.util.stream.Collectors.toSet;

@PublicApi
public final class PrincipalKeys
    extends AbstractImmutableEntitySet<PrincipalKey>
{
    private PrincipalKeys( final ImmutableSet<PrincipalKey> list )
    {
        super( list );
    }

    public static PrincipalKeys from( final PrincipalKey... principalKeys )
    {
        return new PrincipalKeys( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Collection<PrincipalKey> principalKeys )
    {
        return new PrincipalKeys( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final String... principalKeys )
    {
        return new PrincipalKeys( parsePrincipalKeys( principalKeys ) );
    }

    public static PrincipalKeys from( final Iterable<PrincipalKey>... principalKeys )
    {
        final ImmutableSet.Builder<PrincipalKey> keys = ImmutableSet.builder();
        for ( Iterable<PrincipalKey> keysParam : principalKeys )
        {
            keys.addAll( keysParam );
        }
        return new PrincipalKeys( keys.build() );
    }

    public static PrincipalKeys empty()
    {
        return new PrincipalKeys( ImmutableSet.of() );
    }

    private static ImmutableSet<PrincipalKey> parsePrincipalKeys( final String... principalKeys )
    {
        final Set<PrincipalKey> principalKeyList = Stream.of( principalKeys ).map( PrincipalKey::from ).collect( toSet() );
        return ImmutableSet.copyOf( principalKeyList );
    }
}
