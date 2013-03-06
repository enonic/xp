package com.enonic.wem.core.index.elastic.searchsource;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.common.base.Strings;

import com.enonic.wem.api.content.query.ContentIndexQuery;

public class SearchSourceFactory
{
    public static SearchSourceBuilder create( final ContentIndexQuery contentIndexQuery )
    {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        final String fullTextQuery = contentIndexQuery.getFullTextSearchString();

        if ( Strings.isNullOrEmpty( fullTextQuery ) )
        {
            searchSourceBuilder.query( QueryBuilders.matchAllQuery() );
        }
        else
        {
            final QueryStringQueryBuilder fulltextQuery = QueryBuilders.queryString( fullTextQuery );
            fulltextQuery.lenient( true );

            searchSourceBuilder.query( fulltextQuery );
        }

        return searchSourceBuilder;
    }

}
