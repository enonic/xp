package com.enonic.xp.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class RepositoryIds
    extends AbstractImmutableEntitySet<RepositoryId>
{
    private static final RepositoryIds EMPTY = new RepositoryIds( ImmutableSet.of() );

    private RepositoryIds( final ImmutableSet<RepositoryId> set )
    {
        super( set );
    }

    public static RepositoryIds empty()
    {
        return EMPTY;
    }

    public static RepositoryIds from( final RepositoryId... ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static RepositoryIds from( final String... ids )
    {
        return from( Arrays.asList( ids ) );
    }

    public static RepositoryIds from( final Collection<String> ids )
    {
        return fromInternal( ids.stream().map( RepositoryId::from ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static RepositoryIds from( final Iterable<RepositoryId> ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static Collector<RepositoryId, ?, RepositoryIds> collecting()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), RepositoryIds::fromInternal );
    }

    private static RepositoryIds fromInternal( final ImmutableSet<RepositoryId> ids )
    {
        return ids.isEmpty() ? EMPTY : new RepositoryIds( ids );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<RepositoryId> repositories = ImmutableSet.builder();

        public Builder add( final RepositoryId repositoryId )
        {
            this.repositories.add( repositoryId );
            return this;
        }

        public Builder addAll( final RepositoryIds repositoryIds )
        {
            this.repositories.addAll( repositoryIds.getSet() );
            return this;
        }


        public RepositoryIds build()
        {
            return fromInternal( repositories.build() );
        }
    }
}
