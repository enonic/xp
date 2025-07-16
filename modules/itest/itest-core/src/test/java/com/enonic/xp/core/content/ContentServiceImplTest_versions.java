package com.enonic.xp.core.content;

import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.GetActiveNodeVersionsParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class ContentServiceImplTest_versions
    extends AbstractContentServiceTest
{

    @Test
    public void get_versions()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            type( ContentTypeName.folder() ).
            build() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        final NodeVersionQueryResult result =
            this.nodeService.findVersions( GetNodeVersionsParams.create().nodeId( NodeId.from( content.getId() ) ).build() );

        assertEquals( 2, result.getHits() );
        assertEquals( 2, result.getTotalHits() );
    }

    @Test
    public void get_active_versions()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my test content" ).
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        // Two versions, since publish adds one version
        assertVersions( content.getId(), 2 );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        assertVersions( content.getId(), 3 );

        final GetActiveNodeVersionsResult activeVersions =
            this.nodeService.getActiveVersions( GetActiveNodeVersionsParams.create().
                nodeId( NodeId.from( content.getId() ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        final Map<Branch, NodeVersionMetadata> activeContentVersions = activeVersions.getNodeVersions();

        assertEquals( 2, activeContentVersions.size() );

        final Iterator<NodeVersionMetadata> iterator = activeContentVersions.values().iterator();

        assertNotSame( iterator.next(), iterator.next() );
    }

    @Test
    void get_archived_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.archive( ArchiveContentParams.create().contentId( content.getId() ).build() );

        this.contentService.restore( RestoreContentParams.create().contentId( content.getId() ).build() );

        final NodeVersionQueryResult result =
            this.nodeService.findVersions( GetNodeVersionsParams.create().nodeId( NodeId.from( content.getId() ) ).build() );

        assertEquals( 5, result.getHits() );
        assertEquals( 5, result.getTotalHits() );
    }
}

