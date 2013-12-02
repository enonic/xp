package com.enonic.wem.core.index.elastic;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilder;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class ElasticsearchQuery
{
    private final QueryBuilder query;

    private final FilterBuilder filter;

    private final FacetBuilder facet;

    private final IndexType indexType;

    private final Index index;

    private ElasticsearchQuery( final Builder builder )
    {
        this.query = builder.query;
        this.filter = builder.filter;
        this.facet = builder.facet;
        this.indexType = builder.indexType;
        this.index = builder.index;
    }

    public QueryBuilder getQuery()
    {
        return query;
    }

    public FilterBuilder getFilter()
    {
        return filter;
    }

    public FacetBuilder getFacet()
    {
        return facet;
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

    public SearchSourceBuilder toSearchSourceBuilder()
    {
        final SearchSourceBuilder builder = SearchSourceBuilder.searchSource();
        builder.query( this.query );
        builder.facet( this.facet );
        builder.filter( this.filter );

        return builder;
    }


    @Override
    public String toString()
    {
        return "ElasticsearchQuery{" +
            "query=" + query +
            ", filter=" + filter +
            ", facet=" + facet +
            ", indexType=" + indexType +
            ", index=" + index +
            '}';
    }

    public static class Builder
    {
        private QueryBuilder query;

        private FilterBuilder filter;

        private FacetBuilder facet;

        private IndexType indexType;

        private Index index;

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

        public Builder facet( final FacetBuilder facet )
        {
            this.facet = facet;
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

        public ElasticsearchQuery build()
        {
            return new ElasticsearchQuery( this );
        }

    }


}
