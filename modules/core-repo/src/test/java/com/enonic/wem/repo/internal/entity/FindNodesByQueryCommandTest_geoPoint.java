package com.enonic.wem.repo.internal.entity;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_geoPoint
    extends AbstractNodeTest
{
    private static final GeoPoint OSLO = GeoPoint.from( "59.9127300,10.7460900" );

    private static final GeoPoint BERLIN = GeoPoint.from( "52.5243700,13.4105300" );

    private static final GeoPoint MOSCOW = GeoPoint.from( "55.7522200,37.6155600" );

    private static final GeoPoint FREDRIKSTAD = GeoPoint.from( "59.2181000,10.9298000" );

    private static final GeoPoint LONDON = GeoPoint.from( "51.5085300,-0.1257400" );

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
    public void string_value_equals()
        throws Exception
    {
        createGeoLocations();

        queryAndAssert( "myLocation = '" + FREDRIKSTAD.toString() + "'", 1 );
    }

    private void createGeoLocations()
    {
        createNodeWithLocation( OSLO, "oslo", NodePath.ROOT );
        createNodeWithLocation( BERLIN, "berlin", NodePath.ROOT );
        createNodeWithLocation( NEW_YORK, "new-york", NodePath.ROOT );
        createNodeWithLocation( TRONDHEIM, "trondheim", NodePath.ROOT );
        createNodeWithLocation( MOSCOW, "moscow", NodePath.ROOT );
        createNodeWithLocation( FREDRIKSTAD, "fredrikstad", NodePath.ROOT );
        createNodeWithLocation( LONDON, "london", NodePath.ROOT );
    }

    @Test
    public void string_value_in()
        throws Exception
    {
        createGeoLocations();

        queryAndAssert( "myLocation IN ('" + FREDRIKSTAD.toString() + "','" + OSLO.toString() + "')", 2 );
    }

    @Test
    public void order_by_geoDistance()
        throws Exception
    {
        createGeoLocations();

        final FindNodesByQueryResult result = doQuery( "ORDER BY geoDistance('myLocation', '59.9127300 ,10.7460900')" );

        assertOrder( result );
    }

    private void assertOrder( final FindNodesByQueryResult result )
    {
        final Iterator<Node> iterator = result.getNodes().iterator();
        assertEquals( "oslo", iterator.next().name().toString() );
        assertEquals( "fredrikstad", iterator.next().name().toString() );
        assertEquals( "trondheim", iterator.next().name().toString() );
        assertEquals( "berlin", iterator.next().name().toString() );
        assertEquals( "london", iterator.next().name().toString() );
        assertEquals( "moscow", iterator.next().name().toString() );
        assertEquals( "new-york", iterator.next().name().toString() );
    }


    private Node createNodeWithLocation( final GeoPoint geoPoint, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addGeoPoint( "myLocation", geoPoint );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

}
