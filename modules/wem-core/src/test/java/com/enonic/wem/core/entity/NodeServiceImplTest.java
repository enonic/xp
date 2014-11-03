package com.enonic.wem.core.entity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

import static org.junit.Assert.*;

public class NodeServiceImplTest
{

    private NodeServiceImpl nodeService;

    private final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    private final IndexService indexService = Mockito.mock( IndexService.class );

    private final NodeDao nodeDao = Mockito.mock( NodeDao.class );

    private final VersionService versionService = Mockito.mock( VersionService.class );

    private final QueryService queryService = Mockito.mock( QueryService.class );


    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexService( this.indexService );
        this.nodeService.setWorkspaceService( this.workspaceService );
        this.nodeService.setNodeDao( this.nodeDao );
        this.nodeService.setQueryService( this.queryService );
        this.nodeService.setVersionService( this.versionService );
    }

    @Test
    public void get_by_id_resolve_has_child()
        throws Exception
    {
        final NodeId id = NodeId.from( "id" );

        final NodeVersionId versionId = NodeVersionId.from( "versionId" );

        final Node node = Node.newNode().
            id( id ).
            creator( UserKey.superUser() ).
            name( NodeName.from( "nodename" ) ).
            parent( NodePath.ROOT ).
            build();

        Mockito.when( this.workspaceService.getCurrentVersion( Mockito.eq( id ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( versionId );

        Mockito.when( this.nodeDao.getByVersionId( versionId ) ).
            thenReturn( node );

        Mockito.when( this.workspaceService.hasChildren( Mockito.eq( node.path() ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( true );

        final Node resultNode = this.nodeService.getById( id );

        assertTrue( resultNode.getHasChildren() );
    }

    @Test
    public void get_by_ids_resolve_has_child()
        throws Exception
    {
        final NodeId a = NodeId.from( "a" );
        final NodeId b = NodeId.from( "b" );
        final NodeVersionId versionA = NodeVersionId.from( "version-a" );
        final NodeVersionId versionB = NodeVersionId.from( "version-b" );
        final Node nodeA = Node.newNode().
            id( a ).
            creator( UserKey.superUser() ).
            name( NodeName.from( "node-a" ) ).
            parent( NodePath.ROOT ).
            build();
        final Node nodeB = Node.newNode().
            id( b ).
            creator( UserKey.superUser() ).
            name( NodeName.from( "node-b" ) ).
            parent( NodePath.ROOT ).
            build();

        final Nodes nodes = Nodes.from( nodeA, nodeB );

        final NodeIds ids = NodeIds.from( a, b );

        final NodeVersionIds nodeVersionIds = NodeVersionIds.from( versionA, versionB );

        Mockito.when( this.workspaceService.getByVersionIds( Mockito.eq( ids ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( nodeVersionIds );

        Mockito.when( this.nodeDao.getByVersionIds( nodeVersionIds ) ).
            thenReturn( nodes );

        Mockito.when( this.workspaceService.hasChildren( Mockito.eq( nodeA.path() ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( true );

        Mockito.when( this.workspaceService.hasChildren( Mockito.eq( nodeB.path() ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( false );

        final Nodes resultNodes = this.nodeService.getByIds( ids );

        assertEquals( 2, resultNodes.getSize() );

        assertTrue( resultNodes.getNodeById( a ).getHasChildren() );
        assertFalse( resultNodes.getNodeById( b ).getHasChildren() );
    }

    @Ignore
    @Test
    public void duplicate()
        throws Exception
    {
        Attachment a1 = new Attachment.Builder().name( "a" ).size( 111 ).mimeType( "text/html" ).build();
        Attachment a2 = new Attachment.Builder().name( "b" ).size( 222 ).mimeType( "img/png" ).build();

        final Node nodeA = Node.newNode().
            id( NodeId.from( "node-1" ) ).
            creator( UserKey.superUser() ).
            attachments( Attachments.from( a1, a2 ) ).
            childOrder( ChildOrder.from( "random" ) ).
            name( NodeName.from( "node-a" ) ).
            parent( NodePath.ROOT ).
            build();

        NodeVersionId nodeAVersionId = NodeVersionId.from( "a-version-id" );

        Mockito.when(
            this.workspaceService.getCurrentVersion( Mockito.eq( nodeA.id() ), Mockito.isA( WorkspaceContext.class ) ) ).thenReturn(
            nodeAVersionId );

        Mockito.when( this.nodeDao.getByVersionId( nodeAVersionId ) ).thenReturn( nodeA );

        Mockito.when( this.workspaceService.hasChildren( Mockito.eq( nodeA.path() ), Mockito.isA( WorkspaceContext.class ) ) ).thenReturn(
            false );

        Mockito.when( this.nodeDao.store( Mockito.isA( Node.class ) ) ).thenReturn( NodeVersionId.from( "b-version-id" ) );

        final Node nodeB = this.nodeService.duplicate( nodeA.id() );

        assertEquals( nodeA.name() + "-copy", nodeB.name().toString() );
        assertEquals( nodeA.attachments(), nodeB.attachments() );
        assertEquals( nodeA.data(), nodeB.data() );
        assertEquals( nodeA.getChildOrder(), nodeB.getChildOrder() );
        assertEquals( nodeA.parent(), nodeB.parent() );
    }

}