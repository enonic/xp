package com.enonic.xp.core.content;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentServiceImplTest_updateMetadata
    extends AbstractContentServiceTest
{

    @Test
    void update_metadata_owner()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PrincipalKey newOwner = PrincipalKey.from( "user:system:new-owner" );

        final UpdateContentMetadataParams updateContentMetadataParams =
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> {
                edit.owner = newOwner;
            } ).build();

        final UpdateContentMetadataResult result = this.contentService.updateMetadata( updateContentMetadataParams );

        final Content updatedContent = this.contentService.getById( content.getId() );

        assertEquals( newOwner, updatedContent.getOwner() );
        assertEquals( updatedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( updatedContent.getModifier(), content.getModifier() );
        assertEquals( content.getId(), result.getContent().getId() );
    }

    @Test
    void update_metadata_same_content_on_both_branches_generates_single_version_with_origin()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        final int versionCountBeforeUpdate = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        this.contentService.updateMetadata( UpdateContentMetadataParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.owner = PrincipalKey.from( "user:system:new-owner" ) )
                                                .build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionCountBeforeUpdate + 1 );

        final ContentVersion latestVersion = versionsResult.getContentVersions().first();

        assertThat( latestVersion.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.updateMetadata" );
        assertThat( latestVersion.actions() ).extracting( ContentVersion.Action::origin )
            .containsExactly( ContentConstants.BRANCH_MASTER.getValue() );
    }

    @Test
    void update_metadata_different_content_on_branches_generates_two_versions()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        final UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentId( content.getId() ).editor( edit -> edit.displayName = "updated-in-draft" );
        this.contentService.update( updateParams );

        final int versionCountBeforeUpdate = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        this.contentService.updateMetadata( UpdateContentMetadataParams.create()
                                                .contentId( content.getId() )
                                                .editor( edit -> edit.owner = PrincipalKey.from( "user:system:new-owner" ) )
                                                .build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionCountBeforeUpdate + 2 );

        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::operation )
            .containsOnly( "content.updateMetadata" );

        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::origin )
            .containsExactly( ContentConstants.BRANCH_DRAFT.getValue(), ContentConstants.BRANCH_MASTER.getValue() );
    }

    @Test
    void update_metadata_same_owner_still_generates_version_when_inherit_flag_removed()
    {
        final PrincipalKey owner = PrincipalKey.from( "user:system:new-owner" );

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.owner = owner ).build() );

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.inherit.setValue(
                                           EnumSet.of( ContentInheritType.CONTENT, ContentInheritType.SORT, ContentInheritType.NAME ) ) )
                                       .build() );

        final Content contentWithInherit = this.contentService.getById( content.getId() );
        assertThat( contentWithInherit.getOwner() ).isEqualTo( owner );
        assertThat( contentWithInherit.getInherit() ).contains( ContentInheritType.CONTENT );

        final int versionCountBefore = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        this.contentService.updateMetadata(
            UpdateContentMetadataParams.create().contentId( content.getId() ).editor( edit -> edit.owner = owner ).build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionCountBefore + 1 );

        final ContentVersion latestVersion = versionsResult.getContentVersions().first();

        assertThat( latestVersion.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.updateMetadata" );

        assertThat( latestVersion.actions() ).flatExtracting( ContentVersion.Action::fields ).containsExactly( "inherit" );

        final Content updatedContent = this.contentService.getById( content.getId() );
        assertThat( updatedContent.getInherit() ).doesNotContain( ContentInheritType.CONTENT );
    }
}
