package com.enonic.xp.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static PrincipalKeys from( final Iterable<? extends PrincipalKey> principalKeys )
    {
        return principalKeys instanceof PrincipalKeys p ? p : fromInternal( ImmutableSet.copyOf( principalKeys ) );
    }

    public static PrincipalKeys from( final String... principalKeys )
    {
        return Arrays.stream( principalKeys ).map( PrincipalKey::from ).collect( collector() );
    }

    public static Collector<PrincipalKey, ?, PrincipalKeys> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), PrincipalKeys::fromInternal );
    }

    private static PrincipalKeys fromInternal( final ImmutableSet<PrincipalKey> set )
    {
        return set.isEmpty() ? EMPTY : new PrincipalKeys( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<PrincipalKey> principalKeys = new ImmutableSet.Builder<>();

        public Builder add( final PrincipalKey principalKey )
        {
            this.principalKeys.add( principalKey );
            return this;
        }

        public Builder addAll( final Iterable<? extends PrincipalKey> principalKeys )
        {
            this.principalKeys.addAll( principalKeys );
            return this;
        }

        public PrincipalKeys build()
        {
            return fromInternal( principalKeys.build() );
        }
    }
}
