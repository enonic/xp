package com.enonic.xp.core.content;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentServiceImplTest_update
    extends AbstractContentServiceTest
{

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
            createAttachments( createAttachment( "cat", "image/jpeg", image ) ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } ).
            clearAttachments( true ).
            createAttachments( createAttachment( "darth", "image/jpeg", loadImage( "darth-small.jpg" ) ) );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
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
            type( ContentTypeName.folder() ).
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

        //Mocks the content service to return our content type
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentService.setContentTypeService( contentTypeService );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentTypeForAllInputTypes() );

        //Creates a valid content
        PropertyTree data = createPropertyTreeForAllInputTypes();

        final Content content = this.contentService.create( CreateContentParams.create().
            type( ContentTypeName.from( "myContentType" ) ).
            contentData( data ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            displayName( "my display-name" ).
            build() );

        //Updates the content with an incorrect value
        PropertyTree invalidData = new PropertyTree();
        invalidData.addLong( "textLine", 1L );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLong( "double", 1L );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "long", 1.0d );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addBoolean( "comboBox", true );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addString( "comboBox", "value4" );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "checkbox", 1.0d );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "tag", 1.0d );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentSelector", 1.0d );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addDouble( "contentTypeFilter", 1.0d );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDateTime( "date", LocalDateTime.of( 2015, 3, 13, 10, 0, 0 ) );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addInstant( "time", Instant.now() );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addString( "geoPoint", "59.9127300, 10.7460900" );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addXml( "htmlArea", "<p>paragraph</p>" );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDate( "localDateTime", LocalDate.of( 2015, 3, 13 ) );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidData.addLocalDate( "dateTime", LocalDate.of( 2015, 3, 13 ) );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        PropertySet invalidSet = invalidData.newSet();
        invalidSet.addDouble( "setString", 1.0d );
        invalidData.addSet( "set", invalidSet );
        update_incorrect_content_data( content, invalidData );

        //Updates the content with an incorrect value
        invalidData = new PropertyTree();
        invalidSet = invalidData.newSet();
        invalidSet.addLong( "setDouble", 1L );
        invalidData.addSet( "set", invalidSet );
        update_incorrect_content_data( content, invalidData );
    }

    private void update_incorrect_content_data( Content content, PropertyTree invalidData )
    {
        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.data = invalidData;
            } );

        boolean illegalArgumentExceptionThrown = false;
        try
        {
            this.contentService.update( updateContentParams );
        }
        catch ( Exception e )
        {
            illegalArgumentExceptionThrown = true;
        }
        assertTrue( illegalArgumentExceptionThrown );
    }


    @Test
    public void update_with_metadata()
        throws Exception
    {
        final Project project = projectService.create( CreateProjectParams.create()
                                                           .name( ProjectName.from( "project" ) )
                                                           .displayName( "project" )
                                                           .addSiteConfig( SiteConfig.create()
                                                                               .application( ApplicationKey.from( "com.enonic.app.test" ) )
                                                                               .config( new PropertyTree() )
                                                                               .build() )
                                                           .build() );

        ContextBuilder.from( ContextAccessor.current() ).repositoryId( project.getName().getRepoId() ).build().runWith( () -> {
            final PropertyTree data = new PropertyTree();
            data.setString( "testString", "value" );
            data.setString( "testString2", "value" );

            final Mixin mixin = Mixin.create()
                .name( "myapplication:my_mixin" )
                .addFormItem( Input.create().name( "inputToBeMixedIn" ).label( "Mixed in" ).inputType( InputTypeName.TEXT_LINE ).build() )
                .build();

            Mockito.when( this.mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

            final ExtraDatas extraDatas = createExtraDatas();

            final CreateContentParams createContentParams = CreateContentParams.create()
                .contentData( data )
                .displayName( "This is my content" )
                .parent( ContentPath.ROOT )
                .permissions( AccessControlList.empty() )
                .type( ContentTypeName.folder() )
                .extraDatas( extraDatas )
                .build();

            final Content content = this.contentService.create( createContentParams );

            assertTrue( content.hasExtraData() );

            final UpdateContentParams updateContentParams = new UpdateContentParams();
            updateContentParams.contentId( content.getId() ).editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "testString", "value-updated" );
            } );

            this.contentService.update( updateContentParams );

            final Content storedContent = this.contentService.getById( content.getId() );

            assertEquals( "This is my content", storedContent.getDisplayName() );
            assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
            assertEquals( "value", storedContent.getData().getString( "testString2" ) );
        } );
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
        final Attachment updatedContentThumbnail = updatedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( updatedContentThumbnail );
        assertEquals( thumbnail.size(), updatedContentThumbnail.getSize() );

        final UpdateContentParams updateContentParams2 = new UpdateContentParams();
        updateContentParams2.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "brand new display name";
            } );

        this.contentService.update( updateContentParams2 );

        final Content reUpdatedContent = this.contentService.getById( content.getId() );
        final Attachment reUpdatedContentThumbnail = reUpdatedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( reUpdatedContentThumbnail );
        assertEquals( thumbnail.size(), reUpdatedContentThumbnail.getSize() );
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
        final Attachment updatedContentThumbnail = updatedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( updatedContentThumbnail );
        assertEquals( thumbnail.size(), updatedContentThumbnail.getSize() );

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
        final Attachment reUpdatedContentThumbnail = reUpdatedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( reUpdatedContentThumbnail );
        assertEquals( newThumbnail.size(), reUpdatedContentThumbnail.getSize() );
    }

    @Test
    void update_thumbnail_skip_not_changed()
        throws Exception
    {
        final ByteSource thumbnail = loadImage( "cat-small.jpg" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            contentData( new PropertyTree() ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
            byteSource( thumbnail ).
            name( AttachmentNames.THUMBNAIL ).
            mimeType( "image/jpeg" ).
            build() ) ).
            build();

        final Content content = this.contentService.create( createContentParams );


        final Content createdContent = this.contentService.getById( content.getId() );
        final Attachment createdContentThumbnail = createdContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( createdContentThumbnail );
        assertEquals( thumbnail.size(), createdContentThumbnail.getSize() );

        final ByteSource newThumbnail = loadImage( "cat-small.jpg" );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
            byteSource( newThumbnail ).
            name( AttachmentNames.THUMBNAIL ).
            mimeType( "image/jpeg" ).
            build() ) );

        this.contentService.update( updateContentParams );

        final Content updatedContent = this.contentService.getById( content.getId() );
        assertEquals( content.getModifiedTime(), updatedContent.getModifiedTime() );
        final Attachment updatedContentThumbnail = updatedContent.getAttachments().byName( AttachmentNames.THUMBNAIL );
        assertNotNull( updatedContentThumbnail );
        assertEquals( thumbnail.size(), updatedContentThumbnail.getSize() );
    }
    @Test
    public void update_publish_info()
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
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.publishInfo = ContentPublishInfo.create().
                    from( Instant.parse( "2016-11-03T10:43:44Z" ) ).
                    to( Instant.parse( "2016-11-23T10:43:44Z" ) ).
                    build();
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishInfo() );
        assertNotNull( storedContent.getPublishInfo().getFrom() );
        assertNotNull( storedContent.getPublishInfo().getTo() );
        assertEquals( storedContent.getPublishInfo().getFrom(), Instant.parse( "2016-11-03T10:43:44Z" ) );
        assertEquals( storedContent.getPublishInfo().getTo(), Instant.parse( "2016-11-23T10:43:44Z" ) );
    }

    @Test
    public void update_workflow_info()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            workflowInfo( WorkflowInfo.inProgress() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.workflowInfo = WorkflowInfo.create().state( WorkflowState.PENDING_APPROVAL ).checks(
                    Map.of( "Laywer review", WorkflowCheckState.PENDING ) ).build();
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertNotNull( storedContent.getWorkflowInfo().getState() );
        assertNotNull( storedContent.getWorkflowInfo().getChecks() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
        assertEquals( Map.of( "Laywer review", WorkflowCheckState.PENDING ), storedContent.getWorkflowInfo().getChecks() );
    }

    @Test
    public void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "testString", "value-updated" );
            } );

        Mockito.reset( auditLogService );

        this.contentService.update( updateContentParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.update" ) ;
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "id" ), result -> result.getString( "path" ) )
            .containsExactly( content.getId().toString(), content.getPath().toString() );
    }

    @Test
    public void audit_data_disabled()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        when( contentAuditLogFilterService.accept( any() ) ).thenReturn( false );

        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( data )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).editor( edit -> {
            final PropertyTree editData = edit.data;
            editData.setString( "testString", "value-updated" );
        } );

        Mockito.reset( auditLogService );

        this.contentService.update( updateContentParams );

        Mockito.verifyNoMoreInteractions( auditLogService );
    }

    private ExtraDatas createExtraDatas()
    {
        final XDataName xDataName = XDataName.from( "com.enonic.app.test:mixin" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( SiteDescriptor.create()
                                                                                                  .applicationKey( ApplicationKey.from(
                                                                                                      "com.enonic.app.test" ) )
                                                                                                  .xDataMappings( XDataMappings.from(
                                                                                                      XDataMapping.create()
                                                                                                          .xDataName( xDataName )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final XData xData = XData.create().name( xDataName ).form( Form.create().build() ).build();
        when( xDataService.getByName( xData.getName() ) ).thenReturn( xData );

        return ExtraDatas.create().add( new ExtraData( xDataName, new PropertyTree() ) ).build();
    }
}
