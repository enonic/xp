package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;

public class ESSource
{
    private final Set<String> indexNames;

    private final Set<String> indexTypes;

    private final Filters filters;

    private ESSource( final Builder builder )
    {
        indexNames = builder.indexNames;
        indexTypes = builder.indexTypes;
        filters = Filters.create().
            addAll( builder.filters ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<String> getIndexNames()
    {
        return this.indexNames;
    }

    public Set<String> getIndexTypes()
    {
        return this.indexTypes;
    }

    public Filters getFilters()
    {
        return this.filters;
    }

    public static final class Builder
    {
        private Set<String> indexNames = new HashSet<>();

        private Set<String> indexTypes = new HashSet<>();

        private final Set<Filter> filters = new HashSet<>();

        private Builder()
        {
        }

        public Builder indexNames( final Set<String> val )
        {
            indexNames = val;
            return this;
        }

        Builder addIndexName( final String val )
        {
            this.indexNames.add( val );
            return this;
        }

        Builder indexTypes( final Set<String> val )
        {
            indexTypes = val;
            return this;
        }

        Builder addIndexType( final String val )
        {
            this.indexTypes.add( val );
            return this;
        }

        Builder addFilter( final Filter val )
        {
            if ( val != null )
            {
                filters.add( val );
            }

            return this;
        }

        public ESSource build()
        {
            return new ESSource( this );
        }
    }
}
