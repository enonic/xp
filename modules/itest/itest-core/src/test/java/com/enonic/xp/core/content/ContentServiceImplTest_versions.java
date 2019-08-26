package com.enonic.xp.core.content;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_versions
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

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

        this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
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
                branches( Branches.from( CTX_DEFAULT.getBranch(), CTX_OTHER.getBranch() ) ).
                build() );

        final ImmutableSortedSet<ActiveContentVersionEntry> activeContentVersions = activeVersions.getActiveContentVersions();

        assertEquals( 2, activeContentVersions.size() );

        final UnmodifiableIterator<ActiveContentVersionEntry> iterator = activeContentVersions.iterator();

        assertTrue( iterator.next().getContentVersion() != iterator.next().getContentVersion() );
    }

    @Test
    public void version_workflow_info()
        throws Exception
    {
        final ImmutableMap<String, WorkflowCheckState> checks =
            ImmutableMap.of( "checkName1", WorkflowCheckState.APPROVED, "checkName2", WorkflowCheckState.PENDING );

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
}

