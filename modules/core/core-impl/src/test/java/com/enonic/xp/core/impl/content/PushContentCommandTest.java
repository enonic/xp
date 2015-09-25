package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class PushContentCommandTest
{
    private NodeService nodeService;

    private ContentTypeService contentTypeService;

    private EventPublisher eventPublisher;

    private ContentNodeTranslator translator;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.translator = Mockito.mock( ContentNodeTranslator.class );
    }

    @Test
    public void no_outside_requested()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishRequested( NodeId.from( "s2" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( translator.fromNodes( Mockito.isA( Nodes.class ), Mockito.anyBoolean() ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.push( Mockito.any(), Mockito.any() ) ).thenReturn( PushNodesResult.create().
            addSuccess( createNode( "s1", "s1Name", "/content" ) ).
            addSuccess( createNode( "s2", "s2Name", "/content" ) ).
            build() );

        final PushContentsResult result = PushContentCommand.create().
            contentIds( ContentIds.from( ContentId.from( "s1" ), ContentId.from( "s2" ) ) ).
            resolveDependencies( true ).
            strategy( PushContentCommand.PushContentStrategy.STRICT ).
            target( ContentConstants.BRANCH_MASTER ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 2, result.getPushedContent().getSize() );
    }

    @Test
    public void outside_requested_strict()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishReferredFrom( NodeId.from( "s1" ), NodeId.from( "s2" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( translator.fromNodes( Mockito.isA( Nodes.class ), Mockito.isA( boolean.class ) ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.push( Mockito.any(), Mockito.any() ) ).thenReturn( PushNodesResult.create().
            addSuccess( createNode( "s1", "s1Name", "/content" ) ).
            addSuccess( createNode( "s2", "s2Name", "/content" ) ).
            build() );

        final PushContentsResult result = PushContentCommand.create().
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            resolveDependencies( true ).
            strategy( PushContentCommand.PushContentStrategy.STRICT ).
            target( ContentConstants.BRANCH_MASTER ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 0, result.getPushedContent().getSize() );
        assertEquals( 1, result.getPushContentRequests().getPushBecauseRequested().size() );
        assertEquals( 1, result.getPushContentRequests().getPushedBecauseReferredTos().size() );
    }

    @Test
    public void outside_requested_lenient()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishReferredFrom( NodeId.from( "s1" ), NodeId.from( "s2" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( translator.fromNodes( Mockito.isA( Nodes.class ), Mockito.anyBoolean() ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.push( Mockito.any(), Mockito.any() ) ).thenReturn( PushNodesResult.create().
            addSuccess( createNode( "s1", "s1Name", "/content" ) ).
            addSuccess( createNode( "s2", "s2Name", "/content" ) ).
            build() );

        final PushContentsResult result = PushContentCommand.create().
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            resolveDependencies( true ).
            strategy( PushContentCommand.PushContentStrategy.ALLOW_PUBLISH_OUTSIDE_SELECTION ).
            target( ContentConstants.BRANCH_MASTER ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 2, result.getPushedContent().getSize() );
        assertEquals( 1, result.getPushContentRequests().getPushBecauseRequested().size() );
        assertEquals( 1, result.getPushContentRequests().getPushedBecauseReferredTos().size() );
    }

    @Ignore
    @Test
    public void pending_deleted()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                deleteRequested( NodeId.from( "s3" ) ).
                deleteRequested( NodeId.from( "s4" ) ).
                setInitialReasonNodeId( NodeId.from( "s3" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( translator.fromNodes( Mockito.isA( Nodes.class ), Mockito.anyBoolean() ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.push( Mockito.any(), Mockito.any() ) ).thenReturn( PushNodesResult.create().
            addSuccess( createNode( "s1", "s1Name", "/content" ) ).
            addSuccess( createNode( "s2", "s2Name", "/content" ) ).
            build() );

        final PushContentsResult result = PushContentCommand.create().
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            resolveDependencies( true ).
            strategy( PushContentCommand.PushContentStrategy.ALLOW_PUBLISH_OUTSIDE_SELECTION ).
            target( ContentConstants.BRANCH_MASTER ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 2, result.getPushedContent().getSize() );
        assertEquals( 2, result.getDeleted().getSize() );
        assertEquals( 0, result.getPushContentRequests().getPushBecauseRequested().size() );
        assertEquals( 0, result.getPushContentRequests().getPushedBecauseReferredTos().size() );
    }


    @Test
    public void contains_invalid()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishReferredFrom( NodeId.from( "s1" ), NodeId.from( "s2" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        final Content validContent = createContent( "s1", "s1Name", ContentPath.ROOT, true );
        final Content invalidContent = createContent( "s2", "s2Name", ContentPath.ROOT, false );
        final Contents contents = Contents.from( validContent, invalidContent );

        Mockito.when( translator.fromNodes( Mockito.isA( Nodes.class ), Mockito.anyBoolean() ) ).
            thenReturn( contents );

        final PushContentsResult result = PushContentCommand.create().
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            resolveDependencies( true ).
            strategy( PushContentCommand.PushContentStrategy.ALLOW_PUBLISH_OUTSIDE_SELECTION ).
            target( ContentConstants.BRANCH_MASTER ).
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.translator ).
            eventPublisher( this.eventPublisher ).
            build().
            execute();

        assertEquals( 0, result.getPushedContent().getSize() );
        assertEquals( 1, result.getFailed().size() );
    }


    private Node createNode( final String id, final String name, final String path )
    {
        return Node.create().
            id( NodeId.from( id ) ).
            name( name ).
            parentPath( NodePath.create( path ).build() ).
            build();
    }

    private Content createContent( final String id, final String name, final ContentPath path, boolean valid )
    {
        return Content.create().
            id( ContentId.from( id ) ).
            name( name ).
            parentPath( path ).
            valid( valid ).
            build();
    }
}