package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;

import static com.google.common.base.Strings.isNullOrEmpty;

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

        if ( !isNullOrEmpty( orderByDirection ) )
        {
            builder.orderDirection( TermsAggregationQuery.Direction.valueOf( orderByDirection.toUpperCase() ) );
        }

        if ( !isNullOrEmpty( orderByType ) )
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
