package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.content.json.filter.FilterJson;
import com.enonic.xp.content.query.ContentQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeNames;

public class ContentQueryJson
{
    private final String expand;

    private final ContentQuery contentQuery;

    @JsonCreator
    ContentQueryJson( @JsonProperty("queryExpr") final String queryExprString, //
                      @JsonProperty("from") final Integer from, //
                      @JsonProperty("size") final Integer size, //
                      @JsonProperty("contentTypeNames") final List<String> contentTypeNameString,
                      @JsonProperty("expand") final String expand,
                      @JsonProperty("aggregationQueries") final List<AggregationQueryJson> aggregationQueries, //
                      @JsonProperty("queryFilters") final List<FilterJson> queryFilters )
    {

        final ContentQuery.Builder builder = ContentQuery.newContentQuery().
            from( from ).
            size( size ).
            queryExpr( QueryParser.parse( queryExprString ) ).
            addContentTypeNames( ContentTypeNames.from( contentTypeNameString ) );

        if ( aggregationQueries != null )
        {
            for ( final AggregationQueryJson aggregationQueryJson : aggregationQueries )
            {
                builder.aggregationQuery( aggregationQueryJson.getAggregationQuery() );
            }
        }

        if ( queryFilters != null )
        {
            for ( final FilterJson queryFilterJson : queryFilters )
            {
                builder.queryFilter( queryFilterJson.getFilter() );
            }
        }

        this.contentQuery = builder.build();
        this.expand = expand != null ? expand : "none";
    }

    @JsonIgnore
    public ContentQuery getContentQuery()
    {
        return contentQuery;
    }

    @JsonIgnore
    public String getExpand()
    {
        return expand;
    }
}
