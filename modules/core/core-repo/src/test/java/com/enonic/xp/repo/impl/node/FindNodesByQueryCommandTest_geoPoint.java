package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.util.GeoPoint;

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

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void string_value_equals()
        throws Exception
    {
        createGeoLocationNodes();

        queryAndAssert( "myLocation = '" + FREDRIKSTAD + "'", 1 );
    }

    private void createGeoLocationNodes()
    {
        createNodeWithLocation( OSLO, "oslo", NodePath.ROOT );
        createNodeWithLocation( BERLIN, "berlin", NodePath.ROOT );
        createNodeWithLocation( NEW_YORK, "new-york", NodePath.ROOT );
        createNodeWithLocation( TRONDHEIM, "trondheim", NodePath.ROOT );
        createNodeWithLocation( MOSCOW, "moscow", NodePath.ROOT );
        createNodeWithLocation( FREDRIKSTAD, "fredrikstad", NodePath.ROOT );
        createNodeWithLocation( LONDON, "london", NodePath.ROOT );
        nodeService.refresh( RefreshMode.ALL );
    }

    @Test
    public void string_value_in()
        throws Exception
    {
        createGeoLocationNodes();
        queryAndAssert( "myLocation IN ('" + FREDRIKSTAD + "','" + OSLO + "')", 2 );
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
