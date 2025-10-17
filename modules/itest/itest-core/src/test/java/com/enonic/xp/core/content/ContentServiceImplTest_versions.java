package com.enonic.xp.core.content;

import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionCommitInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        assertEquals( 2, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
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

        this.contentService.unpublish( UnpublishContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        final FindContentVersionsResult result = this.contentService.getVersions( FindContentVersionsParams.create().
            contentId( content.getId() ).
            build() );

        assertEquals( 3, result.getContentVersions().getSize() );
        assertEquals( 3, result.getTotalHits() );

        final Iterator<ContentVersion> versions = result.getContentVersions().iterator();

        assertNotNull( versions.next().getPublishInfo() );
        assertNotNull( versions.next().getPublishInfo() );
        assertNull( versions.next().getPublishInfo() );
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

        final FindContentVersionsResult result =
            this.contentService.getVersions( FindContentVersionsParams.create().contentId( content.getId() ).build() );

        assertEquals( 3, result.getContentVersions().getSize() );
        assertEquals( 3, result.getTotalHits() );

        assertThat( result.getContentVersions() ).elements( 0, 1 )
            .extracting( v -> v.getCommitInfo().getType() )
            .containsExactly( ContentVersionCommitInfo.CommitType.RESTORED, ContentVersionCommitInfo.CommitType.ARCHIVED );
    }
}

