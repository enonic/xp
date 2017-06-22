package com.enonic.xp.repo.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryIds;

public class MultiRepoSearchSource
    implements SearchSource, Iterable<SingleRepoSearchSource>
{
    private Set<SingleRepoSearchSource> sources;

    private MultiRepoSearchSource( final Builder builder )
    {
        sources = builder.sources;
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
        return RepositoryIds.from( sources.stream().map( SingleRepoSearchSource::getRepositoryId ).collect( Collectors.toSet() ) );
    }

    public Branches getAllBranches()
    {
        return Branches.from( sources.stream().map( SingleRepoSearchSource::getBranch ).collect( Collectors.toSet() ) );
    }

    @Override
    public Iterator<SingleRepoSearchSource> iterator()
    {
        return sources.iterator();
    }


    public static final class Builder
    {
        private Set<SingleRepoSearchSource> sources = Sets.newHashSet();

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
