package com.enonic.xp.security;

import java.util.Collection;
import java.util.stream.Collector;
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
    private static final PrincipalKeys EMPTY = new PrincipalKeys( ImmutableSet.of() );

    private PrincipalKeys( final ImmutableSet<PrincipalKey> list )
    {
        super( list );
    }

    public static PrincipalKeys empty()
    {
        return EMPTY;
    }

    public static PrincipalKeys from( final PrincipalKey... principalKeys )
    {
        return fromInternal( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final Collection<PrincipalKey> principalKeys )
    {
        return fromInternal( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final String... principalKeys )
    {
        return Stream.of( principalKeys ).map( PrincipalKey::from ).collect( collecting() );
    }

    public static Collector<PrincipalKey, ?, PrincipalKeys> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), PrincipalKeys::fromInternal );
    }

    public static PrincipalKeys fromInternal( final ImmutableSet<PrincipalKey> set )
    {
        return set.isEmpty() ? EMPTY : new PrincipalKeys( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<PrincipalKey> principalKeys = new ImmutableSet.Builder<>();

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
            return fromInternal( principalKeys.build() );
        }
    }
}
