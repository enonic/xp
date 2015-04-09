package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeState;

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

        final Node node = Node.newNode().
            id( id ).
            name( "myContent" ).
            parentPath( NodePath.ROOT ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( id, CompareStatus.NEW ) );

        Mockito.when( this.nodeService.deleteByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        DeleteContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "myContent" ) ).
                build() ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).deleteByPath( Mockito.isA( NodePath.class ) );
    }

    @Test
    public void delete_published()
        throws Exception
    {
        final NodeId id = NodeId.from( "test" );

        final Node node = Node.newNode().
            id( id ).
            name( "myContent" ).
            parentPath( NodePath.ROOT ).
            build();

        Mockito.when( this.nodeService.getByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        Mockito.when( this.nodeService.compare( Mockito.isA( NodeId.class ), Mockito.isA( Branch.class ) ) ).
            thenReturn( new NodeComparison( id, CompareStatus.EQUAL ) );

        Mockito.when( this.nodeService.deleteByPath( Mockito.isA( NodePath.class ) ) ).
            thenReturn( node );

        DeleteContentCommand.create().
            params( DeleteContentParams.create().
                contentPath( ContentPath.from( "myContent" ) ).
                build() ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).setNodeState( Mockito.eq( node.id() ),
                                                                             Mockito.eq( NodeState.PENDING_DELETE ) );
    }

}