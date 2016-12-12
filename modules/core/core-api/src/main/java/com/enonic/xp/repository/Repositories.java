package com.enonic.xp.repository;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class Repositories
    extends AbstractImmutableEntitySet<Repository>
{
    private final ImmutableMap<RepositoryId, Repository> map;

    private Repositories( final Set<Repository> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToIdFunction() );
    }

    public RepositoryIds getIds()
    {
        final Collection<RepositoryId> ids = Collections2.transform( this.set, new ToIdFunction() );
        return RepositoryIds.from( ids );
    }

    public Repository getRepositoryById( final RepositoryId repositoryId )
    {
        return this.map.get( repositoryId );
    }

    public static Repositories empty()
    {
        final ImmutableSet<Repository> set = ImmutableSet.of();
        return new Repositories( set );
    }

    public static Repositories from( final Repository... repositorys )
    {
        return new Repositories( ImmutableSet.copyOf( repositorys ) );
    }

    public static Repositories from( final Iterable<? extends Repository> repositorys )
    {
        return new Repositories( ImmutableSet.copyOf( repositorys ) );
    }

    public static Repositories from( final Collection<? extends Repository> repositorys )
    {
        return new Repositories( ImmutableSet.copyOf( repositorys ) );
    }

    private final static class ToIdFunction
        implements Function<Repository, RepositoryId>
    {
        @Override
        public RepositoryId apply( final Repository value )
        {
            return value.getId();
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Repository> repositorys = Sets.newLinkedHashSet();

        public Builder add( Repository repository )
        {
            this.repositorys.add( repository );
            return this;
        }

        public Builder addAll( Repositories repositorys )
        {
            this.repositorys.addAll( repositorys.getSet() );
            return this;
        }


        public Repositories build()
        {
            return new Repositories( repositorys );
        }
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        for ( final Repository repository : this )
        {
            s.add( "repository", repository.toString() );
        }

        return s.toString();
    }
}
