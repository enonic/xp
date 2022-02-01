package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import java.time.Instant;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.aggregation.StatusesAggregationQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AbstractBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class StatusesAggregationQueryBuilderFactory
    extends AbstractBuilderFactory
{
    StatusesAggregationQueryBuilderFactory( final QueryFieldNameResolver fieldNameResolver )
    {
        super( fieldNameResolver );
    }

    AggregationBuilder create( final StatusesAggregationQuery aggregationQuery )
    {
        final Instant now = Instant.now();

        return new FiltersAggregationBuilder( aggregationQuery.getName() ).
            filter( "NEW", createNewContentFilter() ).
            filter( "PUBLISHED", createPublishedContentFilter( now ) ).
            filter( "UNPUBLISHED", createUnpublishedContentFilter() ).
            filter( "EXPIRED", createExpiredContentFilter( now ) );
    }

    private BoolQueryBuilder createNewContentFilter()
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.mustNot( new ExistsQueryBuilder( ContentIndexPath.PUBLISH_FIRST.getPath() ) );

        return boolQueryBuilder;
    }

    private BoolQueryBuilder createPublishedContentFilter( Instant time )
    {
        final BoolQueryBuilder notPendingFilter = new BoolQueryBuilder();
        notPendingFilter.mustNot( new RangeQueryBuilder( ContentIndexPath.PUBLISH_FROM.getPath() ).
            from( ValueFactory.newDateTime( time ) ).includeLower( true ) );

        final BoolQueryBuilder notExpiredFilter =
            new BoolQueryBuilder().mustNot( new RangeQueryBuilder( ContentIndexPath.PUBLISH_TO.getPath() ).
                to( ValueFactory.newDateTime( time ) ).includeUpper( true ) );

        return new BoolQueryBuilder().must( notPendingFilter ).must( notExpiredFilter );
    }

    private BoolQueryBuilder createUnpublishedContentFilter()
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.must( new ExistsQueryBuilder( ContentIndexPath.PUBLISH_FIRST.getPath() ) ).
            mustNot( new ExistsQueryBuilder( ContentIndexPath.PUBLISH_FROM.getPath() ) );

        return boolQueryBuilder;
    }

    private BoolQueryBuilder createExpiredContentFilter( Instant time )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.must(
            new RangeQueryBuilder( ContentIndexPath.PUBLISH_TO.getPath() ).to( ValueFactory.newDateTime( time ) ).includeUpper( true ) );

        return boolQueryBuilder;
    }
}
