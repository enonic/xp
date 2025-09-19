package com.enonic.xp.core.content;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContentServiceImplTest_patch
    extends AbstractContentServiceTest
{

    @Test
    public void patch_content_modified_time_not_changed()
        throws Exception
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
    public void patch_content_without_admin_permissions()
        throws Exception
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
    public void patch_content_attachments()
        throws Exception
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
    public void patch_content_thumbnail()
        throws Exception
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
}
