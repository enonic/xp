package com.enonic.xp.repository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Repositories
    extends AbstractImmutableEntitySet<Repository>
{
    private Repositories( final Set<Repository> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public RepositoryIds getIds()
    {
        return RepositoryIds.from( set.stream().map( Repository::getId ).collect( ImmutableSet.toImmutableSet() ) );
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<Repository> repositorys = new LinkedHashSet<>();

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
