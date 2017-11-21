package com.enonic.xp.query.filter;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class IndicesFilter
    extends Filter
{

    final ImmutableSet<String> indices;

    final Filter filter;

    final Filter noMatchFilter;

    private IndicesFilter( final Builder builder )
    {
        super( builder );
        indices = ImmutableSet.copyOf( builder.indices );
        filter = builder.filter;
        noMatchFilter = builder.noMatchFilter;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String[] getIndices()
    {
        return indices.toArray( new String[this.indices.size()] );
    }

    public Filter getFilter()
    {
        return filter;
    }

    public Filter getNoMatchFilter()
    {
        return noMatchFilter;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "indices", indices ).
            add( "filter", filter ).
            add( "noMatchFilter", noMatchFilter ).
            toString();
    }

    public static final class Builder
        extends Filter.Builder<Builder>
    {
        private final Set<String> indices = Sets.newHashSet();

        private Filter filter;

        private Filter noMatchFilter;

        private Builder()
        {
        }

        public Builder addIndex( final String index )
        {
            indices.add( index );
            return this;
        }

        public Builder addIndices( final String... indices )
        {
            this.indices.addAll( Arrays.asList( indices ) );
            return this;
        }

        public Builder filter( final Filter val )
        {
            filter = val;
            return this;
        }

        public Builder noMatchFilter( final Filter val )
        {
            noMatchFilter = val;
            return this;
        }

        public IndicesFilter build()
        {
            return new IndicesFilter( this );
        }
    }
}
