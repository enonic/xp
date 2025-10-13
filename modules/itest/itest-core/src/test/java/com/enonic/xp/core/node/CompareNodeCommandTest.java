package com.enonic.xp.core.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PushNodeParams;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.repo.impl.node.CompareNodeCommand;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;
import com.enonic.xp.repo.impl.node.PatchNodeCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompareNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    void status_new()
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.NEW, comparison.getCompareStatus() );
    }

    @Test
    void status_new_target()
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    void status_capital_node_id()
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            setNodeId( NodeId.from( "MyNodeId" ) ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    void status_deleted_stage_yields_new_in_target()
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        assertNotNull( ctxOther().callWith( () -> getNodeById( createdNode.id() ) ) );

        ctxOther().runWith( () -> doPushNode( WS_DEFAULT, createdNode ) );

        assertNotNull( ctxDefault().callWith( () -> getNodeById( createdNode.id() ) ) );

        DeleteNodeCommand.create().
            nodeId( createdNode.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }


    @Test
    void status_equal()
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    void status_newer()
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxDefault().runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.NEWER, comparison.getCompareStatus() );
    }

    @Test
    void status_older()
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxOther().runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.OLDER, comparison.getCompareStatus() );
    }


    @Test
    void status_moved_source()
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final Node mySecondNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-second-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxDefault().runWith( () -> moveNode( createdNode.id(), mySecondNode.path() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( NodeCompareStatus.MOVED, comparison.getCompareStatus() );
    }


    private NodeComparison doCompare( final Branch branch, final Node createdNode )
    {
        return CompareNodeCommand.create().
            storageService( this.storageService ).
            nodeId( createdNode.id() ).
            target( branch ).
            build().
            execute();
    }

    private Node doUpdateNode( final Node createdNode )
    {
        final PatchNodeParams updateNodeParams = PatchNodeParams.create().
            id( createdNode.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10L ).
            build();

        return PatchNodeCommand.create().
            params( updateNodeParams ).binaryService( this.binaryService ).indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().execute().getResult( ContextAccessor.current().getBranch() );
    }

    private PushNodesResult doPushNode( final Branch branch, final Node createdNode )
    {
        return PushNodesCommand.create().params( PushNodeParams.create().ids( NodeIds.from( createdNode.id() ) ).target( branch ).build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

}
