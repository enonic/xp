package com.enonic.wem.core.index.elastic.searchsource;

import java.util.Collection;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import com.enonic.wem.api.content.query.ContentIndexQuery;
import com.enonic.wem.core.index.content.ContentIndexField;

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

        if ( contentIndexQuery.getContentTypeNames() != null && contentIndexQuery.getContentTypeNames().isNotEmpty() )
        {
            final Collection<String> contentTypeNames =
                getCollectionAsLowercase( contentIndexQuery.getContentTypeNames().getAsStringSet() );

            FilterBuilder filterBuilder = new TermsFilterBuilder( ContentIndexField.CONTENT_TYPE, contentTypeNames );

            searchSourceBuilder.filter( filterBuilder );
        }

        if ( contentIndexQuery.isIncludeFacets() )
        {
            final String facetsDefinition = contentIndexQuery.getFacets();

            if ( !Strings.isNullOrEmpty( facetsDefinition ) )
            {
                searchSourceBuilder.facets( facetsDefinition.getBytes() );
            }
        }

        return searchSourceBuilder;
    }

    private static Collection<String> getCollectionAsLowercase( final Collection<String> collection )
    {
        Collection<String> lowerCaseStrings = Collections2.transform( collection, new Function<String, String>()
        {
            public String apply( String str )
            {
                return str.toLowerCase();
            }
        } );

        return lowerCaseStrings;
    }

}
