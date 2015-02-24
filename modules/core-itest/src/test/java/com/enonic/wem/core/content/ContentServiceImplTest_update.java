package com.enonic.wem.core.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.Metadata;
import com.enonic.xp.content.Metadatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.attachment.AttachmentNames;
import com.enonic.xp.content.attachment.Attachments;
import com.enonic.xp.content.attachment.CreateAttachment;
import com.enonic.xp.content.attachment.CreateAttachments;
import com.enonic.xp.core.impl.schema.content.BuiltinContentTypeProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.form.Input.newInput;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class ContentServiceImplTest_update
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void update_content_modified_time_updated()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        final Content updatedContent = this.contentService.getById( content.getId() );

        assertEquals( "new display name", updatedContent.getDisplayName() );
        assertNotNull( updatedContent.getCreator() );
        assertNotNull( updatedContent.getCreatedTime() );
        assertNotNull( updatedContent.getModifier() );
        assertNotNull( updatedContent.getModifiedTime() );
        assertTrue( updatedContent.getModifiedTime().isAfter( content.getModifiedTime() ) );
    }

    @Test
    public void update_content_image()
        throws Exception
    {
        final ByteSource image = loadImage( "cat-small.jpg" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachment( "cat", "image/jpg", image ) ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } ).
            createAttachments( createAttachment( "darth", "image/jpg", loadImage( "darth-small.jpg" ) ) );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 3, attachments.getSize() ); // original, small, medium
    }

    @Test
    public void update_content_data()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( BuiltinContentTypeProvider.FOLDER.getName() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "testString", "value-updated" );
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( "This is my content", storedContent.getDisplayName() );
        assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
        assertEquals( "value", storedContent.getData().getString( "testString2" ) );
    }


    @Test
    public void update_with_metadata()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final Mixin mixin = newMixin().name( "mymodule:my_mixin" ).
            addFormItem( newInput().
                name( "inputToBeMixedIn" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).
            thenReturn( mixin );

        Mockito.when( this.mixinService.getByLocalName( Mockito.isA( String.class ) ) ).
            thenReturn( mixin );

        final Metadata metadata = new Metadata( MixinName.from( "mymodule:my_mixin" ), new PropertyTree() );

        Metadatas metadatas = Metadatas.from( Lists.newArrayList( metadata ) );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( BuiltinContentTypeProvider.FOLDER.getName() ).
            metadata( metadatas ).
            build();

        final Content content = this.contentService.create( createContentParams );

        assertTrue( content.hasMetadata() );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "testString", "value-updated" );
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( "This is my content", storedContent.getDisplayName() );
        assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
        assertEquals( "value", storedContent.getData().getString( "testString2" ) );
    }

    @Test
    public void update_content_with_thumbnail_keep_on_update()
        throws Exception
    {
        final ByteSource thumbnail = loadImage( "cat-small.jpg" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            contentData( new PropertyTree() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( thumbnail ).
                name( AttachmentNames.THUMBNAIL ).
                mimeType( "image/jpeg" ).
                build() ) );

        this.contentService.update( updateContentParams );

        final Content updatedContent = this.contentService.getById( content.getId() );
        assertNotNull( updatedContent.getThumbnail() );
        assertEquals( thumbnail.size(), updatedContent.getThumbnail().getSize() );

        final UpdateContentParams updateContentParams2 = new UpdateContentParams();
        updateContentParams2.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "brand new display name";
            } );

        this.contentService.update( updateContentParams2 );

        final Content reUpdatedContent = this.contentService.getById( content.getId() );
        assertNotNull( reUpdatedContent.getThumbnail() );
        assertEquals( thumbnail.size(), reUpdatedContent.getThumbnail().getSize() );
        assertEquals( "brand new display name", reUpdatedContent.getDisplayName() );
    }

    @Test
    public void update_thumbnail()
        throws Exception
    {
        final ByteSource thumbnail = loadImage( "cat-small.jpg" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            contentData( new PropertyTree() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( thumbnail ).
                name( AttachmentNames.THUMBNAIL ).
                mimeType( "image/jpeg" ).
                build() ) );

        this.contentService.update( updateContentParams );

        final Content updatedContent = this.contentService.getById( content.getId() );
        assertNotNull( updatedContent.getThumbnail() );
        assertEquals( thumbnail.size(), updatedContent.getThumbnail().getSize() );

        final ByteSource newThumbnail = loadImage( "darth-small.jpg" );

        final UpdateContentParams updateContentParams2 = new UpdateContentParams();
        updateContentParams2.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "yet another display name";
            } ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( newThumbnail ).
                name( AttachmentNames.THUMBNAIL ).
                mimeType( "image/jpeg" ).
                build() ) );

        this.contentService.update( updateContentParams2 );

        final Content reUpdatedContent = this.contentService.getById( content.getId() );

        assertNotNull( reUpdatedContent.getThumbnail() );
        final Thumbnail thumbnailAttachment = reUpdatedContent.getThumbnail();
        assertEquals( newThumbnail.size(), thumbnailAttachment.getSize() );
    }

}
