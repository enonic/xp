package com.enonic.xp.query.aggregation;

import org.junit.Test;

import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;

import static org.junit.Assert.*;

public class TermsAggregationQueryTest
{
    @Test
    public void testBuilder()
    {
        final TermsAggregationQuery query = TermsAggregationQuery.create( "category" ).
            fieldName( "category" ).
            orderDirection( TermsAggregationQuery.Direction.ASC ).
            orderType( TermsAggregationQuery.Type.TERM ).
            addSubQuery( StatsAggregationQuery.create( "subquery" ).
                fieldName( "other" ).
                build() ).
            build();

        assertEquals( "category", query.getName() );
        assertEquals( "category", query.getFieldName() );
        assertEquals( TermsAggregationQuery.Direction.ASC, query.getOrderDirection() );
        assertEquals( TermsAggregationQuery.Type.TERM, query.getOrderType() );
        assertEquals( 1, query.getSubQueries().getSize() );
    }

}
