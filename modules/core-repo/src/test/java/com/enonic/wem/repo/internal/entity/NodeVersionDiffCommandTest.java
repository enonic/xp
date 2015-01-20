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
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;

import static org.junit.Assert.*;

public class NodeVersionDiffCommandTest
    extends AbstractNodeTest
{
    @Test
    public void only_in_source()
        throws Exception
    {
        final Node node = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        CTX_DEFAULT.runWith( () -> doPushNode( WS_OTHER, node ) );

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

        CTX_DEFAULT.runWith( () -> doPushNode( WS_OTHER, node ) );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void newer_in_source()
        throws Exception
    {
        final Node node = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        CTX_DEFAULT.runWith( () -> doPushNode( WS_OTHER, node ) );

        CTX_DEFAULT.runWith( () -> doUpdateNode( node ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void newer_in_target()
        throws Exception
    {
        final Node node = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        CTX_DEFAULT.runWith( () -> doPushNode( WS_OTHER, node ) );

        CTX_OTHER.runWith( () -> doUpdateNode( node ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    private NodeVersionDiffResult getDiff( final Workspace source, final Workspace target )
    {
        return this.versionService.diff( NodeVersionDiffQuery.create().
            target( target ).
            source( source ).
            build(), TEST_REPO.getId() );
    }

    private void printIndexContent()
    {
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( TEST_REPO.getId() ), IndexType.VERSION.getName() );
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( TEST_REPO.getId() ), IndexType.WORKSPACE.getName() );
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