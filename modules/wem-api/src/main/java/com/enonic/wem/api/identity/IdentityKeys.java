package com.enonic.wem.api.identity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

import static java.util.stream.Collectors.toList;

public final class IdentityKeys
    extends AbstractImmutableEntityList<IdentityKey>
{
    private IdentityKeys( final ImmutableList<IdentityKey> list )
    {
        super( list );
    }

    public static IdentityKeys from( final IdentityKey... identityKeys )
    {
        return new IdentityKeys( ImmutableList.copyOf( identityKeys ) );
    }

    public static IdentityKeys from( final Iterable<? extends IdentityKey> identityKeys )
    {
        return new IdentityKeys( ImmutableList.copyOf( identityKeys ) );
    }

    public static IdentityKeys from( final Collection<? extends IdentityKey> identityKeys )
    {
        return new IdentityKeys( ImmutableList.copyOf( identityKeys ) );
    }

    public static IdentityKeys from( final String... identityKeys )
    {
        return new IdentityKeys( parseIdentityKeys( identityKeys ) );
    }

    public static IdentityKeys empty()
    {
        return new IdentityKeys( ImmutableList.<IdentityKey>of() );
    }

    private static ImmutableList<IdentityKey> parseIdentityKeys( final String... identityKeys )
    {
        final List<IdentityKey> identityKeyList = Stream.of( identityKeys ).map( IdentityKey::from ).collect( toList() );
        return ImmutableList.copyOf( identityKeyList );
    }
}
