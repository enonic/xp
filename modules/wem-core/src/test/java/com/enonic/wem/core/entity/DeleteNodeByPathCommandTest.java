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
import com.enonic.wem.core.entity.dao.NodeElasticsearchDao;
import com.enonic.wem.core.index.IndexService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteNodeByPathCommandTest
{

    private NodeElasticsearchDao nodeElasticsearchDao;

    private IndexService indexService;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeElasticsearchDao = mock( NodeElasticsearchDao.class );

        this.indexService = mock( IndexService.class );
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
            nodeElasticsearchDao( this.nodeElasticsearchDao ).
            indexService( this.indexService ).
            build();

        deleteNode.execute();

        verify( nodeElasticsearchDao ).deleteByPath( nodeToDelete.path() );
        verify( indexService ).deleteEntity( nodeToDelete.id() );
        verify( indexService ).deleteEntity( childNode.id() );
    }

    private void setupMocks( final Node nodeToDelete, final Node childNode )
    {
        when( this.nodeElasticsearchDao.getByPath( nodeToDelete.path() ) ).
            thenReturn( nodeToDelete );

        when( this.nodeElasticsearchDao.getByParent( nodeToDelete.path() ) ).
            thenReturn( Nodes.from( childNode ) );

        when( this.nodeElasticsearchDao.getByParent( childNode.path() ) ).
            thenReturn( Nodes.empty() );

        when( this.nodeElasticsearchDao.deleteById( nodeToDelete.id() ) ).
            thenReturn( nodeToDelete );

        when( this.nodeElasticsearchDao.deleteById( childNode.id() ) ).
            thenReturn( nodeToDelete );
    }
}
