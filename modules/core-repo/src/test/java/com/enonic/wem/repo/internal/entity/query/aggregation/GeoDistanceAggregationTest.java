package com.enonic.wem.repo.internal.entity.query.aggregation;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import junit.framework.Assert;

import com.enonic.wem.api.aggregation.Bucket;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.aggregation.Buckets;
import com.enonic.wem.api.aggregation.GeoDistanceRangeBucket;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.query.aggregation.DistanceRange;
import com.enonic.wem.api.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.wem.api.util.GeoPoint;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class GeoDistanceAggregationTest
    extends AbstractNodeTest
{
    private static final GeoPoint OSLO = GeoPoint.from( "59.9127300 ,10.7460900" );

    private static final GeoPoint BERLIN = GeoPoint.from( "52.5243700,13.4105300" );

    private static final GeoPoint MOSCOW = GeoPoint.from( "55.7522200,37.6155600\n" );

    private static final GeoPoint FREDRIKSTAD = GeoPoint.from( "59.2181000,10.9298000" );

    private static final GeoPoint LONDON = GeoPoint.from( "51.5085300 ,-0.1257400" );

    private static final GeoPoint NEW_YORK = GeoPoint.from( "40.7142700,-74.0059700" );

    private static final GeoPoint TRONDHEIM = GeoPoint.from( "63.4304900,10.3950600" );

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
        createNode( OSLO, "oslo", NodePath.ROOT );
        createNode( BERLIN, "berlin", NodePath.ROOT );
        createNode( NEW_YORK, "new-york", NodePath.ROOT );
        createNode( TRONDHEIM, "trondheim", NodePath.ROOT );
        createNode( MOSCOW, "moscow", NodePath.ROOT );
        createNode( FREDRIKSTAD, "fredrikstad", NodePath.ROOT );
        createNode( LONDON, "london", NodePath.ROOT );

        final NodeQuery query = NodeQuery.create().
            addAggregationQuery( GeoDistanceAggregationQuery.create( "myGeoDistance" ).
                fieldName( "geoPoint" ).
                origin( OSLO ).
                unit( "km" ).
                addRange( DistanceRange.create().
                    to( 200.0 ).
                    build() ).
                addRange( DistanceRange.create().
                    from( 200.0 ).
                    to( 800.0 ).
                    build() ).
                addRange( DistanceRange.create().
                    from( 800.0 ).
                    to( 1600.0 ).
                    build() ).
                addRange( DistanceRange.create().
                    from( 1600.0 ).
                    to( 2400.0 ).
                    build() ).
                addRange( DistanceRange.create().
                    from( 2400.0 ).
                    build() ).
                build() ).
            build();

        FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getAggregations().getSize() );

        final BucketAggregation aggregation = (BucketAggregation) result.getAggregations().get( "myGeoDistance" );

        final Buckets buckets = aggregation.getBuckets();

        final Iterator<Bucket> iterator = buckets.iterator();
        verifyBucket( iterator.next(), 2, null ); // OSLO, FREDRIKSTAD
        verifyBucket( iterator.next(), 1, null ); // TRONDHEIM
        verifyBucket( iterator.next(), 2, null ); // BERLIN, LONDON
        verifyBucket( iterator.next(), 1, null ); // MOSCOW
        verifyBucket( iterator.next(), 1, null ); // NEW YORK
    }

    private void verifyBucket( final Bucket bucket, final int count, final String key )
    {
        assertTrue( bucket instanceof GeoDistanceRangeBucket );
        final GeoDistanceRangeBucket buck = (GeoDistanceRangeBucket) bucket;

        assertEquals( count, buck.getDocCount() );

        if ( !Strings.isNullOrEmpty( key ) )
        {
            Assert.assertEquals( buck.getKey(), key );
        }
    }

    private Node createNode( final GeoPoint geoPoint, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addGeoPoint( "geoPoint", geoPoint );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }


}
