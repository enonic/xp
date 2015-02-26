package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

public class FindNodesByQueryCommandTest_dateTime
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
    public void instant_compare_query()
        throws Exception
    {
        createNodeWithInstant( Instant.parse( "2015-02-26T12:00:00Z" ), "node1", NodePath.ROOT );
        createNodeWithInstant( Instant.parse( "2015-02-26T13:00:00Z" ), "node2", NodePath.ROOT );

        queryAndAssert( "myInstant = instant('2015-02-26T12:00:00Z')", 1 );
        queryAndAssert( "myInstant < instant('2015-02-26T12:00:00Z')", 0 );
        queryAndAssert( "myInstant <= instant('2015-02-26T12:00:00Z')", 1 );
        queryAndAssert( "myInstant > instant('2015-02-26T12:00:00Z')", 1 );
        queryAndAssert( "myInstant >= instant('2015-02-26T12:00:00Z')", 2 );
    }


    @Test
    public void localDate()
        throws Exception
    {
        createNodeWithLocalDate( LocalDate.parse( "2015-02-26" ), "node1", NodePath.ROOT );
        createNodeWithLocalDate( LocalDate.parse( "2015-02-27" ), "node2", NodePath.ROOT );

        queryAndAssert( "myLocalDate = instant('2015-02-26T00:00:00Z')", 1 );
    }


    @Test
    public void with_milliseconds()
        throws Exception
    {
        createNodeWithInstant( Instant.parse( "2015-02-26T12:00:00Z" ), "node1", NodePath.ROOT );
        createNodeWithInstant( Instant.parse( "2015-02-26T12:00:00.001Z" ), "node2", NodePath.ROOT );
        createNodeWithInstant( Instant.parse( "2015-02-26T13:00:00Z" ), "node3", NodePath.ROOT );

        queryAndAssert( "myInstant = instant('2015-02-26T12:00:00.001Z')", 1 );
        queryAndAssert( "myInstant < instant('2015-02-26T12:00:00.001Z')", 1 );
    }

    private Node createNodeWithInstant( final Instant instant, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addInstant( "myInstant", instant );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

    private Node createNodeWithLocalDate( final LocalDate localDate, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addLocalDate( "myLocalDate", localDate );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

}
