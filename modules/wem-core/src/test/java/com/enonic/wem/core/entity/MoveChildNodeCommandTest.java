package com.enonic.wem.core.entity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.NodeQueryResultEntry;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceService;

import static com.enonic.wem.core.entity.NodeOrderValueResolver.ORDER_SPACE;
import static com.enonic.wem.core.entity.NodeOrderValueResolver.START_ORDER_VALUE;
import static org.junit.Assert.*;

public class MoveChildNodeCommandTest
{

    private NodeDao nodeDao;

    private VersionService versionService;

    private WorkspaceService workspaceService;

    private QueryService queryService;

    private IndexService indexService;

    @Before
    public void setUp()
        throws Exception
    {
        this.versionService = Mockito.mock( VersionService.class );
        this.workspaceService = Mockito.mock( WorkspaceService.class );
        this.nodeDao = Mockito.mock( NodeDao.class );
        this.queryService = Mockito.mock( QueryService.class );
        this.indexService = Mockito.mock( IndexService.class );
        this.workspaceService = Mockito.mock( WorkspaceService.class );
    }


    @Test(expected = java.lang.IllegalArgumentException.class)
    public void parent_not_ordered()
        throws Exception
    {
        mockStoringNode();

        Node parentNode = Node.newNode().
            id( NodeId.from( "parent-id" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "parent" ) ).
            childOrder( ChildOrder.from( "displayName desc" ) ).
            build();

        Node nodeAboveInsert = mockNode( "node-above-insert", parentNode.path().toString(), START_ORDER_VALUE + ORDER_SPACE * 2 );

        Node nodeToMoveBefore = mockNode( "node-to-move-before", parentNode.path().toString(), START_ORDER_VALUE + ORDER_SPACE );

        Node nodeToMove = mockNode( "node-to-move", parentNode.path().toString(), START_ORDER_VALUE );

        final Node updatedNode = MoveChildNodeCommand.create().
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            parentNode( parentNode ).
            nodeToMove( nodeToMove ).
            nodeToMoveBefore( nodeToMoveBefore ).
            build().
            execute();

        assertEquals( new Long( ( nodeAboveInsert.getManualOrderValue() + nodeToMoveBefore.getManualOrderValue() ) / 2 ),
                      updatedNode.getManualOrderValue() );
    }


    @Test
    public void insert_between()
        throws Exception
    {
        mockStoringNode();

        Node parentNode = Node.newNode().
            id( NodeId.from( "parent-id" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "parent" ) ).
            childOrder( ChildOrder.from( "manualOrderValue desc" ) ).
            build();

        Node nodeAboveInsert = mockNode( "node-above-insert", parentNode.path().toString(), START_ORDER_VALUE + ORDER_SPACE * 2 );

        Node nodeToMoveBefore = mockNode( "node-to-move-before", parentNode.path().toString(), START_ORDER_VALUE + ORDER_SPACE );

        Node nodeToMove = mockNode( "node-to-move", parentNode.path().toString(), START_ORDER_VALUE );

        Mockito.when( queryService.find( Mockito.isA( NodeQuery.class ), Mockito.isA( IndexContext.class ) ) ).
            thenReturn( NodeQueryResult.create().
                addEntry( new NodeQueryResultEntry( 1l, nodeAboveInsert.id().toString() ) ).
                build() );

        final Node updatedNode = MoveChildNodeCommand.create().
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            parentNode( parentNode ).
            nodeToMove( nodeToMove ).
            nodeToMoveBefore( nodeToMoveBefore ).
            build().
            execute();

        assertEquals( new Long( ( nodeAboveInsert.getManualOrderValue() + nodeToMoveBefore.getManualOrderValue() ) / 2 ),
                      updatedNode.getManualOrderValue() );
    }

    @Test
    public void insert_first()
        throws Exception
    {
        mockStoringNode();

        Node parentNode = Node.newNode().
            id( NodeId.from( "parent-id" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "parent" ) ).
            childOrder( ChildOrder.from( "manualOrderValue desc" ) ).
            build();

        Node nodeToMoveBefore = mockNode( "node-to-move-before", parentNode.path().toString(), START_ORDER_VALUE + ORDER_SPACE );

        Node nodeToMove = mockNode( "node-to-move", parentNode.path().toString(), START_ORDER_VALUE );

        Mockito.when( queryService.find( Mockito.isA( NodeQuery.class ), Mockito.isA( IndexContext.class ) ) ).
            thenReturn( NodeQueryResult.create().
                build() );

        final Node updatedNode = MoveChildNodeCommand.create().
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            parentNode( parentNode ).
            nodeToMove( nodeToMove ).
            nodeToMoveBefore( nodeToMoveBefore ).
            build().
            execute();

        assertEquals( new Long( START_ORDER_VALUE + 2 * ORDER_SPACE ), updatedNode.getManualOrderValue() );
    }


    @Test
    public void insert_last()
        throws Exception
    {
        mockStoringNode();

        Node parentNode = Node.newNode().
            id( NodeId.from( "parent-id" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "parent" ) ).
            childOrder( ChildOrder.from( "manualOrderValue desc" ) ).
            build();

        Node nodeAboveInsert = mockNode( "node-above-insert", parentNode.path().toString(), START_ORDER_VALUE );

        Node nodeToMove = mockNode( "node-to-move", parentNode.path().toString(),
                                    NodeOrderValueResolver.START_ORDER_VALUE + NodeOrderValueResolver.ORDER_SPACE );

        Mockito.when( queryService.find( Mockito.isA( NodeQuery.class ), Mockito.isA( IndexContext.class ) ) ).
            thenReturn( NodeQueryResult.create().
                addEntry( new NodeQueryResultEntry( 1l, nodeAboveInsert.id().toString() ) ).
                build() );

        final Node updatedNode = MoveChildNodeCommand.create().
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            queryService( this.queryService ).
            workspaceService( this.workspaceService ).
            parentNode( parentNode ).
            nodeToMove( nodeToMove ).
            nodeToMoveBefore( null ).
            build().
            execute();

        assertEquals( new Long( NodeOrderValueResolver.START_ORDER_VALUE - NodeOrderValueResolver.ORDER_SPACE ),
                      updatedNode.getManualOrderValue() );
    }

    private void mockStoringNode()
    {
        Mockito.when( nodeDao.store( Mockito.isA( Node.class ) ) ).
            thenReturn( NodeVersionId.from( "a" ) );
    }

    private Node mockNode( final String baseName, final String parentPath, final long orderValue )
    {
        final NodeId nodeId = NodeId.from( baseName + "-id" );

        final NodeVersionId nodeVersionId = NodeVersionId.from( baseName + "-version" );

        Node node = Node.newNode().
            id( nodeId ).
            name( NodeName.from( baseName ) ).
            parent( NodePath.newPath( parentPath ).build() ).
            manualOrderValue( orderValue ).
            build();

        Mockito.when( this.workspaceService.getCurrentVersion( Mockito.eq( nodeId ), Mockito.isA( WorkspaceContext.class ) ) ).
            thenReturn( nodeVersionId );

        Mockito.when( this.nodeDao.getByVersionId( nodeVersionId ) ).
            thenReturn( node );

        return node;
    }

}