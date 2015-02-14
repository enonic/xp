package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.stats.Stats;

import com.enonic.xp.aggregation.StatsAggregation;

class StatsAggregationFactory
    extends AggregationsFactory
{

    static StatsAggregation create( final Stats stats )
    {
        return StatsAggregation.create( stats.getName() ).
            avg( stats.getAvg() ).
            count( stats.getCount() ).
            max( stats.getMax() ).
            min( stats.getMin() ).
            sum( stats.getSum() ).
            build();
    }

}
