package com.enonic.xp.core.impl.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class ResolvePublishDependenciesCommandTest
{
    private NodeService nodeService;

    private ContentTypeService contentTypeService;

    private ContentNodeTranslator contentNodeTranslator;

    private EventPublisher eventPublisher;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentNodeTranslator = Mockito.mock( ContentNodeTranslator.class );
        this.eventPublisher = Mockito.mock( EventPublisher.class );
    }

    @Test
    public void resolve_requested_no_children()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishParentFor( NodeId.from( "s2" ), NodeId.from( "s1" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( contentNodeTranslator.fromNodes( Mockito.isA( Nodes.class ) ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.compare( NodeIds.from( NodeId.from( "s1" ), NodeId.from( "s2" ) ), ContentConstants.BRANCH_MASTER ) ).
            thenReturn( NodeComparisons.create().add( new NodeComparison( NodeId.from( "s1" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s2" ), CompareStatus.NEW ) ).build() );

        final ResolvePublishDependenciesResult result = ResolvePublishDependenciesCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentIds( ContentIds.from( ContentId.from( "s1" ), ContentId.from( "s2" ) ) ).
            target( ContentConstants.BRANCH_MASTER ).
            build().
            execute();

        assertEquals( 1, result.getPushContentRequests().getRequestedContentIds( true ).getSize() );
        assertEquals( 1, result.getPushContentRequests().getDependantsContentIds( true, true ).getSize() );
    }

    @Test
    public void resolve_requested_and_referred()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishReferredFrom( NodeId.from( "s2" ), NodeId.from( "s1" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( contentNodeTranslator.fromNodes( Mockito.isA( Nodes.class ) ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.compare( NodeIds.from( NodeId.from( "s1" ), NodeId.from( "s2" ) ), ContentConstants.BRANCH_MASTER ) ).
            thenReturn( NodeComparisons.create().add( new NodeComparison( NodeId.from( "s1" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s2" ), CompareStatus.NEW ) ).build() );

        final ResolvePublishDependenciesResult result = ResolvePublishDependenciesCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            target( ContentConstants.BRANCH_MASTER ).
            build().
            execute();

        assertEquals( 1, result.getPushContentRequests().getRequestedContentIds( true ).getSize() );
        assertEquals( 1, result.getPushContentRequests().getDependantsContentIds( true, true ).getSize() );
    }

    @Test
    public void resolve_requested_referred_and_parent()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                publishReferredFrom( NodeId.from( "s2" ), NodeId.from( "s1" ) ).
                publishParentFor( NodeId.from( "s3" ), NodeId.from( "s1" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( contentNodeTranslator.fromNodes( Mockito.isA( Nodes.class ) ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ),
                                       createContent( "s3", "s3Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.compare( NodeIds.from( NodeId.from( "s1" ), NodeId.from( "s2" ), NodeId.from( "s3" ) ),
                                           ContentConstants.BRANCH_MASTER ) ).
            thenReturn( NodeComparisons.create().add( new NodeComparison( NodeId.from( "s1" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s2" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s3" ), CompareStatus.NEW ) ).build() );

        final ResolvePublishDependenciesResult result = ResolvePublishDependenciesCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            target( ContentConstants.BRANCH_MASTER ).
            build().
            execute();

        assertEquals( 1, result.getPushContentRequests().getRequestedContentIds( true ).getSize() );
        assertEquals( 2, result.getPushContentRequests().getDependantsContentIds( true, true ).getSize() );
    }


    @Test
    public void resolve_requested_and_deleted()
        throws Exception
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                publishRequested( NodeId.from( "s1" ) ).
                deleteChildOf( NodeId.from( "s2" ), NodeId.from( "s1" ) ).
                deleteChildOf( NodeId.from( "s3" ), NodeId.from( "s1" ) ).
                setInitialReasonNodeId( NodeId.from( "s1" ) ).
                build() );

        Mockito.when( nodeService.getByIds( Mockito.isA( NodeIds.class ) ) ).
            thenReturn( Nodes.empty() );

        Mockito.when( contentNodeTranslator.fromNodes( Mockito.isA( Nodes.class ) ) ).
            thenReturn( Contents.from( createContent( "s1", "s1Name", ContentPath.ROOT, true ),
                                       createContent( "s2", "s2Name", ContentPath.ROOT, true ),
                                       createContent( "s3", "s3Name", ContentPath.ROOT, true ) ) );

        Mockito.when( nodeService.compare( NodeIds.from( NodeId.from( "s1" ) ), ContentConstants.BRANCH_MASTER ) ).
            thenReturn( NodeComparisons.create().add( new NodeComparison( NodeId.from( "s1" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s2" ), CompareStatus.NEW ) ).add(
                new NodeComparison( NodeId.from( "s3" ), CompareStatus.NEW ) ).build() );

        final ResolvePublishDependenciesResult result = ResolvePublishDependenciesCommand.create().
            nodeService( this.nodeService ).
            contentTypeService( this.contentTypeService ).
            translator( this.contentNodeTranslator ).
            eventPublisher( this.eventPublisher ).
            contentIds( ContentIds.from( ContentId.from( "s1" ) ) ).
            target( ContentConstants.BRANCH_MASTER ).
            build().
            execute();

        assertEquals( 1, result.getPushContentRequests().getRequestedContentIds( true ).getSize() );
        assertEquals( 0, result.getPushContentRequests().getDependantsContentIds( true, true ).getSize() );
    }

    private Content createContent( final String id, final String name, final ContentPath path, boolean valid )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            name( name ).
            parentPath( path ).
            valid( valid ).
            build();
    }
}