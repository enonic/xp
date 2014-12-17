package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;

public class TermsAggregationQueryJson
    extends AggregationQueryJson
{

    private TermsAggregationQuery termsAggregationQuery;

    @JsonCreator
    public TermsAggregationQueryJson( @JsonProperty("name") final String name, //
                                      @JsonProperty("fieldName") final String fieldName, //
                                      @JsonProperty("size") final Integer size, //
                                      @JsonProperty("orderByDirection") final String orderByDirection, //
                                      @JsonProperty("orderByType") final String orderByType )
    {
        final TermsAggregationQuery.Builder builder = TermsAggregationQuery.create( name ).
            fieldName( fieldName ).
            size( size );

        if ( !Strings.isNullOrEmpty( orderByDirection ) )
        {
            builder.orderDirection( TermsAggregationQuery.Direction.valueOf( orderByDirection.toUpperCase() ) );
        }

        if ( !Strings.isNullOrEmpty( orderByType ) )
        {
            builder.orderType( TermsAggregationQuery.Type.valueOf( orderByType.toUpperCase() ) );
        }

        this.termsAggregationQuery = builder.build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return termsAggregationQuery;
    }

}
