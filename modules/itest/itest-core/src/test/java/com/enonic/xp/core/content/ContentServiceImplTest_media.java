package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDatas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentServiceImplTest_media
    extends AbstractContentServiceTest
{

    @Test
    public void create_media_image()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getData().getString( ContentPropertyNames.MEDIA ) );
        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
    }

    @Test
    public void create_media_image_invalid_file_name()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        // file ending with point is illegal on Windows
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "cat-small." ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        assertThrows( IllegalArgumentException.class, () -> this.contentService.create( createMediaParams ) );
    }

    @Test
    public void create_media_image_invalid_file_name_allowed_by_config()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        // file ending with point is illegal on Windows
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "cat-small." ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final ContentConfig contentConfig = mock( ContentConfig.class );
        when( contentConfig.attachments_allowUnsafeNames() ).thenReturn( true );

        contentService.initialize( contentConfig );
        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
        assertEquals( attachments.get( 0 ).getName(), "cat-small." );
    }

    @Test
    public void no_file_extension_in_display_name()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat.jpg" ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( "Small cat.jpg", storedContent.getName().toString() );
        assertEquals( "Small cat", storedContent.getDisplayName() );
    }


    @Test
    public void update_media_image()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).name( "Small cat" ).parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( WorkflowState.READY, content.getWorkflowInfo().getState() );

        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().content( content.getId() )
            .name( "dart-small" )
            .byteSource( loadImage( "darth-small.jpg" ) )
            .workflowInfo( WorkflowInfo.inProgress() );

        this.contentService.update( updateMediaParams );

        final Content updatedContent = this.contentService.getById( storedContent.getId() );

        assertEquals( WorkflowState.IN_PROGRESS, updatedContent.getWorkflowInfo().getState() );

        final Attachments attachments = updatedContent.getAttachments();

        assertNotNull( attachments );
        assertEquals( 1, attachments.getSize() );

        for ( final Attachment attachment : attachments )
        {
            assertTrue( attachment.getName().startsWith( "dart-small" ) );
        }
    }

    @Test
    public void update_media_image_skip_not_changed()
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).name( "Small cat" ).parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().content( content.getId() )
            .name( "Small cat" )
            .byteSource( loadImage( "cat-small.jpg" ) );

        this.contentService.update( updateMediaParams );

        final Content updatedContent = this.contentService.getById( storedContent.getId() );
        assertEquals( content.getModifiedTime(), updatedContent.getModifiedTime() );
    }

    @Test
    public void update_media_image_invalid_file_name()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        // file ending with point is illegal on Windows
        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().
            content( content.getId() ).
            name( "dart-small." ).
            byteSource( loadImage( "darth-small.jpg" ) );

        assertThrows( IllegalArgumentException.class, () -> this.contentService.update( updateMediaParams ) );
    }


    @Test
    public void audit_data()
        throws Exception
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

        final Content content = this.contentService.create( createMediaParams );

        Mockito.verify( auditLogService, Mockito.timeout( 5000 ).atLeast( 15 ) ).log( captor.capture() );

        final PropertySet logResultSet = captor.getAllValues()
            .stream()
            .filter( log -> log.getType().equals( "system.content.create" ) )
            .findFirst()
            .get()
            .getData()
            .getSet( "result" );

        assertEquals( content.getId().toString(), logResultSet.getString( "id" ) );
        assertEquals( content.getPath().toString(), logResultSet.getString( "path" ) );
    }

    @Test
    public void create_media_document()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "document.pdf" ) ).
            name( "document.pdf" ).
            mimeType( "application/pdf" ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getData().getString( ContentPropertyNames.MEDIA ) );
        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
    }

}
