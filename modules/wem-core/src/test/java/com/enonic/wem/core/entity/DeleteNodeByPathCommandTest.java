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
import com.enonic.wem.core.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.elastic.ElasticsearchNodeDao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteNodeByPathCommandTest
{

    private ElasticsearchNodeDao elasticsearchNodeDao;

    private ElasticsearchIndexService indexService;

    @Before
    public void setUp()
        throws Exception
    {
        this.elasticsearchNodeDao = mock( ElasticsearchNodeDao.class );

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
            nodeDao( this.elasticsearchNodeDao ).
            indexService( this.indexService ).
            build();

        deleteNode.execute();

        verify( elasticsearchNodeDao ).deleteByPath( nodeToDelete.path() );
        verify( indexService ).delete( nodeToDelete.id() );
        verify( indexService ).delete( childNode.id() );
    }

    private void setupMocks( final Node nodeToDelete, final Node childNode )
    {
        when( this.elasticsearchNodeDao.getByPath( nodeToDelete.path() ) ).
            thenReturn( nodeToDelete );

        when( this.elasticsearchNodeDao.getByParent( nodeToDelete.path() ) ).
            thenReturn( Nodes.from( childNode ) );

        when( this.elasticsearchNodeDao.getByParent( childNode.path() ) ).
            thenReturn( Nodes.empty() );

        when( this.elasticsearchNodeDao.deleteById( nodeToDelete.id() ) ).
            thenReturn( nodeToDelete );

        when( this.elasticsearchNodeDao.deleteById( childNode.id() ) ).
            thenReturn( nodeToDelete );
    }
}
