package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.SortNodeCommand;

import static org.assertj.core.api.Assertions.assertThat;

public class SortNodeCommandTest_manualOrder
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void move_first()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "a" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "c", "a", "b", "d", "e", "f" );
    }

    @Test
    public void move_in_between()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "b" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "c", "b", "d", "e", "f" );
    }

    @Test
    public void move_last()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "d", "e", "f", "c" );
    }

    @Test
    public void move_inexistent_keeps_seed()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( NodeId.from( "g" ) ).build() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "g" ) ).moveBefore( NodeId.from( "z" ) ).build() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "g" ) ).moveBefore( NodeId.from( "a" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "c", "d", "e", "f" );
    }

    @Test
    public void move_before_inexistent_keeps_seed()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "a" ) ).moveBefore( NodeId.from( "g" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "c", "d", "e", "f" );
    }

    @Test
    public void reorder()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        manualOrderWithByNameSeed( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "a" ) ).moveBefore( NodeId.from( "e" ) ).build() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "b" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        // `a` moved right in front of `e`
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "c", "d", "a", "e", "f", "b" );
    }

    @Test
    public void move_last_missing_order_values()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        manualOrderWithByNameSeed( parentNode );

        setManualOrderValueToNull( NodeId.from( "e" ) );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "a" ) ).moveBefore( NodeId.from( "e" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        refresh();
        // `e` moved to the end but `a` left intact
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "c", "d", "f", "e" );
    }

    private void manualOrderWithByNameSeed( final Node parentNode )
    {
        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .manualOrderSeed( ChildOrder.name() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    private void setManualOrderValueToNull( final NodeId nodeId )
    {
        updateNode( UpdateNodeParams.create().
            editor( ( node ) -> {
                node.manualOrderValue = null;
            } ).
            id( nodeId ).
            refresh( RefreshMode.ALL ).
            build() );
    }

    private void createChildNodes( final Node parentNode )
    {
        createNode( parentNode.path(), "b" );
        createNode( parentNode.path(), "a" );
        createNode( parentNode.path(), "c" );
        createNode( parentNode.path(), "f" );
        createNode( parentNode.path(), "e" );
        createNode( parentNode.path(), "d" );
    }
}


