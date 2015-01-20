package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class NodeVersionDiffCommandTest
    extends AbstractNodeTest
{
    @Test
    public void only_in_source()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doPushNode( WS_OTHER, node );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void same_version()
        throws Exception
    {
        final Node node = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        doPushNode( WS_OTHER, node );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void newer_in_source()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );

        doUpdateNode( node );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void newer_in_target()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );

        CTX_OTHER.runWith( () -> doUpdateNode( node ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void moved_in_source()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doMoveNode( node, node2 );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void moved_in_target()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        CTX_OTHER.runWith( () -> doMoveNode( node, node2 ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void deleted_in_source()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doDeleteNode( node.id() );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        //assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void deleted_in_target()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        CTX_OTHER.runWith( () -> doDeleteNode( node.id() ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    private void doMoveNode( final Node node, final Node newParent )
    {
        MoveNodeCommand.create().
            newParent( newParent.path() ).
            id( node.id() ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

    private NodeVersionDiffResult getDiff( final Workspace source, final Workspace target )
    {
        return NodeVersionDiffCommand.create().
            versionService( this.versionService ).
            query( NodeVersionDiffQuery.create().
                target( target ).
                source( source ).
                build() ).
            build().
            execute();
    }

    private Node doUpdateNode( final Node node )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
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