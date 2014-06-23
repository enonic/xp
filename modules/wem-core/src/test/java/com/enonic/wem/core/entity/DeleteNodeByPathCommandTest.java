package com.enonic.wem.core.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.entity.dao.NodeDao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteNodeByPathCommandTest
{

    private NodeDao nodeDao;

    private ElasticsearchIndexService indexService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeDao = mock( NodeDao.class );

        this.indexService = mock( ElasticsearchIndexService.class );
    }

    @Test
    public void delete()
        throws Exception
    {
        final Node nodeToDelete = Node.newNode().
            id( EntityId.from( "parent-id" ) ).
            creator( UserKey.superUser() ).
            createdTime( Instant.now() ).
            name( NodeName.from( "mynode" ) ).
            parent( NodePath.ROOT ).
            build();

        final Node childNode = Node.newNode().
            id( EntityId.from( "child-id" ) ).
            creator( UserKey.superUser() ).
            createdTime( Instant.now() ).
            name( NodeName.from( "childnode" ) ).
            parent( nodeToDelete.path() ).
            build();

        setupMocks( nodeToDelete, childNode );

        final DeleteNodeByPathCommand deleteNode = DeleteNodeByPathCommand.create( new Context( Workspace.from( "test" ) ) ).
            nodePath( nodeToDelete.path() ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build();

        deleteNode.execute();

        verify( nodeDao ).deleteById( nodeToDelete.id(), Workspace.from( "test" ) );
        verify( indexService ).delete( nodeToDelete.id(), Workspace.from( "test" ) );
        verify( indexService ).delete( childNode.id(), Workspace.from( "test" ) );
    }

    private void setupMocks( final Node nodeToDelete, final Node childNode )
    {
        when( this.nodeDao.getByPath( nodeToDelete.path(), Workspace.from( "test" ) ) ).
            thenReturn( nodeToDelete );

        when( this.nodeDao.getByParent( nodeToDelete.path(), Workspace.from( "test" ) ) ).
            thenReturn( Nodes.from( childNode ) );

        when( this.nodeDao.getByParent( childNode.path(), Workspace.from( "test" ) ) ).
            thenReturn( Nodes.empty() );

        when( this.nodeDao.deleteById( nodeToDelete.id(), Workspace.from( "test" ) ) ).
            thenReturn( nodeToDelete );

        when( this.nodeDao.deleteById( childNode.id(), Workspace.from( "test" ) ) ).
            thenReturn( nodeToDelete );
    }
}
