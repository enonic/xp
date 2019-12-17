package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindNodesByQueryCommandTest_func_range
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void string_range()
        throws Exception
    {
        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-3" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-4" ).
            build() );

        queryAndAssert( 2, "range('" + NodeIndexPath.NAME.getPath() + "', 'node-1', 'node-4' )" );
    }

    @Test
    public void array_values()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "version", "6.2.5" );
        data.addString( "version", "6.3.5" );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( data ).
            build() );

        queryAndAssert( 0, "range('version', '6.3.0', '6.3.1' )" );
        queryAndAssert( 1, "range('version', '6.2.4', '6.3.1' )" );
        queryAndAssert( 1, "range('version', '6.2.5', '6.3.1', 'true', 'false' )" );
    }

    @Test
    public void instant_values()
        throws Exception
    {
        final PropertyTree node1 = new PropertyTree();
        node1.addInstant( "publishFrom", Instant.parse( "2015-08-01T10:00:00Z" ) );

        final PropertyTree node2 = new PropertyTree();
        node2.addInstant( "publishFrom", Instant.parse( "2015-08-01T11:00:00Z" ) );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( node1 ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            data( node2 ).
            build() );

        queryAndAssert( 1, "range('publishfrom', instant('2015-08-01T09:00:00Z'), instant('2015-08-01T11:00:00Z') )" );
        queryAndAssert( 2, "range('publishfrom', instant('2015-08-01T09:00:00Z'), instant('2015-08-01T11:00:00Z'), 'false', 'true' )" );
        queryAndAssert( 2, "range('publishfrom', instant('2015-08-01T09:00:00Z'), instant('2015-08-01T12:00:00Z') )" );
    }

    @Test
    public void numeric_values()
        throws Exception
    {
        final PropertyTree node1 = new PropertyTree();
        node1.addDouble( "myValue", 2.0 );

        final PropertyTree node2 = new PropertyTree();
        node2.addDouble( "myValue", 3.0 );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( node1 ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            data( node2 ).
            build() );

        queryAndAssert( 0, "range('myValue', 2.0, 3.0 )" );
        queryAndAssert( 0, "range('myValue', 2.0, 3.0, 'false', 'false' )" );
        queryAndAssert( 1, "range('myValue', 2.0, 3.0, 'true', 'false' )" );
        queryAndAssert( 2, "range('myValue', 2.0, 3.0, 'true', 'true' )" );
    }

    @Test
    public void array_values_in_range()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "version", "6.2.5" );
        data.addString( "version", "6.3.5" );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( data ).
            build() );

        queryAndAssert( 1, "range('version', '6.3.0', '6.4.0' )" );
    }

    @Test
    public void empty_to_string()
        throws Exception
    {
        final PropertyTree node1 = new PropertyTree();
        node1.addString( "myValue", "a" );

        final PropertyTree node2 = new PropertyTree();
        node2.addString( "myValue", "b" );

        final PropertyTree node3 = new PropertyTree();
        node3.addString( "myValue", "c" );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( node1 ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            data( node2 ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-3" ).
            data( node3 ).
            build() );

        queryAndAssert( 3, "range('myValue', 'a', '', 'true', 'false')" );
        queryAndAssert( 2, "range('myValue', 'b', '', 'true', 'false')" );
        queryAndAssert( 1, "range('myValue', 'c', '', 'true', 'false')" );
    }

    @Test
    public void empty_to_instant()
        throws Exception
    {
        final PropertyTree node1 = new PropertyTree();
        node1.addInstant( "publishFrom", Instant.parse( "2015-08-01T10:00:00Z" ) );

        final PropertyTree node2 = new PropertyTree();
        node2.addInstant( "publishFrom", Instant.parse( "2015-08-01T11:00:00Z" ) );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            data( node1 ).
            build() );

        createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            data( node2 ).
            build() );

        queryAndAssert( 2, "range('publishfrom', instant('2015-08-01T09:00:00Z'), '')" );
        queryAndAssert( 2, "range('publishfrom', '', instant('2017-08-01T09:00:00Z'))" );
    }


    private void queryAndAssert( final int exptected, final String queryString )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryParser.parse( queryString ) ).build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( exptected, result.getNodeIds().getSize() );
    }

}
