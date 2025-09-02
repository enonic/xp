package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.SortNodeCommand;

import static org.assertj.core.api.Assertions.assertThat;

public class ReorderChildNodesCommandTest
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
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        // current node order: a,b,c,d,e,f
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

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: c,a,b,d,e,f
        assertThat(reOrderedResult.getNodeIds() ).map( NodeId::toString ).containsExactly( "c", "a", "b", "d", "e", "f" );
    }

    @Test
    public void move_in_between()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        // current node order: a,b,c,d,e,f
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

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: a,c,b,d,e,f
        assertThat(reOrderedResult.getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "c", "b", "d", "e", "f" );
    }

    @Test
    public void move_last()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        // current node order: a,b,c,d,e,f
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

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        // updated node order: a,b,d,e,f,c
        assertThat(reOrderedResult.getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "d", "e", "f", "c" );
    }

    @Test
    public void move_last_missing_order_values()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        createChildNodes( parentNode );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.name() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        setManualOrderValueToNull( NodeId.from( "f" ) );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .addManualOrder(
                             ReorderChildNodeParams.create().nodeId( NodeId.from( "c" ) ).moveBefore( NodeId.from( "f" ) ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        final FindNodesByParentResult reOrderedResult = findByParent( parentNode.path() );

        assertThat( reOrderedResult.getNodeIds() ).map( NodeId::toString ).containsExactly( "a", "b", "d", "e", "c", "f" );
    }

    private void setManualOrderValueToNull( final NodeId nodeId )
    {
        updateNode( UpdateNodeParams.create().
            editor( ( node ) -> {
                node.manualOrderValue = null;
            } ).
            id( nodeId ).
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


