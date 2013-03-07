package com.enonic.wem.core.index.elastic.searchsource;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.google.common.base.Strings;

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

        if ( contentIndexQuery.isIncludeFacets() )
        {
            final TermsFacetBuilder spaceFacet = FacetBuilders.termsFacet( "space" ).field( ContentIndexField.SPACE ).allTerms( true );

            final TermsFacetBuilder typeFacet = FacetBuilders.termsFacet( "type" ).field( ContentIndexField.CONTENT_TYPE ).allTerms( true );

            final DateHistogramFacetBuilder modifiedFacet =
                FacetBuilders.dateHistogramFacet( "modified" ).field( ContentIndexField.LAST_MODIFIED + ".date" ).interval( "2d" );

            searchSourceBuilder.facet( spaceFacet ).facet( typeFacet ).facet( modifiedFacet );
        }

        return searchSourceBuilder;
    }

}
