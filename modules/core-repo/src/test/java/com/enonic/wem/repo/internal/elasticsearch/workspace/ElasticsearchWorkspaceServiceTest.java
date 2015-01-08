package com.enonic.wem.repo.internal.elasticsearch.workspace;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.WorkspaceDiffResult;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.compare.query.WorkspaceDiffQuery;

import static org.junit.Assert.*;

public class ElasticsearchWorkspaceServiceTest
    extends AbstractNodeTest
{
    private final static Workspace STAGE = ContentConstants.WORKSPACE_STAGE;

    private final static Workspace PROD = ContentConstants.WORKSPACE_PROD;

    @Test
    public void only_in_stage()
        throws Exception
    {
        storeInWorkspace( "1", "a", STAGE );
        storeInWorkspace( "2", "b", STAGE );
        storeInWorkspace( "3", "c", STAGE );

        final WorkspaceDiffResult result = getDiff( STAGE, PROD );

        assertEquals( 3, result.getNodesWithDifferences().getSize() );
    }

    @Test
    public void in_stage_and_prod()
        throws Exception
    {
        storeInWorkspace( "1", "a", STAGE );
        storeInWorkspace( "1", "a", PROD );
        storeInWorkspace( "2", "b", STAGE );
        storeInWorkspace( "2", "b", PROD );
        storeInWorkspace( "3", "c", STAGE );
        storeInWorkspace( "3", "c", PROD );

        final WorkspaceDiffResult result = getDiff( STAGE, PROD );

        assertEquals( 0, result.getNodesWithDifferences().getSize() );
    }

    @Test
    public void prod_only()
        throws Exception
    {
        storeInWorkspace( "1", "a", PROD );
        storeInWorkspace( "2", "b", PROD );
        storeInWorkspace( "3", "c", PROD );

        final WorkspaceDiffResult result = getDiff( STAGE, PROD );

        assertEquals( 3, result.getNodesWithDifferences().getSize() );
    }

    @Test
    public void different_versions()
        throws Exception
    {
        storeInWorkspace( "1", "a", STAGE );
        storeInWorkspace( "1", "b", PROD );

        final WorkspaceDiffResult result = getDiff( STAGE, PROD );

        assertEquals( 1, result.getNodesWithDifferences().getSize() );
    }

    @Test
    public void update()
        throws Exception
    {
        storeInWorkspace( "1", "a", STAGE );
        storeInWorkspace( "1", "a", PROD );

        assertEquals( 0, getDiff( STAGE, PROD ).getNodesWithDifferences().getSize() );

        storeInWorkspace( "1", "b", STAGE );

        assertEquals( 1, getDiff( STAGE, PROD ).getNodesWithDifferences().getSize() );

        storeInWorkspace( "1", "b", PROD );

        assertEquals( 0, getDiff( STAGE, PROD ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void mix()
        throws Exception
    {
        storeInWorkspace( "1", "a", STAGE );
        storeInWorkspace( "1", "b", PROD );
        storeInWorkspace( "2", "c", STAGE );
        storeInWorkspace( "2", "c", PROD );
        storeInWorkspace( "3", "d", STAGE );
        storeInWorkspace( "3", "e", PROD );

        final WorkspaceDiffResult result = getDiff( STAGE, PROD );

        final Iterator<NodeId> resultIterator = result.getNodesWithDifferences().iterator();

        assertEquals( 2, result.getNodesWithDifferences().getSize() );
        assertEquals( "1", resultIterator.next().toString() );
        assertEquals( "3", resultIterator.next().toString() );
    }

    private WorkspaceDiffResult getDiff( final Workspace source, final Workspace target )
    {
        return this.workspaceService.diff( WorkspaceDiffQuery.create().
            target( target ).
            source( source ).
            build(), WorkspaceContext.from( ContentConstants.WORKSPACE_STAGE, ContentConstants.CONTENT_REPO.getId() ) );
    }

    private void storeInWorkspace( final String nodeId, final String nodeVersionId, final Workspace workspace )
    {
        this.workspaceService.store( StoreWorkspaceDocument.create().
            node( Node.newNode().
                id( NodeId.from( nodeId ) ).
                name( "my-node" ).
                nodeType( NodeType.from( "content" ) ).
                parent( NodePath.ROOT ).
                build() ).
            nodeVersionId( NodeVersionId.from( new BlobKey( nodeVersionId ) ) ).
            build(), WorkspaceContext.from( workspace, ContentConstants.CONTENT_REPO.getId() ) );
    }

    private void printIndexContent()
    {
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( ContentConstants.CONTENT_REPO.getId() ), "node" );
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( ContentConstants.CONTENT_REPO.getId() ), "workspace" );

    }
}