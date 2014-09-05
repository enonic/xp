package com.enonic.wem.core.elasticsearch.aggregation

import com.enonic.wem.api.query.aggregation.AggregationQuery
import com.enonic.wem.api.query.aggregation.TermsAggregationQuery
import com.google.common.collect.Sets
import org.elasticsearch.search.aggregations.AggregationBuilder
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder
import spock.lang.Specification

class AggregationQueryBuilderFactoryTest
    extends Specification
{
    def "term aggregation"()
    {
        given:

        AggregationBuilderFactory factory = new AggregationBuilderFactory();
        TermsAggregationQuery termsAgg = AggregationQuery.newTermsAggregation( "myTermAgg" ).fieldName( "myFieldName" ).size( 10 ).build();

        when:
        Set<AggregationBuilder> builders = factory.create( Sets.newHashSet( termsAgg ) );

        then:
        builders.iterator().next() instanceof TermsBuilder
    }

}
