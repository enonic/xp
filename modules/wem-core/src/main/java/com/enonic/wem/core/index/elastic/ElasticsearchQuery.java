package com.enonic.wem.core.index.elastic;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class ElasticsearchQuery
{

    public static final int DEFAULT_SIZE = 10;

    private final QueryBuilder query;

    private final FilterBuilder filter;

    private final ImmutableSet<FacetBuilder> facetBuilders;

    private final IndexType indexType;

    private final Index index;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private final int from;

    private final int size;

    private final boolean explain;

    private ElasticsearchQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filter = builder.filter;
        this.facetBuilders = ImmutableSet.copyOf( builder.facetBuilders );
        this.indexType = builder.indexType;
        this.index = builder.index;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
        this.size = builder.size;
        this.from = builder.from;
        this.explain = builder.explain;
    }

    public QueryBuilder getQuery()
    {
        return query;
    }

    public FilterBuilder getFilter()
    {
        return filter;
    }

    public ImmutableSet<FacetBuilder> getFacetBuilders()
    {
        return facetBuilders;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }

    public Index getIndex()
    {
        return index;
    }

    public static Builder newQuery()
    {
        return new Builder();
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public boolean doExplain()
    {
        return explain;
    }

    public ImmutableSet<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public SearchSourceBuilder toSearchSourceBuilder()
    {
        SearchSourceBuilder builder = new SearchSourceBuilder().
            query( this.getQuery() ).
            filter( this.getFilter() ).
            from( this.getFrom() ).
            size( this.getSize() ).
            explain( this.doExplain() );

        if ( this.getFacetBuilders() != null && !this.getFacetBuilders().isEmpty() )
        {
            for ( final FacetBuilder facetBuilder : this.getFacetBuilders() )
            {
                builder.facet( facetBuilder );
            }
        }

        for ( final SortBuilder sortBuilder : this.getSortBuilders() )
        {
            builder.sort( sortBuilder );
        }

        return builder;
    }

    @Override
    public String toString()
    {
        String sortBuildersAsString = "";

        if ( sortBuilders != null && sortBuilders.size() > 0 )
        {
            Joiner joiner = Joiner.on( "," );
            sortBuildersAsString = joiner.join( getSortBuilders() );
        }

        return "ElasticsearchQuery{" +
            "query=" + query +
            ", filter=" + filter +
            ", facet=" + facetBuilders +
            ", indexType=" + indexType +
            ", index=" + index +
            ", sortBuilders=" + sortBuildersAsString +
            '}';
    }

    public static class Builder
    {
        private QueryBuilder query;

        private FilterBuilder filter;

        private Set<FacetBuilder> facetBuilders = Sets.newHashSet();

        private IndexType indexType;

        private Index index;

        private Set<SortBuilder> sortBuilders = Sets.newHashSet();

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private boolean explain = false;

        public Builder query( final QueryBuilder query )
        {
            this.query = query;
            return this;
        }

        public Builder filter( final FilterBuilder filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder addFacet( final FacetBuilder facetBuilder )
        {
            this.facetBuilders.add( facetBuilder );
            return this;
        }

        public Builder addFacets( final Collection<FacetBuilder> facetBuilders )
        {
            if ( facetBuilders != null )
            {
                this.facetBuilders.addAll( facetBuilders );
            }

            return this;
        }

        public Builder indexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder index( final Index index )
        {
            this.index = index;
            return this;
        }

        public Builder sortBuilders( final Set<SortBuilder> sortBuilders )
        {
            this.sortBuilders = sortBuilders;
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size( size );
            return this;
        }

        public Builder explain( final boolean explain )
        {
            this.explain = explain;
            return this;
        }


        public ElasticsearchQuery build()
        {
            return new ElasticsearchQuery( this );
        }

    }


}
