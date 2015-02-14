package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.content.CompareStatus;
import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.node.CreateNodeParams;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodeComparison;
import com.enonic.xp.core.node.NodeIds;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.PushNodesResult;
import com.enonic.xp.core.node.UpdateNodeParams;

import static org.junit.Assert.*;

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

        assertEquals( CompareStatus.Status.NEW, comparison.getCompareStatus().getStatus() );
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

        assertEquals( CompareStatus.Status.NEW_TARGET, comparison.getCompareStatus().getStatus() );
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
            branchService( this.branchService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.Status.NEW_TARGET, comparison.getCompareStatus().getStatus() );
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

        assertEquals( CompareStatus.Status.EQUAL, comparison.getCompareStatus().getStatus() );
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

        assertEquals( CompareStatus.Status.NEWER, comparison.getCompareStatus().getStatus() );
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

        assertEquals( CompareStatus.Status.OLDER, comparison.getCompareStatus().getStatus() );
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
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute() );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.Status.MOVED, comparison.getCompareStatus().getStatus() );
    }


    private NodeComparison doCompare( final Branch branch, final Node createdNode )
    {
        return CompareNodeCommand.create().
            versionService( this.versionService ).
            branchService( this.branchService ).
            nodeId( createdNode.id() ).
            target( branch ).
            build().
            execute();
    }

    private Node doUpdateNode( final Node createdNode )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( createdNode.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10l ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    private PushNodesResult doPushNode( final Branch branch, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( branch ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

}
