package com.enonic.xp.repository;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Repositories
    extends AbstractImmutableEntityList<Repository>
{
    private static final Repositories EMPTY = new Repositories( ImmutableList.of() );

    private Repositories( final ImmutableList<Repository> set )
    {
        super( set );
    }

    public RepositoryIds getIds()
    {
        return list.stream().map( Repository::getId ).collect( RepositoryIds.collector() );
    }

    public static Repositories empty()
    {
        return EMPTY;
    }

    public static Repositories from( final Repository... repositories )
    {
        return new Repositories( ImmutableList.copyOf( repositories ) );
    }

    public static Repositories from( final Iterable<? extends Repository> repositories )
    {
        return repositories instanceof Repositories r ? r : fromInternal( ImmutableList.copyOf( repositories ) );
    }

    public static Collector<Repository, ?, Repositories> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Repositories::fromInternal );
    }

    private static Repositories fromInternal( final ImmutableList<Repository> list )
    {
        return list.isEmpty() ? EMPTY : new Repositories( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Repository> repositories = ImmutableList.builder();

        public Builder add( Repository repository )
        {
            this.repositories.add( repository );
            return this;
        }

        public Builder addAll( Iterable<? extends Repository> repositories )
        {
            this.repositories.addAll( repositories );
            return this;
        }

        public Repositories build()
        {
            return fromInternal( repositories.build() );
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
