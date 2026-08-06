package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeServiceImplTest_findByQuery_parent
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void parent_matches_direct_children_only()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node child1 = createNode( parent.path(), "child1" );
        final Node child2 = createNode( parent.path(), "child2" );
        createNode( child1.path(), "grandchild" );
        createNode( NodePath.ROOT, "outside" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( parent.path() ).size( -1 ).build() );

        assertThat( result.getNodeIds() ).containsExactlyInAnyOrder( child1.id(), child2.id() );
    }

    @Test
    void recursive_matches_every_descendant()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node child1 = createNode( parent.path(), "child1" );
        final Node child2 = createNode( parent.path(), "child2" );
        final Node grandchild = createNode( child1.path(), "grandchild" );
        final Node outside = createNode( NodePath.ROOT, "outside" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( parent.path() ).recursive( true ).size( -1 ).build() );

        assertThat( result.getNodeIds() ).containsExactlyInAnyOrder( child1.id(), child2.id(), grandchild.id() )
            .doesNotContain( parent.id(), outside.id() );
    }

    @Test
    void recursive_from_root_matches_everything_but_root()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        final Node child = createNode( parent.path(), "child" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( NodePath.ROOT ).recursive( true ).size( -1 ).build() );

        assertThat( result.getNodeIds() ).contains( parent.id(), child.id() );
        assertThat( result.getNodeIds().stream().map( id -> nodeService.getById( id ).path() ) ).doesNotContain( NodePath.ROOT );
    }

    @Test
    void recursive_expects_a_parent()
    {
        final NodeQuery.Builder builder = NodeQuery.create().recursive( true );

        assertEquals( "recursive expects a parent", assertThrows( IllegalArgumentException.class, builder::build ).getMessage() );
    }

    @Test
    void parent_without_order_falls_back_to_child_order_of_parent()
    {
        final Node parent = createNode(
            CreateNodeParams.create().name( "parent" ).parent( NodePath.ROOT ).childOrder( ChildOrder.from( "_name DESC" ) ).build() );
        final Node child1 = createNode( parent.path(), "a" );
        final Node child2 = createNode( parent.path(), "b" );
        final Node child3 = createNode( parent.path(), "c" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( parent.path() ).size( -1 ).build() );

        assertThat( result.getNodeIds() ).containsExactly( child3.id(), child2.id(), child1.id() );
    }

    @Test
    void own_order_wins_over_child_order_of_parent()
    {
        final Node parent = createNode(
            CreateNodeParams.create().name( "parent" ).parent( NodePath.ROOT ).childOrder( ChildOrder.from( "_name DESC" ) ).build() );
        final Node child1 = createNode( parent.path(), "a" );
        final Node child2 = createNode( parent.path(), "b" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = nodeService.findByQuery(
            NodeQuery.create().query( QueryParser.parse( "order by _name asc" ) ).parent( parent.path() ).size( -1 ).build() );

        assertThat( result.getNodeIds() ).containsExactly( child1.id(), child2.id() );
    }

    @Test
    void count_query_skips_child_order_resolution()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        createNode( parent.path(), "child1" );
        createNode( parent.path(), "child2" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( parent.path() ).recursive( true ).size( 0 ).build() );

        assertEquals( 2, result.getTotalHits() );
        assertThat( result.getNodeIds() ).isEmpty();
    }

    @Test
    void parent_that_does_not_exist_matches_nothing()
    {
        createNode( NodePath.ROOT, "parent" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            nodeService.findByQuery( NodeQuery.create().parent( new NodePath( "/no-such-parent" ) ).size( -1 ).build() );

        assertEquals( 0, result.getTotalHits() );
    }

    @Test
    void recursive_combines_with_query_constraint()
    {
        final Node parent = createNode( NodePath.ROOT, "parent" );
        createNode( parent.path(), "keep-me" );
        final Node child2 = createNode( parent.path(), "other" );
        createNode( child2.path(), "keep-me-too" );
        createNode( NodePath.ROOT, "keep-me-outside" );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create()
                                                                           .query( QueryParser.parse( "_name LIKE 'keep-me*'" ) )
                                                                           .parent( parent.path() )
                                                                           .recursive( true )
                                                                           .size( -1 )
                                                                           .build() );

        assertEquals( 2, result.getTotalHits() );
    }
}
