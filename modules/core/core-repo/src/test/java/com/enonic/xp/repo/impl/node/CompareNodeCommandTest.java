package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompareNodeCommandTest
    extends AbstractNodeTest
{
    private final Context draft = CTX_DEFAULT;

    private final Context master = CTX_OTHER;

    @Test
    public void status_new()
        throws Exception
    {
        draft.callWith( this::createDefaultRootNode );

        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW, comparison.getCompareStatus() );
    }

    @Test
    public void status_new_target()
        throws Exception
    {
        master.callWith( this::createDefaultRootNode );

        final Node createdNode = master.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    public void status_capital_node_id()
        throws Exception
    {
        master.callWith( this::createDefaultRootNode );

        final Node createdNode = master.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            setNodeId( NodeId.from( "MyNodeId" ) ).
            build() ) );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    public void status_deleted_stage_yields_new_in_target()
        throws Exception
    {
        master.callWith( this::createDefaultRootNode );

        final Node createdNode = master.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        assertNotNull( master.callWith( () -> getNodeById( createdNode.id() ) ) );

        master.runWith( () -> doPushNode( WS_DEFAULT, createdNode ) );

        assertNotNull( draft.callWith( () -> getNodeById( createdNode.id() ) ) );

        DeleteNodeByIdCommand.create().
            nodeId( createdNode.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }


    @Test
    public void status_equal()
        throws Exception
    {
        draft.callWith( this::createDefaultRootNode );

        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        draft.runWith( () -> doPushNode( WS_OTHER, createdNode ) );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    public void status_newer()
        throws Exception
    {
        draft.callWith( this::createDefaultRootNode );

        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        draft.runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        draft.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEWER, comparison.getCompareStatus() );
    }

    @Test
    public void status_older()
        throws Exception
    {
        draft.callWith( this::createDefaultRootNode );

        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        draft.runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        master.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.OLDER, comparison.getCompareStatus() );
    }


    @Test
    public void status_moved_source()
        throws Exception
    {
        draft.callWith( this::createDefaultRootNode );

        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final Node mySecondNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-second-node" ).
            build() ) );

        draft.runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        draft.runWith( () -> MoveNodeCommand.create().
            id( createdNode.id() ).
            newParent( mySecondNode.path() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute() );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.MOVED, comparison.getCompareStatus() );
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
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( createdNode.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10L ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private PushNodesResult doPushNode( final Branch branch, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( branch ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

}
