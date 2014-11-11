package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.CompareNodeCommand;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeComparison;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.PushNodeCommand;
import com.enonic.wem.core.entity.UpdateNodeCommand;
import com.enonic.wem.core.entity.UpdateNodeParams;

import static org.junit.Assert.*;

public class CompareNodesCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        createContentRepository();
    }

    @Test
    public void status_new()
        throws Exception
    {
        Context stage = ContentConstants.CONTEXT_STAGE;

        final Node createdNode = stage.runWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = stage.runWith( () -> doCompare( ContentConstants.WORKSPACE_PROD, createdNode ) );

        assertEquals( CompareStatus.Status.NEW, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_equal()
        throws Exception
    {
        final Context stage = ContentConstants.CONTEXT_STAGE;
        final Workspace prod = Workspace.from( "prod" );

        final Node createdNode = stage.runWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        stage.runWith( () -> doPushNode( prod, createdNode ) );

        final NodeComparison comparison = stage.runWith( () -> doCompare( prod, createdNode ) );

        assertEquals( CompareStatus.Status.EQUAL, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_newer()
        throws Exception
    {
        final Context stage = ContentConstants.CONTEXT_STAGE;
        final Workspace prodWs = ContentConstants.WORKSPACE_PROD;

        final Node createdNode = stage.runWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        stage.runWith( () -> doPushNode( prodWs, createdNode ) );
        refresh();

        stage.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        // printAllIndexContent( StorageNameResolver.resolveStorageIndexName( ContentConstants.CONTENT_REPO.getId() ),
        //                       IndexType.VERSION.getName() );

        final NodeComparison comparison = stage.runWith( () -> doCompare( prodWs, createdNode ) );

        assertEquals( CompareStatus.Status.NEWER, comparison.getCompareStatus().getStatus() );
    }

    @Test
    public void status_older()
        throws Exception
    {
        final Context stageContext = ContentConstants.CONTEXT_STAGE;
        final Workspace prodWs = ContentConstants.WORKSPACE_PROD;

        final Context prodContext = ContextBuilder.create().
            workspace( prodWs ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            build();

        final Node createdNode = stageContext.runWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        stageContext.runWith( () -> doPushNode( prodWs, createdNode ) );
        refresh();

        prodContext.runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = stageContext.runWith( () -> doCompare( prodWs, createdNode ) );

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
        return UpdateNodeCommand.create().
            params( new UpdateNodeParams().editor( toBeEdited -> Node.editNode( toBeEdited ).
                manualOrderValue( 10l ) ).
                id( createdNode.id() ) ).
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
