package com.enonic.wem.core.index.elastic;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilder;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class ElasticsearchQuery
{

    //TODO: Make builder if to be used
    private QueryBuilder query;

    private FilterBuilder filter;

    private FacetBuilder facet;

    private IndexType indexType;

    private Index index;


    public void setQuery( final QueryBuilder query )
    {
        this.query = query;
    }

    public void setFilter( final FilterBuilder filter )
    {
        this.filter = filter;
    }

    public void setFacet( final FacetBuilder facet )
    {
        this.facet = facet;
    }

    public void setIndexType( final IndexType indexType )
    {
        this.indexType = indexType;
    }

    public void setIndex( final Index index )
    {
        this.index = index;
    }

    public SearchSourceBuilder toSearchSourceBuilder()
    {
        final SearchSourceBuilder builder = SearchSourceBuilder.searchSource();
        builder.query( this.query );
        builder.facet( this.facet );
        builder.filter( this.filter );

        return builder;
    }

}
