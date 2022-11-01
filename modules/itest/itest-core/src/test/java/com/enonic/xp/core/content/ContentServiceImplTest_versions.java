package com.enonic.xp.core.content;

import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        final FindContentVersionsResult result = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( content.getId() ).
            build() );

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

        final GetActiveContentVersionsResult activeVersions =
            this.contentService.getActiveVersions( GetActiveContentVersionsParams.create().
                contentId( content.getId() ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        final ImmutableList<ActiveContentVersionEntry> activeContentVersions = activeVersions.getActiveContentVersions();

        assertEquals( 2, activeContentVersions.size() );

        final UnmodifiableIterator<ActiveContentVersionEntry> iterator = activeContentVersions.iterator();

        assertTrue( iterator.next().getContentVersion() != iterator.next().getContentVersion() );
    }

    @Test
    public void version_workflow_info()
        throws Exception
    {
        final Map<String, WorkflowCheckState> checks =
            Map.of( "checkName1", WorkflowCheckState.APPROVED, "checkName2", WorkflowCheckState.PENDING );

        final WorkflowInfo workflowInfo = WorkflowInfo.create().
            state( WorkflowState.IN_PROGRESS ).
            checks( checks ).
            build();

        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my test content" ).
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            type( ContentTypeName.folder() ).
            workflowInfo( workflowInfo ).
            build() );

        final FindContentVersionsResult versions = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( content.getId() ).
            build() );

        final ContentVersion contentVersion = versions.getContentVersions().iterator().next();
        final WorkflowInfo retrievedWorkflowInfo = contentVersion.getWorkflowInfo();

        assertEquals( WorkflowState.IN_PROGRESS, retrievedWorkflowInfo.getState() );
        assertEquals( WorkflowCheckState.APPROVED, retrievedWorkflowInfo.getChecks().get( "checkName1" ) );
        assertEquals( WorkflowCheckState.PENDING, retrievedWorkflowInfo.getChecks().get( "checkName2" ) );
    }

    @Test
    public void get_published_versions()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "content" ).
            parent( ContentPath.ROOT ).
            name( "myContent" ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.publish( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        this.contentService.unpublishContent( UnpublishContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        final FindContentVersionsResult result = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( content.getId() ).
            build() );

        assertEquals( 3, result.getHits() );
        assertEquals( 3, result.getTotalHits() );

        final Iterator<ContentVersion> versions = result.getContentVersions().iterator();

        assertNotNull( versions.next().getPublishInfo() );
        assertNotNull( versions.next().getPublishInfo() );
        assertNull( versions.next().getPublishInfo() );
    }

    @Test
    public void get_archived_versions()
        throws Exception
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

        final FindContentVersionsResult result =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        assertEquals( 5, result.getHits() );
        assertEquals( 5, result.getTotalHits() );

        final ImmutableList<ContentVersion> versions = ImmutableList.copyOf( result.getContentVersions().iterator() );

        assertEquals( ContentVersionPublishInfo.CommitType.RESTORED, versions.get( 0 ).getPublishInfo().getType() );
        assertEquals( ContentVersionPublishInfo.CommitType.ARCHIVED, versions.get( 2 ).getPublishInfo().getType() );
    }
}

