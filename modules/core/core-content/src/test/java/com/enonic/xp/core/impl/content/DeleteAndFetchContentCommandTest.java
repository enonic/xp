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
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class DeleteAndFetchContentCommandTest
{
    private NodeService nodeService;

    private ContentNodeTranslator translator;

    private EventPublisher eventPublisher;

    private ContentTypeService contentTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
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

        final Content content = Content.create().
            id( ContentId.from( "test" ) ).
            name( "test" ).
            parentPath( ContentPath.ROOT ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.findByParent( Mockito.isA( FindNodesByParentParams.class ) ) ).
            thenReturn( FindNodesByParentResult.create().
                hits( 0 ).
                nodeIds( NodeIds.empty() ).
                totalHits( 0 ).
                build() );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( createTarget( id ), createTarget( id ), CompareStatus.NEW ) );

        Mockito.when( this.nodeService.deleteById( node.id() ) ).
            thenReturn( NodeIds.from( id ) );

        Mockito.when( this.nodeService.setNodeState( Mockito.isA( SetNodeStateParams.class ) ) ).
            thenReturn( SetNodeStateResult.
                create().
                addUpdatedNode( node ).
                build() );

        Mockito.when( this.translator.fromNode( node, true ) ).thenReturn( content );

        final Contents result = DeleteAndFetchContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "/myContent" ) ).
                build() ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 1, result.getSize() );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).deleteById( node.id() );
    }

    @Test
    public void delete_online_content_instantly()
        throws Exception
    {
        final NodeId id = NodeId.from( "test" );

        final Node node = Node.create().
            id( id ).
            name( "myContent" ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            build();

        final Content content = Content.create().
            id( ContentId.from( "test" ) ).
            name( "test" ).
            parentPath( ContentPath.ROOT ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.findByParent( Mockito.isA( FindNodesByParentParams.class ) ) ).
            thenReturn( FindNodesByParentResult.create().
                hits( 0 ).
                nodeIds( NodeIds.empty() ).
                totalHits( 0 ).
                build() );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( createTarget( id ), createTarget( id ), CompareStatus.EQUAL ) );

        Mockito.when( this.nodeService.deleteByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( NodeIds.from( id ) );

        Mockito.when( this.nodeService.deleteById( node.id() ) ).
            thenReturn( NodeIds.from( id ) );

        Mockito.when( this.translator.fromNode( node, true ) ).thenReturn( content );

        final Contents result = DeleteAndFetchContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "/myContent" ) ).
                deleteOnline( true ).
                build() ).
            contentTypeService( this.contentTypeService ).
            nodeService( this.nodeService ).
            eventPublisher( this.eventPublisher ).
            translator( this.translator ).
            build().
            execute();

        assertEquals( 1, result.getSize() );
        Mockito.verify( this.nodeService, Mockito.times( 2 ) ).deleteById( node.id() );
    }

    private NodeBranchEntry createTarget( final NodeId id )
    {
        return NodeBranchEntry.create().
            nodeId( id ).
            nodePath( NodePath.ROOT ).
            nodeState( NodeState.DEFAULT ).
            nodeVersionId( NodeVersionId.from( "1" ) ).
            build();
    }

}