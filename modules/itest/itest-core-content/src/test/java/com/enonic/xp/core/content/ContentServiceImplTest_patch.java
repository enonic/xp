package com.enonic.xp.core.content;

import java.util.Iterator;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentServiceImplTest_patch
    extends AbstractContentServiceTest
{

    @Test
    void patch_content_modified_time_not_changed()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PatchContentParams patchContentParams = PatchContentParams.create().contentId( content.getId() ).patcher( edit -> {
            edit.displayName.setValue( "new display name" );
        } ).build();

        this.contentService.patch( patchContentParams );

        final Content patchedContent = this.contentService.getById( content.getId() );

        assertEquals( "new display name", patchedContent.getDisplayName() );
        assertEquals( patchedContent.getCreatedTime(), content.getCreatedTime() );
        assertEquals( patchedContent.getModifiedTime(), content.getModifiedTime() );
        assertEquals( patchedContent.getModifier(), content.getModifier() );
    }

    @Test
    void patch_content_without_admin_permissions()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PatchContentParams patchContentParams = PatchContentParams.create().contentId( content.getId() ).patcher( edit -> {
            edit.displayName.setValue( "new display name" );
        } ).build();

        assertThrows( ForbiddenAccessException.class, () -> ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().user( ContextAccessor.current().getAuthInfo().getUser() ).build() )
            .build()
            .runWith( () -> {
                this.contentService.patch( patchContentParams );
            } ) );
    }

    @Test
    void patch_content_attachments()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .createAttachments( CreateAttachments.create()
                                    .add( CreateAttachment.create()
                                              .name( "test-attachment1" )
                                              .label( "test-label1" )
                                              .mimeType( "image/jpeg" )
                                              .text( "text content 1" )
                                              .byteSource( ByteSource.wrap( "ABC".getBytes() ) )
                                              .build() )
                                    .add( CreateAttachment.create()
                                              .name( "test-attachment2" )
                                              .label( "test-label2" )
                                              .mimeType( "image/jpeg" )
                                              .text( "text content 2" )
                                              .byteSource( ByteSource.wrap( "ABCD".getBytes() ) )
                                              .build() )
                                    .add( CreateAttachment.create()
                                              .name( "test-attachment3" )
                                              .label( "test-label3" )
                                              .mimeType( "image/jpeg" )
                                              .text( "text content 3" )
                                              .byteSource( ByteSource.wrap( "ABCDE".getBytes() ) )
                                              .build() )
                                    .build() )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PatchContentParams patchContentParams = PatchContentParams.create()
            .contentId( content.getId() )
            .patcher( edit -> {
                edit.attachments.setValue( Attachments.create()
                                               .add( Attachment.create()
                                                         .name( "test-attachment1" )
                                                         .label( "test-label-edited" )
                                                         .mimeType( "image/gif" )
                                                         .size( 666 )
                                                         .sha512( "sha512" )
                                                         .textContent( "text content edited" )
                                                         .build() )
                                               .build() );
            } )
            .createAttachments( CreateAttachments.create()
                                    .add( CreateAttachment.create()
                                              .name( "test-attachment-added" )
                                              .label( "test-label-edited" )
                                              .mimeType( "image/jpeg" )
                                              .text( "text content added" )
                                              .byteSource( ByteSource.wrap( "ABC".getBytes() ) )
                                              .build() )
                                    .build() )
            .build();

        this.contentService.patch( patchContentParams );

        final Content patchedContent = this.contentService.getById( content.getId() );

        assertEquals( 2, patchedContent.getAttachments().getSize() );

        final Iterator<Attachment> iterator = patchedContent.getAttachments().iterator();

        final Attachment attachment = iterator.next();
        assertEquals( "test-attachment1", attachment.getName() );
        assertEquals( "test-label-edited", attachment.getLabel() );
        assertEquals( "image/gif", attachment.getMimeType() );
        assertEquals( 666, attachment.getSize() );
        assertEquals( "sha512", attachment.getSha512() );
        assertEquals( "text content edited", attachment.getTextContent() );
        assertEquals( "sha512", attachment.getSha512() );

        final Attachment attachment2 = iterator.next();
        assertEquals( "test-attachment-added", attachment2.getName() );
        assertEquals( "test-label-edited", attachment2.getLabel() );
        assertEquals( "image/jpeg", attachment2.getMimeType() );
        assertEquals( "text content added", attachment2.getTextContent() );
        assertEquals( 3, attachment2.getSize() );
        assertEquals( 128, attachment2.getSha512().length() );
    }

    @Test
    void patch_content_thumbnail()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .createAttachments( CreateAttachments.empty() )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final PatchContentParams patchContentParams = PatchContentParams.create()
            .contentId( content.getId() )
            .createAttachments( CreateAttachments.create()
                                    .add( CreateAttachment.create()
                                              .name( AttachmentNames.THUMBNAIL )
                                              .label( "test-label-edited" )
                                              .mimeType( "image/jpeg" )
                                              .text( "text content added" )
                                              .byteSource( ByteSource.wrap( "ABC".getBytes() ) )
                                              .build() )
                                    .build() )
            .build();

        this.contentService.patch( patchContentParams );

        final Content patchedContent = this.contentService.getById( content.getId() );

        final Attachment thumbnail = patchedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertEquals( "image/jpeg", thumbnail.getMimeType() );
        assertEquals( AttachmentNames.THUMBNAIL, thumbnail.getBinaryReference().toString() );
        assertEquals( 3, thumbnail.getSize() );
    }

    @Test
    void patch_same_content_on_both_branches_generates_single_version_with_origin()
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

        final int versionCountBeforePatch = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.displayName.setValue( "patched" ) )
                                       .branches( Branches.from( ContentConstants.BRANCH_MASTER, ContentConstants.BRANCH_DRAFT ) )
                                       .build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionCountBeforePatch + 1 );

        final ContentVersion latestVersion = versionsResult.getContentVersions().first();

        assertThat( latestVersion.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.patch" );
        assertThat( latestVersion.actions() ).extracting( ContentVersion.Action::origin )
            .containsExactly( ContentConstants.BRANCH_MASTER.getValue() );
    }

    @Test
    void patch_different_content_on_branches_generates_two_versions()
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

        final int versionCountBeforePatch = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.language.setValue( Locale.ENGLISH ) )
                                       .branches( Branches.from( ContentConstants.BRANCH_MASTER, ContentConstants.BRANCH_DRAFT ) )
                                       .build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionCountBeforePatch + 2 );

        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::operation )
            .containsOnly( "content.patch" );

        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::origin )
            .containsExactly( ContentConstants.BRANCH_DRAFT.getValue(), ContentConstants.BRANCH_MASTER.getValue() );
    }

    @Test
    void patch_language_only_resets_editorial_pointer()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "lang-patch" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.language.setValue( Locale.ENGLISH ) )
                                       .build() );

        final GetContentVersionsResult versions =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        final ContentVersion latest = versions.getContentVersions().first();

        assertThat( latest.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.patch" );
        assertThat( latest.actions() ).flatExtracting( ContentVersion.Action::fields ).contains( "language" );
        // With language in EDITORIAL_FIELDS, the resolveEditorialIfNotEditorialChange resolver returns null
        // (no carry-over), so the new version has no `editorial` action property.
        assertThat( latest.actions() ).extracting( ContentVersion.Action::editorial ).containsExactly( (ContentVersionId) null );
    }

    @Test
    void patch_data_only_resets_editorial_pointer()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "data-patch" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> {
                                           final PropertyTree newData = new PropertyTree();
                                           newData.addString( "myField", "value" );
                                           edit.data.setValue( newData );
                                       } )
                                       .build() );

        final GetContentVersionsResult versions =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).size( 1 ).build() );

        final ContentVersion latest = versions.getContentVersions().first();

        assertThat( latest.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.patch" );
        assertThat( latest.actions() ).flatExtracting( ContentVersion.Action::fields ).contains( "data" );
        assertThat( latest.actions() ).extracting( ContentVersion.Action::editorial ).containsExactly( (ContentVersionId) null );
    }

    @Test
    void patch_childOrder_only_resets_editorial_pointer()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "childorder-patch" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.childOrder.setValue( ChildOrder.from( "_name ASC" ) ) )
                                       .build() );

        final GetContentVersionsResult versions =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).size( 1 ).build() );

        final ContentVersion latest = versions.getContentVersions().first();

        assertThat( latest.actions() ).extracting( ContentVersion.Action::operation ).containsExactly( "content.patch" );
        assertThat( latest.actions() ).flatExtracting( ContentVersion.Action::fields ).contains( "childOrder" );
        assertThat( latest.actions() ).extracting( ContentVersion.Action::editorial ).containsExactly( (ContentVersionId) null );
    }

    @Test
    void patch_editorial_with_draft_ahead_of_master_resets_editorial_on_both_versions()
    {
        // Create content; publish so DRAFT and MASTER are both at V1.
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "content" )
                                                                .name( "two-version-patch" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.publish( PushContentParams.create()
                                         .contentIds( ContentIds.from( content.getId() ) )
                                         .includeDependencies( false )
                                         .build() );

        // Diverge: editorial update only on DRAFT; MASTER stays at V1.
        final UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentId( content.getId() ).editor( edit -> edit.displayName = "updated-in-draft" );
        this.contentService.update( updateParams );

        final int versionsBefore = this.contentService.getVersions(
            GetContentVersionsParams.create().contentId( content.getId() ).build() ).getContentVersions().getSize();

        // Editorial patch targeting BOTH branches — must produce two new versions, both with editorial = null.
        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                                       .patcher( edit -> edit.language.setValue( Locale.ENGLISH ) )
                                       .build() );

        final GetContentVersionsResult versionsResult =
            this.contentService.getVersions( GetContentVersionsParams.create().contentId( content.getId() ).build() );

        assertThat( versionsResult.getContentVersions() ).hasSize( versionsBefore + 2 );

        // The two newest versions are the patch outputs, one per branch.
        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::operation )
            .containsOnly( "content.patch" );

        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::origin )
            .containsExactlyInAnyOrder( ContentConstants.BRANCH_DRAFT.getValue(), ContentConstants.BRANCH_MASTER.getValue() );

        // Both new versions must have editorial = null.
        assertThat( versionsResult.getContentVersions().stream()
                        .limit( 2 )
                        .flatMap( v -> v.actions().stream() ) )
            .extracting( ContentVersion.Action::editorial )
            .containsOnly( (ContentVersionId) null );
    }
}
