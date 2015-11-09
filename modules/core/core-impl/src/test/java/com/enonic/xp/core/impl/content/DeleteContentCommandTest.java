package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;

import static org.junit.Assert.*;

public class DeleteContentCommandTest
{
    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private EventPublisher eventPublisher;


    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.translator = Mockito.mock( ContentNodeTranslator.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void delete_not_published()
        throws Exception
    {
        final NodeId id = NodeId.from( "test" );

        final Node node = Node.create().
            id( id ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( id, CompareStatus.NEW ) );

        Mockito.when( this.nodeService.deleteById( node.id() ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.setNodeState( Mockito.isA( SetNodeStateParams.class ) ) ).
            thenReturn( SetNodeStateResult.
                create().
                addUpdatedNode( node ).
                build() );

        Mockito.when( this.nodeService.findByQuery( Mockito.isA( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );

        Mockito.when( this.translator.fromNodes( Nodes.from( node ), false ) ).thenReturn( Contents.create().
            add( Content.create().
                id( ContentId.from( "test" ) ).
                name( "test" ).
                parentPath( ContentPath.ROOT ).
                build() ).
            build() );

        final Contents myContent = DeleteContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "myContent" ) ).
                build() ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 1, myContent.getSize() );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).deleteById( node.id() );
    }

    @Test
    public void delete_published()
        throws Exception
    {
        final NodeId id = NodeId.from( "test" );

        final Node node = Node.create().
            id( id ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final SetNodeStateResult setNodeStateResult = SetNodeStateResult.
            create().
            addUpdatedNode( node ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.findByParent( Mockito.isA( FindNodesByParentParams.class ) ) ).
            thenReturn( FindNodesByParentResult.create().
                hits( 0 ).
                nodes( Nodes.empty() ).
                totalHits( 0 ).
                build() );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( id, CompareStatus.EQUAL ) );

        Mockito.when( this.nodeService.deleteByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.setNodeState( Mockito.isA( SetNodeStateParams.class ) ) ).
            thenReturn( setNodeStateResult );

        Mockito.when( this.translator.fromNodes( Nodes.from( node ), false ) ).thenReturn( Contents.create().
            add( Content.create().
                id( ContentId.from( "test" ) ).
                name( "test" ).
                parentPath( ContentPath.ROOT ).
                build() ).
            build() );

        final Contents myContent = DeleteContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "myContent" ) ).
                build() ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            build().
            execute();

        assertEquals( 1, myContent.getSize() );

        final SetNodeStateParams params = SetNodeStateParams.create().
            nodeId( node.id() ).
            nodeState( NodeState.PENDING_DELETE ).
            build();

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).setNodeState( Mockito.eq( params ) );

    }

}