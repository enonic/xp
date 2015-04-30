package com.enonic.wem.core.content;

import java.time.LocalDateTime;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.attachment.AttachmentNames;
import com.enonic.xp.content.attachment.Attachments;
import com.enonic.xp.content.attachment.CreateAttachment;
import com.enonic.xp.content.attachment.CreateAttachments;
import com.enonic.xp.core.impl.schema.content.BuiltinContentTypeProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.form.inputtype.DateTimeConfig;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.icon.Thumbnail;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.Reference;

import static com.enonic.xp.schema.content.ContentType.newContentType;
import static com.enonic.xp.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class ContentServiceImplTest_update
    extends AbstractContentServiceTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

    // TODO: This test should run, to be fixed
    @Ignore
    @Test
    public void propertiesTransformedAccordingToContentTypeDefinition()
        throws Exception
    {
        final Content createdContent = createContentWithTransform();

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( createdContent.getId() ).
            editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "myReference", "newValue" );
            } );

        this.contentService.update( updateContentParams );
    }

    private Content createContentWithTransform()
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService.setContentTypeService( contentTypeService );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createTestContentType() );

        PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        data.addString( "myReference", "1234" );
        data.addString( "myDateTime", "2015-03-13T10:00:00+02:00" );

        return contentService.create( CreateContentParams.create().
            type( ContentTypeName.from( "myContentType" ) ).
            contentData( data ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            displayName( "my display-name" ).
            build() );
    }


    private ContentType createTestContentType()
    {
        return newContentType().
            superType( ContentTypeName.documentMedia() ).
            name( "myContentType" ).
            addFormItem( Input.create().
                inputType( InputTypes.DATE_TIME ).
                name( "myDateTime" ).
                inputTypeConfig( DateTimeConfig.create().
                    withTimezone( true ).
                    build() ).
                build() ).
            addFormItem( Input.create().
                inputType( InputTypes.CONTENT_SELECTOR ).
                name( "myReference" ).
                inputTypeConfig( ContentSelectorConfig.create().
                    addAllowedContentType( ContentTypeName.from( "myContentType" ) ).
                    build() ).
                build() ).
            build();
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
    public void update_incorrect_content_data()
        throws Exception
    {

        //Creates a content and a reference to this object
        final Content referredContent = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "Referred content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );
        final Reference reference = Reference.from( referredContent.getId().toString() );


        //Mocks the content service to return our content type
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService.setContentTypeService( contentTypeService );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createTestContentType() );

        //Creates a valid content
        PropertyTree data = new PropertyTree( );
        data.addReference( "myReference", reference );
        data.addLocalDateTime( "myDateTime", LocalDateTime.of( 2015, 03, 13, 10, 00, 0 ) );

        final Content content = this.contentService.create( CreateContentParams.create().
            type( ContentTypeName.from( "myContentType" ) ).
            contentData( data ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            displayName( "my display-name" ).
            build() );

        //Updates the content with an incorrect value
        PropertyTree invalidData = new PropertyTree( );
        invalidData.addString( "myReference", "1234" ); //Incorrect value
        invalidData.addLocalDateTime( "myDateTime",  LocalDateTime.of( 2015, 03, 13, 10, 00, 0 ) );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.data = invalidData;
            } );

        exception.expect( IllegalArgumentException.class );
        this.contentService.update( updateContentParams );
    }


    @Test
    public void update_with_metadata()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final Mixin mixin = newMixin().name( "mymodule:my_mixin" ).
            addFormItem( Input.create().
                name( "inputToBeMixedIn" ).
                inputType( InputTypes.TEXT_LINE ).
                build() ).
            build();

        Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).
            thenReturn( mixin );

        Mockito.when( this.mixinService.getByLocalName( Mockito.isA( String.class ) ) ).
            thenReturn( mixin );

        final ExtraData extraData = new ExtraData( MixinName.from( "mymodule:my_mixin" ), new PropertyTree() );

        ExtraDatas extraDatas = ExtraDatas.from( Lists.newArrayList( extraData ) );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( BuiltinContentTypeProvider.FOLDER.getName() ).
            extraDatas( extraDatas ).
            build();

        final Content content = this.contentService.create( createContentParams );

        assertTrue( content.hasExtraData() );

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
