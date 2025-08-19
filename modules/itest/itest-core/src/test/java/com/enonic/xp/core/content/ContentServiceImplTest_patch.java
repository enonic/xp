package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

//    @Test
//    public void patch_with_skip_sync()
//        throws Exception
//    {
//        Instant now = Instant.now();
//
//        final CreateContentParams createContentParams = CreateContentParams.create()
//            .contentData( new PropertyTree() )
//            .displayName( "This is my content" )
//            .parent( ContentPath.ROOT )
//            .type( ContentTypeName.folder() )
//            .build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final PatchContentParams patchContentParams = PatchContentParams.create().contentId( content.getId() ).patcher( edit -> {
//            edit.displayName.setValue( "new display name" );
//        } ).skipSync( true ).build();
//
//        this.contentService.patch( patchContentParams );
//
//        final FindAuditLogResult findAuditLogResult = auditLogService.find( FindAuditLogParams.create().from( now ).build() );
//
//        Mockito.verify( eventPublisher ).publish( Mockito.any() );
//
////        assertEquals( 1, findAuditLogResult.getCount());
//    }
//
//    @Test
//    public void update_content_image()
//        throws Exception
//    {
//        final ByteSource image = loadImage( "cat-small.jpg" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( new PropertyTree() ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.imageMedia() ).
//            createAttachments( createAttachment( "cat", "image/jpeg", image ) ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                edit.displayName = "new display name";
//            } ).
//            clearAttachments( true ).
//            createAttachments( createAttachment( "darth", "image/jpeg", loadImage( "darth-small.jpg" ) ) );
//
//        this.contentService.update( updateContentParams );
//
//        final Content storedContent = this.contentService.getById( content.getId() );
//
//        final Attachments attachments = storedContent.getAttachments();
//        assertEquals( 1, attachments.getSize() );
//    }
//
//    @Test
//    public void update_content_data()
//        throws Exception
//    {
//        final PropertyTree data = new PropertyTree();
//        data.setString( "testString", "value" );
//        data.setString( "testString2", "value" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( data ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                final PropertyTree editData = edit.data;
//                editData.setString( "testString", "value-updated" );
//            } );
//
//        this.contentService.update( updateContentParams );
//
//        final Content storedContent = this.contentService.getById( content.getId() );
//
//        assertEquals( "This is my content", storedContent.getDisplayName() );
//        assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
//        assertEquals( "value", storedContent.getData().getString( "testString2" ) );
//    }
//
//    @Test
//    public void update_incorrect_content_data()
//        throws Exception
//    {
//
//        //Mocks the content service to return our content type
//        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
//        this.contentService.setContentTypeService( contentTypeService );
//        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
//            thenReturn( createContentTypeForAllInputTypes() );
//
//        //Creates a valid content
//        PropertyTree data = createPropertyTreeForAllInputTypes();
//
//        final Content content = this.contentService.create( CreateContentParams.create().
//            type( ContentTypeName.from( "myContentType" ) ).
//            contentData( data ).
//            name( "myContent" ).
//            parent( ContentPath.ROOT ).
//            displayName( "my display-name" ).
//            build() );
//
//        //Updates the content with an incorrect value
//        PropertyTree invalidData = new PropertyTree();
//        invalidData.addLong( "textLine", 1L );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addLong( "double", 1L );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "long", 1.0d );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addBoolean( "comboBox", true );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addString( "comboBox", "value4" );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "checkbox", 1.0d );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "tag", 1.0d );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "contentSelector", 1.0d );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addDouble( "contentTypeFilter", 1.0d );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addLocalDateTime( "date", LocalDateTime.of( 2015, 3, 13, 10, 0, 0 ) );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addInstant( "time", Instant.now() );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addString( "geoPoint", "59.9127300, 10.7460900" );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addXml( "htmlArea", "<p>paragraph</p>" );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addLocalDate( "localDateTime", LocalDate.of( 2015, 3, 13 ) );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidData.addLocalDate( "dateTime", LocalDate.of( 2015, 3, 13 ) );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        PropertySet invalidSet = invalidData.newSet();
//        invalidSet.addDouble( "setString", 1.0d );
//        invalidData.addSet( "set", invalidSet );
//        update_incorrect_content_data( content, invalidData );
//
//        //Updates the content with an incorrect value
//        invalidData = new PropertyTree();
//        invalidSet = invalidData.newSet();
//        invalidSet.addLong( "setDouble", 1L );
//        invalidData.addSet( "set", invalidSet );
//        update_incorrect_content_data( content, invalidData );
//    }
//
//    private void update_incorrect_content_data( Content content, PropertyTree invalidData )
//    {
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                edit.data = invalidData;
//            } );
//
//        boolean illegalArgumentExceptionThrown = false;
//        try
//        {
//            this.contentService.update( updateContentParams );
//        }
//        catch ( Exception e )
//        {
//            illegalArgumentExceptionThrown = true;
//        }
//        assertTrue( illegalArgumentExceptionThrown );
//    }
//
//
//    @Test
//    public void update_with_metadata()
//        throws Exception
//    {
//        final PropertyTree data = new PropertyTree();
//        data.setString( "testString", "value" );
//        data.setString( "testString2", "value" );
//
//        final Mixin mixin = Mixin.create().name( "myapplication:my_mixin" ).
//            addFormItem( Input.create().
//                name( "inputToBeMixedIn" ).
//                label( "Mixed in" ).
//                inputType( InputTypeName.TEXT_LINE ).
//                build() ).
//            build();
//
//        Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).
//            thenReturn( mixin );
//
//        final ExtraData extraData = new ExtraData( XDataName.from( "myapplication:my_mixin" ), new PropertyTree() );
//
//        ExtraDatas extraDatas = ExtraDatas.from( List.of( extraData ) );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( data ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            permissions( AccessControlList.empty() ).
//            type( ContentTypeName.folder() ).
//            extraDatas( extraDatas ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        assertTrue( content.hasExtraData() );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                final PropertyTree editData = edit.data;
//                editData.setString( "testString", "value-updated" );
//            } );
//
//        this.contentService.update( updateContentParams );
//
//        final Content storedContent = this.contentService.getById( content.getId() );
//
//        assertEquals( "This is my content", storedContent.getDisplayName() );
//        assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
//        assertEquals( "value", storedContent.getData().getString( "testString2" ) );
//    }
//
//    @Test
//    public void update_content_with_thumbnail_keep_on_update()
//        throws Exception
//    {
//        final ByteSource thumbnail = loadImage( "cat-small.jpg" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            contentData( new PropertyTree() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.contentId( content.getId() ).
//            editor( edit -> {
//                edit.displayName = "new display name";
//            } ).
//            createAttachments( CreateAttachments.from( CreateAttachment.create().
//                byteSource( thumbnail ).
//                name( AttachmentNames.THUMBNAIL ).
//                mimeType( "image/jpeg" ).
//                build() ) );
//
//        this.contentService.update( updateContentParams );
//
//        final Content updatedContent = this.contentService.getById( content.getId() );
//        assertNotNull( updatedContent.getThumbnail() );
//        assertEquals( thumbnail.size(), updatedContent.getThumbnail().getSize() );
//
//        final UpdateContentParams updateContentParams2 = new UpdateContentParams();
//        updateContentParams2.contentId( content.getId() ).
//            editor( edit -> {
//                edit.displayName = "brand new display name";
//            } );
//
//        this.contentService.update( updateContentParams2 );
//
//        final Content reUpdatedContent = this.contentService.getById( content.getId() );
//        assertNotNull( reUpdatedContent.getThumbnail() );
//        assertEquals( thumbnail.size(), reUpdatedContent.getThumbnail().getSize() );
//        assertEquals( "brand new display name", reUpdatedContent.getDisplayName() );
//    }
//
//    @Test
//    public void update_thumbnail()
//        throws Exception
//    {
//        final ByteSource thumbnail = loadImage( "cat-small.jpg" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            contentData( new PropertyTree() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.contentId( content.getId() ).
//            editor( edit -> {
//                edit.displayName = "new display name";
//            } ).
//            createAttachments( CreateAttachments.from( CreateAttachment.create().
//                byteSource( thumbnail ).
//                name( AttachmentNames.THUMBNAIL ).
//                mimeType( "image/jpeg" ).
//                build() ) );
//
//        this.contentService.update( updateContentParams );
//
//        final Content updatedContent = this.contentService.getById( content.getId() );
//        assertNotNull( updatedContent.getThumbnail() );
//        assertEquals( thumbnail.size(), updatedContent.getThumbnail().getSize() );
//
//        final ByteSource newThumbnail = loadImage( "darth-small.jpg" );
//
//        final UpdateContentParams updateContentParams2 = new UpdateContentParams();
//        updateContentParams2.contentId( content.getId() ).
//            editor( edit -> {
//                edit.displayName = "yet another display name";
//            } ).
//            createAttachments( CreateAttachments.from( CreateAttachment.create().
//                byteSource( newThumbnail ).
//                name( AttachmentNames.THUMBNAIL ).
//                mimeType( "image/jpeg" ).
//                build() ) );
//
//        this.contentService.update( updateContentParams2 );
//
//        final Content reUpdatedContent = this.contentService.getById( content.getId() );
//
//        assertNotNull( reUpdatedContent.getThumbnail() );
//        final Thumbnail thumbnailAttachment = reUpdatedContent.getThumbnail();
//        assertEquals( newThumbnail.size(), thumbnailAttachment.getSize() );
//    }
//
//    @Test
//    void update_thumbnail_skip_not_changed()
//        throws Exception
//    {
//        final ByteSource thumbnail = loadImage( "cat-small.jpg" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            contentData( new PropertyTree() ).
//            createAttachments( CreateAttachments.from( CreateAttachment.create().
//            byteSource( thumbnail ).
//            name( AttachmentNames.THUMBNAIL ).
//            mimeType( "image/jpeg" ).
//            build() ) ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//
//        final Content createdContent = this.contentService.getById( content.getId() );
//        assertNotNull( createdContent.getThumbnail() );
//        assertEquals( thumbnail.size(), createdContent.getThumbnail().getSize() );
//
//        final ByteSource newThumbnail = loadImage( "cat-small.jpg" );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.contentId( content.getId() ).
//            createAttachments( CreateAttachments.from( CreateAttachment.create().
//            byteSource( newThumbnail ).
//            name( AttachmentNames.THUMBNAIL ).
//            mimeType( "image/jpeg" ).
//            build() ) );
//
//        this.contentService.update( updateContentParams );
//
//        final Content updatedContent = this.contentService.getById( content.getId() );
//        assertEquals( content.getModifiedTime(), updatedContent.getModifiedTime() );
//        assertNotNull( updatedContent.getThumbnail() );
//        final Thumbnail thumbnailAttachment = updatedContent.getThumbnail();
//        assertEquals( thumbnail.size(), thumbnailAttachment.getSize() );
//    }
//    @Test
//    public void update_publish_info()
//        throws Exception
//    {
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( new PropertyTree() ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                edit.publishInfo = ContentPublishInfo.create().
//                    from( Instant.parse( "2016-11-03T10:43:44Z" ) ).
//                    to( Instant.parse( "2016-11-23T10:43:44Z" ) ).
//                    build();
//            } );
//
//        this.contentService.update( updateContentParams );
//
//        final Content storedContent = this.contentService.getById( content.getId() );
//        assertNotNull( storedContent.getPublishInfo() );
//        assertNotNull( storedContent.getPublishInfo().getFrom() );
//        assertNotNull( storedContent.getPublishInfo().getTo() );
//        assertEquals( storedContent.getPublishInfo().getFrom(), Instant.parse( "2016-11-03T10:43:44Z" ) );
//        assertEquals( storedContent.getPublishInfo().getTo(), Instant.parse( "2016-11-23T10:43:44Z" ) );
//    }
//
//    @Test
//    public void update_workflow_info()
//        throws Exception
//    {
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( new PropertyTree() ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            workflowInfo( WorkflowInfo.inProgress() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).checks(
//                    Map.of( "Laywer review", WorkflowCheckState.PENDING ) ).build();
//            } );
//
//        this.contentService.update( updateContentParams );
//
//        final Content storedContent = this.contentService.getById( content.getId() );
//        assertNotNull( storedContent.getWorkflowInfo() );
//        assertNotNull( storedContent.getWorkflowInfo().getState() );
//        assertNotNull( storedContent.getWorkflowInfo().getChecks() );
//        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
//        assertEquals( Map.of( "Laywer review", WorkflowCheckState.PENDING ), storedContent.getWorkflowInfo().getChecks() );
//    }
//
//    @Test
//    public void audit_data()
//    {
//        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );
//
//        final PropertyTree data = new PropertyTree();
//        data.setString( "testString", "value" );
//        data.setString( "testString2", "value" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create().
//            contentData( data ).
//            displayName( "This is my content" ).
//            parent( ContentPath.ROOT ).
//            type( ContentTypeName.folder() ).
//            build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.
//            contentId( content.getId() ).
//            editor( edit -> {
//                final PropertyTree editData = edit.data;
//                editData.setString( "testString", "value-updated" );
//            } );
//
//        Mockito.reset( auditLogService );
//
//        this.contentService.update( updateContentParams );
//
//        verify( auditLogService, atMostOnce() ).log( captor.capture() );
//
//        final LogAuditLogParams log = captor.getValue();
//        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.update" ) ;
//        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
//            .extracting( result -> result.getString( "id" ), result -> result.getString( "path" ) )
//            .containsExactly( content.getId().toString(), content.getPath().toString() );
//    }
//
//    @Test
//    public void audit_data_disabled()
//    {
//        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );
//
//        when( contentAuditLogFilterService.accept( any() ) ).thenReturn( false );
//
//        final PropertyTree data = new PropertyTree();
//        data.setString( "testString", "value" );
//        data.setString( "testString2", "value" );
//
//        final CreateContentParams createContentParams = CreateContentParams.create()
//            .contentData( data )
//            .displayName( "This is my content" )
//            .parent( ContentPath.ROOT )
//            .type( ContentTypeName.folder() )
//            .build();
//
//        final Content content = this.contentService.create( createContentParams );
//
//        final UpdateContentParams updateContentParams = new UpdateContentParams();
//        updateContentParams.contentId( content.getId() ).editor( edit -> {
//            final PropertyTree editData = edit.data;
//            editData.setString( "testString", "value-updated" );
//        } );
//
//        Mockito.reset( auditLogService );
//
//        this.contentService.update( updateContentParams );
//
//        Mockito.verifyNoMoreInteractions( auditLogService );
//    }
}
