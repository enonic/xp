package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class CompareNodesCommandTest
    extends AbstractNodeTest
{

    @Test
    public void status_new()
        throws Exception
    {
        final Node createdNode = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = CTX_DEFAULT.callWith( () -> doCompare( WS_PROD, createdNode ) );

        assertEquals( CompareStatus.Status.NEW, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_equal()
        throws Exception
    {
        final Context stage = CTX_DEFAULT;
        final Workspace prod = Workspace.from( "prod" );

        final Node createdNode = stage.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        stage.runWith( () -> doPushNode( prod, createdNode ) );

        final NodeComparison comparison = stage.callWith( () -> doCompare( prod, createdNode ) );

        assertEquals( CompareStatus.Status.EQUAL, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_newer()
        throws Exception
    {
        final Context stage = CTX_DEFAULT;
        final Workspace prodWs = WS_PROD;

        final Node createdNode = stage.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        stage.runWith( () -> doPushNode( prodWs, createdNode ) );
        refresh();

        stage.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = stage.callWith( () -> doCompare( prodWs, createdNode ) );

        assertEquals( CompareStatus.Status.NEWER, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_older()
        throws Exception
    {

        final Context prodContext = ContextBuilder.create().
            workspace( WS_PROD ).
            repositoryId( TEST_REPO.getId() ).
            build();

        final Node createdNode = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        CTX_DEFAULT.runWith( () -> doPushNode( WS_PROD, createdNode ) );
        refresh();

        prodContext.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = CTX_DEFAULT.callWith( () -> doCompare( WS_PROD, createdNode ) );

        assertEquals( CompareStatus.Status.OLDER, comparison.getCompareStatus().getStatus() );
    }

    private NodeComparison doCompare( final Workspace workspace, final Node createdNode )
    {
        return CompareNodeCommand.create().
            queryService( this.queryService ).
            versionService( this.versionService ).
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
            build().
            execute();
    }

    private Node doPushNode( final Workspace workspace, final Node createdNode )
    {
        return PushNodeCommand.create().
            id( createdNode.id() ).
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
