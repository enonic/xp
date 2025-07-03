package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.api.ApiDescriptors;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class RoutableNodeVersionIds
    extends AbstractImmutableEntitySet<RoutableNodeVersionId>
{
    private static final RoutableNodeVersionIds EMPTY = new RoutableNodeVersionIds( ImmutableSet.of() );

    private RoutableNodeVersionIds( final ImmutableSet<RoutableNodeVersionId> set )
    {
        super( set );
    }

    public static RoutableNodeVersionIds empty()
    {
        return new RoutableNodeVersionIds( ImmutableSet.of() );
    }

    public static RoutableNodeVersionIds from( final RoutableNodeVersionId... routableNodeVersionIds )
    {
        return fromInternal( ImmutableSet.copyOf( routableNodeVersionIds ) );
    }

    public static RoutableNodeVersionIds from( final Collection<RoutableNodeVersionId> routableNodeVersionIds )
    {
        return fromInternal( ImmutableSet.copyOf( routableNodeVersionIds ) );
    }

    public static Collector<RoutableNodeVersionId, ?, RoutableNodeVersionIds> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), RoutableNodeVersionIds::fromInternal );
    }

    private static RoutableNodeVersionIds fromInternal( final ImmutableSet<RoutableNodeVersionId> set )
    {
        return set.isEmpty() ? EMPTY : new RoutableNodeVersionIds( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<RoutableNodeVersionId> routableNodeVersionIds = new LinkedHashSet<>();

        public Builder add( final RoutableNodeVersionId routableNodeVersionId )
        {
            this.routableNodeVersionIds.add( routableNodeVersionId );
            return this;
        }

        public Builder addAll( final Collection<RoutableNodeVersionId> routableNodeVersionIds )
        {
            this.routableNodeVersionIds.addAll( routableNodeVersionIds );
            return this;
        }

        public RoutableNodeVersionIds build()
        {
            return new RoutableNodeVersionIds( ImmutableSet.copyOf( this.routableNodeVersionIds ) );
        }

    }

}
