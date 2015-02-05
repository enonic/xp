package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class CompareNodeCommandTest
    extends AbstractNodeTest
{
    private final Context draft = CTX_DEFAULT;

    private final Context online = CTX_OTHER;

    @Test
    public void status_new()
        throws Exception
    {
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
        final Node createdNode = online.callWith( () -> createNode( CreateNodeParams.create().
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
        final Node createdNode = online.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        assertNotNull( online.callWith( () -> getNodeById( createdNode.id() ) ) );

        online.runWith( () -> doPushNode( WS_DEFAULT, createdNode ) );

        assertNotNull( draft.callWith( () -> getNodeById( createdNode.id() ) ) );

        DeleteNodeByIdCommand.create().
            nodeId( createdNode.id() ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.Status.NEW_TARGET, comparison.getCompareStatus().getStatus() );
    }


    @Test
    public void status_equal()
        throws Exception
    {
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
        final Node createdNode = draft.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        draft.runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        online.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.Status.OLDER, comparison.getCompareStatus().getStatus() );
    }


    @Test
    public void status_moved_source()
        throws Exception
    {
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
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute() );

        final NodeComparison comparison = draft.callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.Status.MOVED, comparison.getCompareStatus().getStatus() );
    }


    private NodeComparison doCompare( final Workspace workspace, final Node createdNode )
    {
        return CompareNodeCommand.create().
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeId( createdNode.id() ).
            target( workspace ).
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
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    private PushNodesResult doPushNode( final Workspace workspace, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( workspace ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }

}
