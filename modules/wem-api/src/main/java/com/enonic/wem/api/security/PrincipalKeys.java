package com.enonic.wem.api.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

public final class PrincipalKeys
    extends AbstractImmutableEntityList<PrincipalKey>
{
    private PrincipalKeys( final ImmutableList<PrincipalKey> list )
    {
        super( list );
    }

    public static PrincipalKeys from( final PrincipalKey... principalKeys )
    {
        return new PrincipalKeys( ImmutableList.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Iterable<? extends PrincipalKey> principalKeys )
    {
        return new PrincipalKeys( ImmutableList.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Collection<? extends PrincipalKey> principalKeys )
    {
        return new PrincipalKeys( ImmutableList.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final String... principalKeys )
    {
        return new PrincipalKeys( parsePrincipalKeys( principalKeys ) );
    }

    public static PrincipalKeys empty()
    {
        return new PrincipalKeys( ImmutableList.<PrincipalKey>of() );
    }

    private static ImmutableList<PrincipalKey> parsePrincipalKeys( final String... principalKeys )
    {
        final List<PrincipalKey> principalKeyList = Stream.of( principalKeys ).map( PrincipalKey::from ).collect( toList() );
        return ImmutableList.copyOf( principalKeyList );
    }
}
