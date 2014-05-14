package com.enonic.wem.core.entity;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
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

    public static final Workspace TEST_WORKSPACE = new Workspace( "test" );

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
            createdTime( DateTime.now() ).
            name( NodeName.from( "mynode" ) ).
            parent( NodePath.ROOT ).
            build();

        final Node childNode = Node.newNode().
            id( EntityId.from( "child-id" ) ).
            creator( UserKey.superUser() ).
            createdTime( DateTime.now() ).
            name( NodeName.from( "childnode" ) ).
            parent( nodeToDelete.path() ).
            build();

        setupMocks( nodeToDelete, childNode );

        final DeleteNodeByPathCommand deleteNode = DeleteNodeByPathCommand.create().
            nodePath( nodeToDelete.path() ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            workspace( TEST_WORKSPACE ).
            build();

        deleteNode.execute();

        verify( nodeDao ).deleteByPath( nodeToDelete.path(), TEST_WORKSPACE );
        verify( indexService ).delete( nodeToDelete.id() );
        verify( indexService ).delete( childNode.id() );
    }

    private void setupMocks( final Node nodeToDelete, final Node childNode )
    {
        when( this.nodeDao.getByPath( nodeToDelete.path(), TEST_WORKSPACE ) ).
            thenReturn( nodeToDelete );

        when( this.nodeDao.getByParent( nodeToDelete.path(), TEST_WORKSPACE ) ).
            thenReturn( Nodes.from( childNode ) );

        when( this.nodeDao.getByParent( childNode.path(), TEST_WORKSPACE ) ).
            thenReturn( Nodes.empty() );

        when( this.nodeDao.deleteById( nodeToDelete.id(), TEST_WORKSPACE ) ).
            thenReturn( nodeToDelete );

        when( this.nodeDao.deleteById( childNode.id(), TEST_WORKSPACE ) ).
            thenReturn( nodeToDelete );
    }
}
