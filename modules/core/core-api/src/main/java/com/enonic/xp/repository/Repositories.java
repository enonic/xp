package com.enonic.xp.repository;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Repositories
    extends AbstractImmutableEntitySet<Repository>
{
    private static final Repositories EMPTY = new Repositories( ImmutableSet.of() );

    private Repositories( final ImmutableSet<Repository> set )
    {
        super( set );
    }

    public RepositoryIds getIds()
    {
        return set.stream().map( Repository::getId ).collect( RepositoryIds.collector() );
    }

    public static Repositories empty()
    {
        return EMPTY;
    }

    public static Repositories from( final Repository... repositories )
    {
        return new Repositories( ImmutableSet.copyOf( repositories ) );
    }

    public static Repositories from( final Iterable<? extends Repository> repositories )
    {
        return repositories instanceof Repositories r ? r : fromInternal( ImmutableSet.copyOf( repositories ) );
    }

    public static Collector<Repository, ?, Repositories> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), Repositories::fromInternal );
    }

    private static Repositories fromInternal( final ImmutableSet<Repository> set )
    {
        return set.isEmpty() ? EMPTY : new Repositories( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Repository> repositories = ImmutableSet.builder();

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
