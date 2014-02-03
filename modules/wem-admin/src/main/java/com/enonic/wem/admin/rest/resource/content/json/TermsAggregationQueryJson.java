package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery;

public class TermsAggregationQueryJson
    extends AggregationQueryJson
{

    private TermsAggregationQuery termsAggregationQuery;

    @JsonCreator
    public TermsAggregationQueryJson( @JsonProperty("name") final String name, //
                                      @JsonProperty("fieldName") final String fieldName, //
                                      @JsonProperty("size") final Integer size )
    {
        this.termsAggregationQuery = TermsAggregationQuery.newTermsAggregation( name ).
            fieldName( fieldName ).
            size( size ).
            build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return termsAggregationQuery;
    }
}
