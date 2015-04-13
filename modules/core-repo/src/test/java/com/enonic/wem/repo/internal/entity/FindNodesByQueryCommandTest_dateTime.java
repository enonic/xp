package com.enonic.wem.repo.internal.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
        createNodeWithLocalDate( LocalDate.parse( "2015-01-26" ), "node1", NodePath.ROOT );
        createNodeWithLocalDate( LocalDate.parse( "2015-02-26" ), "node2", NodePath.ROOT );
        createNodeWithLocalDate( LocalDate.parse( "2015-02-27" ), "node3", NodePath.ROOT );

        queryAndAssert( "myLocalDate = '2015-02-26'", 1 );
        queryAndAssert( "myLocalDate >= '2015-02-26'", 2 );
    }

    @Test
    public void localDateTime()
        throws Exception
    {
        createNodeWithLocalDateTime( LocalDateTime.parse( "2015-01-26T10:00:00" ), "node1", NodePath.ROOT );
        createNodeWithLocalDateTime( LocalDateTime.parse( "2015-01-27T10:00:00" ), "node2", NodePath.ROOT );

        printContentRepoIndex();

        queryAndAssert( "myLocalDateTime = '2015-01-26T10:00:00'", 1 );
        queryAndAssert( "myLocalDateTime = localDateTime('2015-01-26T10:00')", 1 );
        queryAndAssert( "myLocalDateTime > '2015-01-26T10:00:00'", 1 );
    }

    @Test
    public void localTime()
        throws Exception
    {
        createNodeWithLocalTime( LocalTime.of( 9, 36 ), "node1", NodePath.ROOT );
        createNodeWithLocalTime( LocalTime.of( 9, 36, 0 ), "node2", NodePath.ROOT );
        createNodeWithLocalTime( LocalTime.of( 10, 36, 30 ), "node3", NodePath.ROOT );
        createNodeWithLocalTime( LocalTime.of( 11, 36, 30 ), "node4", NodePath.ROOT );
        createNodeWithLocalTime( LocalTime.of( 12, 36, 30 ), "node5", NodePath.ROOT );
        createNodeWithLocalTime( LocalTime.of( 13, 36, 30 ), "node6", NodePath.ROOT );

        printContentRepoIndex();

        queryAndAssert( "myLocalTime > time('9:00')", 6 );
        queryAndAssert( "myLocalTime = '09:36'", 2 );
        queryAndAssert( "myLocalTime = '10:36'", 0 );
        queryAndAssert( "myLocalTime = '10:36:30'", 1 );
        queryAndAssert( "myLocalTime = time('10:36:30.0')", 1 );
        queryAndAssert( "myLocalTime LIKE '10:*'", 1 );
        queryAndAssert( "myLocalTime > '09:00'", 6 );
        queryAndAssert( "myLocalTime > '10:00'", 4 );
        queryAndAssert( "myLocalTime > '10:36:30'", 3 );
        queryAndAssert( "myLocalTime < '10:36:30'", 2 );
        queryAndAssert( "myLocalTime < '10:36:30.1'", 3 );
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

    private Node createNodeWithLocalDateTime( final LocalDateTime localDateTime, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addLocalDateTime( "myLocalDateTime", localDateTime );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }


    private Node createNodeWithLocalTime( final LocalTime localTime, final String name, final NodePath parent )
    {
        final PropertyTree data = new PropertyTree();
        data.addLocalTime( "myLocalTime", localTime );

        return createNode( CreateNodeParams.create().
            parent( parent ).
            name( name ).
            data( data ).
            build() );
    }

}
