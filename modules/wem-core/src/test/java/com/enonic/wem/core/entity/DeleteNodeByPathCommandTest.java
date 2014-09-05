package com.enonic.wem.core.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DeleteNodeByPathCommandTest
{

    private NodeDao nodeDao;

    private ElasticsearchIndexService indexService;

    private WorkspaceService workspaceService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeDao = mock( NodeDao.class );
        this.workspaceService = mock( WorkspaceService.class );
        this.indexService = mock( ElasticsearchIndexService.class );
    }

    @Test
    public void delete()
        throws Exception
    {
        final Workspace workspace = Workspace.from( "test" );

        final EntityId parentId = EntityId.from( "parent-id" );
        final Node nodeToDelete = Node.newNode().
            id( parentId ).
            creator( UserKey.superUser() ).
            createdTime( Instant.now() ).
            name( NodeName.from( "mynode" ) ).
            parent( NodePath.ROOT ).
            build();

        final EntityId childId = EntityId.from( "child-id" );
        final Node childNode = Node.newNode().
            id( childId ).
            creator( UserKey.superUser() ).
            createdTime( Instant.now() ).
            name( NodeName.from( "childnode" ) ).
            parent( nodeToDelete.path() ).
            build();

        //setupMocks( nodeToDelete, childNode );

        final NodeVersionId parentVersionId = NodeVersionId.from( "parent-node-version" );
        final NodeVersionId childVersionId = NodeVersionId.from( "child-node-version" );

        // Mock fetching of nodeToDelete
        Mockito.when( this.workspaceService.getByPath( new WorkspacePathQuery( workspace, nodeToDelete.path() ) ) ).
            thenReturn( parentVersionId );
        Mockito.when( this.nodeDao.getByVersionId( parentVersionId ) ).
            thenReturn( nodeToDelete );

        // Mock fething children of nodeToDelete
        final NodeVersionIds childVersions = NodeVersionIds.create().
            add( childVersionId ).
            build();
        Mockito.when( this.workspaceService.findByParent( new WorkspaceParentQuery( workspace, nodeToDelete.path() ) ) ).
            thenReturn( childVersions );
        Mockito.when( this.nodeDao.getByVersionIds( childVersions ) ).
            thenReturn( Nodes.from( childNode ) );

        // Mock empty result from child of nodeToDelete getByParent
        Mockito.when( this.workspaceService.findByParent( new WorkspaceParentQuery( workspace, childNode.path() ) ) ).
            thenReturn( NodeVersionIds.empty() );

        // Exercise
        final DeleteNodeByPathCommand deleteNode = DeleteNodeByPathCommand.create( new Context( workspace ) ).
            nodePath( nodeToDelete.path() ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            workspaceService( this.workspaceService ).
            build();

        deleteNode.execute();

        verify( workspaceService ).delete( new WorkspaceDeleteQuery( workspace, nodeToDelete.id() ) );
        verify( workspaceService ).delete( new WorkspaceDeleteQuery( workspace, childNode.id() ) );
        verify( indexService ).delete( nodeToDelete.id(), workspace );
        verify( indexService ).delete( childNode.id(), workspace );
    }

}
