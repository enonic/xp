package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.RareTermsAggregationQuery;

public class RareTermsAggregationQueryJson
    extends AggregationQueryJson
{

    private RareTermsAggregationQuery rareTermsAggregationQuery;

    @JsonCreator
    public RareTermsAggregationQueryJson( @JsonProperty("name") final String name, //
                                          @JsonProperty("fieldName") final String fieldName, //
                                          @JsonProperty("maxDocCount") final Integer maxDocCount )
    {
        final RareTermsAggregationQuery.Builder builder = RareTermsAggregationQuery.create( name ).
            fieldName( fieldName ).
            maxDocCount( maxDocCount );

        this.rareTermsAggregationQuery = builder.build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return rareTermsAggregationQuery;
    }

}
