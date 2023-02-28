package com.enonic.xp.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

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

    public static Builder create()
    {
        return new Builder();
    }

    private static ImmutableSet<PrincipalKey> parsePrincipalKeys( final String... principalKeys )
    {
        return Stream.of( principalKeys ).map( PrincipalKey::from ).collect( ImmutableSet.toImmutableSet() );
    }

    @Deprecated
    public Set<String> asStrings()
    {
        return this.set.stream().map( PrincipalKey::toString ).collect( Collectors.toSet() );
    }

    public static class Builder
    {
        private final ImmutableList.Builder<PrincipalKey> principalKeys = new ImmutableList.Builder<>();

        public Builder add( final PrincipalKey principalKey )
        {
            if ( principalKey != null )
            {
                this.principalKeys.add( principalKey );
            }
            return this;
        }

        public Builder addAll( final PrincipalKeys principalKeys )
        {
            if ( principalKeys != null )
            {
                this.principalKeys.addAll( principalKeys );
            }
            return this;
        }

        public PrincipalKeys build()
        {
            return PrincipalKeys.from( principalKeys.build() );
        }
    }
}
