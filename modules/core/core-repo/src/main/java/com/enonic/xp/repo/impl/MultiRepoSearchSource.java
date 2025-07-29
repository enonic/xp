package com.enonic.xp.repo.impl;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryIds;

public class MultiRepoSearchSource
    implements SearchSource, Iterable<SingleRepoSearchSource>
{
    private final Set<SingleRepoSearchSource> sources;

    private MultiRepoSearchSource( final Builder builder )
    {
        sources = builder.sources.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<SingleRepoSearchSource> getSources()
    {
        return sources;
    }

    public RepositoryIds getRepositoryIds()
    {
        return sources.stream().map( SingleRepoSearchSource::getRepositoryId ).collect( RepositoryIds.collector() );
    }

    public Branches getAllBranches()
    {
        return sources.stream().map( SingleRepoSearchSource::getBranch ).collect( Branches.collector() );
    }

    @Override
    public Iterator<SingleRepoSearchSource> iterator()
    {
        return sources.iterator();
    }


    public static final class Builder
    {
        private final ImmutableSet.Builder<SingleRepoSearchSource> sources = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder add( final SingleRepoSearchSource source )
        {
            sources.add( source );
            return this;
        }

        public MultiRepoSearchSource build()
        {
            return new MultiRepoSearchSource( this );
        }
    }
}
