package com.enonic.xp.core.content;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
// Remove the unused import statement
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_getActiveVersions
    extends AbstractContentServiceTest
{

    @Test
    void get_active_versions_published_content()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        // Publish the content
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        // Get active versions from both branches
        final GetActiveContentVersionsResult result = this.contentService.getActiveVersions(
            GetActiveContentVersionsParams.create()
                .contentId( content.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .build() );

        final Map<Branch, ContentVersion> contentVersions = result.getContentVersions();

        assertEquals( 2, contentVersions.size() );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_DRAFT ) );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_MASTER ) );

        // Both versions should be the same since content was just published
        assertEquals( contentVersions.get( ContentConstants.BRANCH_DRAFT ).versionId(),
                     contentVersions.get( ContentConstants.BRANCH_MASTER ).versionId() );
    }

    @Test
    void get_active_versions_modified_after_publish()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        // Publish the content
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        // Update the content in draft
        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> edit.displayName = "Updated Display Name" );
        this.contentService.update( updateContentParams );

        // Get active versions from both branches
        final GetActiveContentVersionsResult result = this.contentService.getActiveVersions(
            GetActiveContentVersionsParams.create()
                .contentId( content.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .build() );

        final Map<Branch, ContentVersion> contentVersions = result.getContentVersions();

        assertEquals( 2, contentVersions.size() );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_DRAFT ) );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_MASTER ) );

        // Versions should be different since draft was modified after publish
        assertNotEquals( contentVersions.get( ContentConstants.BRANCH_DRAFT ).versionId(),
                        contentVersions.get( ContentConstants.BRANCH_MASTER ).versionId() );
    }

    @Test
    void get_active_versions_unpublished_content()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        // Get active versions - content only exists in draft
        final GetActiveContentVersionsResult result = this.contentService.getActiveVersions(
            GetActiveContentVersionsParams.create()
                .contentId( content.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .build() );

        final Map<Branch, ContentVersion> contentVersions = result.getContentVersions();

        assertEquals( 1, contentVersions.size() );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_DRAFT ) );
        assertNull( contentVersions.get( ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    void get_active_versions_single_branch()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        // Publish the content
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        // Get active versions from only draft branch
        final GetActiveContentVersionsResult result = this.contentService.getActiveVersions(
            GetActiveContentVersionsParams.create()
                .contentId( content.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT ) )
                .build() );

        final Map<Branch, ContentVersion> contentVersions = result.getContentVersions();

        assertEquals( 1, contentVersions.size() );
        assertNotNull( contentVersions.get( ContentConstants.BRANCH_DRAFT ) );
        assertNull( contentVersions.get( ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    void get_active_versions_verify_version_properties()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        // Publish the content
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        // Get active versions
        final GetActiveContentVersionsResult result = this.contentService.getActiveVersions(
            GetActiveContentVersionsParams.create()
                .contentId( content.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .build() );

        final Map<Branch, ContentVersion> contentVersions = result.getContentVersions();

        // Verify draft version properties
        final ContentVersion draftVersion = contentVersions.get( ContentConstants.BRANCH_DRAFT );
        assertNotNull( draftVersion.versionId() );
        assertNotNull( draftVersion.timestamp() );
        assertNotNull( draftVersion.path() );
        assertNotNull( draftVersion.contentId() );

        // Verify master version properties
        final ContentVersion masterVersion = contentVersions.get( ContentConstants.BRANCH_MASTER );
        assertNotNull( masterVersion.versionId() );
        assertNotNull( masterVersion.timestamp() );
        assertNotNull( masterVersion.path() );
        assertNotNull( masterVersion.contentId() );

        // Verify the actions contain publish operation
        assertThat( masterVersion.actions() ).isNotEmpty();
        assertThat( masterVersion.actions().stream()
                       .anyMatch( action -> "content.publish".equals( action.operation() ) ) ).isTrue();
    }

    @Test
    void get_active_versions_no_role()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "My Content" )
                                                                .parent( ContentPath.ROOT )
                                                                .name( "myContent" )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        assertThrows( ForbiddenAccessException.class, () -> ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( testprojectName.getRepoId() )
            .build()
            .runWith( () -> this.contentService.getActiveVersions( GetActiveContentVersionsParams.create()
                                                                       .contentId( content.getId() )
                                                                       .branches( Branches.from( ContentConstants.BRANCH_DRAFT ) )
                                                                       .build() ) ) );
    }
}
