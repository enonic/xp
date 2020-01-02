package com.enonic.xp.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class RepositoryIds
    extends AbstractImmutableEntitySet<RepositoryId>
    implements Iterable<RepositoryId>
{
    private RepositoryIds( final ImmutableSet<RepositoryId> set )
    {
        super( set );
    }

    public static RepositoryIds empty()
    {
        final ImmutableSet<RepositoryId> set = ImmutableSet.of();
        return new RepositoryIds( set );
    }

    public static RepositoryIds from( final RepositoryId... ids )
    {
        return new RepositoryIds( ImmutableSet.copyOf( ids ) );
    }

    public static RepositoryIds from( final String... ids )
    {
        return new RepositoryIds( parseIds( ids ) );
    }

    public static RepositoryIds from( final Collection<String> ids )
    {
        return new RepositoryIds( doParseIds( ids ) );
    }

    public static RepositoryIds from( final Iterable<RepositoryId> ids )
    {
        return new RepositoryIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<RepositoryId> parseIds( final String... paths )
    {
        return doParseIds( Arrays.asList( paths ) );
    }

    private static ImmutableSet<RepositoryId> doParseIds( final Collection<String> list )
    {
        return list.stream().map( RepositoryId::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( RepositoryId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<RepositoryId> repositories = new ArrayList<>();

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
            return RepositoryIds.from( repositories );
        }
    }
}
