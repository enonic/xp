package com.enonic.wem.core.index.query.aggregation

import com.enonic.wem.api.query.aggregation.DateRange
import com.enonic.wem.api.query.aggregation.NumericRange
import com.enonic.wem.api.query.aggregation.RangeAggregationQuery
import com.enonic.wem.core.index.query.builder.BaseTestBuilderFactory
import org.elasticsearch.common.xcontent.ToXContent
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.search.aggregations.AggregationBuilder
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder
import org.joda.time.DateTime

class RangeAggregationBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    def "date range aggregation"()
    {
        def DateTime past = new DateTime( 1975, 8, 1, 12, 04 )
        def DateTime future = new DateTime( 2055, 01, 01, 12, 00 )

        given:
        def expectedJson = this.getClass().getResource( "aggs_daterange.json" ).text

        def RangeAggregationQuery query = RangeAggregationQuery.dateRangeQuery( "myRangeQuery" ).
            fieldName( "myField" ).
            range( DateRange.newDateRange().
                       key( "to eternity" ).
                       to( future ).
                       build() ).
            range( DateRange.newDateRange().
                       from( past ).
                       build() ).
            build();

        when:
        AggregationBuilder builder = RangeAggregationBuilderFactory.create( query )

        then:
        builder instanceof DateRangeBuilder
        // This have to wait until we get a order neutral json comparer
        // cleanString( getJson( builder ) ) == cleanString( expectedJson )
    }

    def "date range aggregation with date math expression"()
    {

        given:
        def expectedJson = this.getClass().getResource( "aggs_daterange_datemath.json" ).text

        def RangeAggregationQuery query = RangeAggregationQuery.dateRangeQuery( "myRangeQuery" ).
            fieldName( "myField" ).
            range( DateRange.newDateRange().
                       key( "ten days from now" ).
                       to( "now+10d/d" ).
                       build() ).
            range( DateRange.newDateRange().
                       from( "now-10d/d" ).
                       build() ).
            build();

        when:
        AggregationBuilder builder = RangeAggregationBuilderFactory.create( query )

        then:
        builder instanceof DateRangeBuilder
        // This have to wait until we get a order neutral json comparer
        // cleanString( getJson( builder ) ) == cleanString( expectedJson )
    }


    def "numeric range aggregation"()
    {
        given:
        def expectedJson = this.getClass().getResource( "aggs_numericrange.json" ).text

        def RangeAggregationQuery query = RangeAggregationQuery.numericRangeQuery( "myRangeQuery" ).
            fieldName( "myField" ).
            range( NumericRange.newNumericRange().
                       key( "to eternity" ).
                       to( 1000 ).
                       build() ).
            range( NumericRange.newNumericRange().
                       from( 10 ).
                       build() ).
            build();

        when:
        AggregationBuilder builder = RangeAggregationBuilderFactory.create( query )

        then:
        builder instanceof RangeBuilder
        // This have to wait until we get a order neutral json comparer
        //cleanString( getJson( builder ) ) == cleanString( expectedJson )
    }

    public String getJson( AggregationBuilder aggregationBuilder )
        throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        aggregationBuilder.toXContent( builder, ToXContent.EMPTY_PARAMS );
        builder.endObject();

        return builder.string();
    }
}
