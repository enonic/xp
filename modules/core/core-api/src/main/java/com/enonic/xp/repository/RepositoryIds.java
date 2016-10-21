package com.enonic.xp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
        final Collection<String> list = Lists.newArrayList( paths );
        return doParseIds( list );
    }

    private static ImmutableSet<RepositoryId> doParseIds( final Collection<String> list )
    {
        final Collection<RepositoryId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( RepositoryId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private final static class ParseFunction
        implements Function<String, RepositoryId>
    {
        @Override
        public RepositoryId apply( final String value )
        {
            return RepositoryId.from( value );
        }
    }

    public static class Builder
    {
        private List<RepositoryId> repositories = Lists.newArrayList();

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
