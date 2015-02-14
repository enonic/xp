package com.enonic.wem.repo.internal.entity.query.aggregation;

import java.time.Instant;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.Buckets;
import com.enonic.wem.api.aggregation.DateRangeBucket;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.aggregation.DateRange;
import com.enonic.wem.api.query.aggregation.DateRangeAggregationQuery;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class DateRangeAggregationTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void ranges()
        throws Exception
    {
        createNode( Instant.parse( "2014-12-10T10:00:00Z" ), "n1", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T10:30:00Z" ), "n2", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T11:30:00Z" ), "n3", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T12:45:00Z" ), "n4", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T13:59:59Z" ), "n5", NodePath.ROOT );
        createNode( Instant.parse( "2014-12-10T14:01:00Z" ), "n6", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( DateRangeAggregationQuery.create( "myDateRange" ).
                fieldName( "instant" ).
                addRange( DateRange.create().
                    to( Instant.parse( "2014-12-10T11:00:00Z" ) ).
                    build() ).
                addRange( DateRange.create().
                    from( Instant.parse( "2014-12-10T11:00:00Z" ) ).
                    to( Instant.parse( "2014-12-10T14:00:00Z" ) ).
                    build() ).
                addRange( DateRange.create().
                    from( Instant.parse( "2014-12-10T14:00:00Z" ) ).
                    build() ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final BucketAggregation aggregation = (BucketAggregation) result.getAggregations().get( "myDateRange" );

        final Buckets buckets = aggregation.getBuckets();

        final Iterator<Bucket> iterator = buckets.iterator();
        verifyBucket( iterator.next(), 2 );
        verifyBucket( iterator.next(), 3 );
        verifyBucket( iterator.next(), 1 );
    }

    private void verifyBucket( final Bucket bucket, final int count )
    {
        assertTrue( bucket instanceof DateRangeBucket );
        final DateRangeBucket buck = (DateRangeBucket) bucket;

        assertEquals( count, buck.getDocCount() );
    }

    private Node createNode( final Instant instantValue, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addInstant( "instant", instantValue );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }
}
