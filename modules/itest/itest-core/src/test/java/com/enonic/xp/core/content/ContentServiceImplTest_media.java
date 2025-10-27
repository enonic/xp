package com.enonic.xp.core.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationService;
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
import com.enonic.xp.core.impl.content.XDataMappingServiceImpl;
import com.enonic.xp.core.impl.schema.xdata.XDataServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentServiceImplTest_media
    extends AbstractContentServiceTest
{

    @BeforeEach
    public void beforeEach()
        throws Exception
    {
        xDataService = new XDataServiceImpl( mock( ApplicationService.class ), resourceService );
        xDataMappingService = new XDataMappingServiceImpl( siteService, xDataService );
        contentService.setxDataService( xDataService );
        contentService.setXDataMappingService( xDataMappingService );
    }

    @Test
    public void create_media_image()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

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

        when( config.attachments_allowUnsafeNames() ).thenReturn( true );

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

        final Content content = this.contentService.create( createMediaParams );

        // file ending with point is illegal on Windows
        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().
            content( content.getId() ).
            name( "dart-small." ).
            byteSource( loadImage( "darth-small.jpg" ) );

        assertThrows( IllegalArgumentException.class, () -> this.contentService.update( updateMediaParams ) );
    }


    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            altText( "alt text" ).
            caption( "caption" ).
            copyright( "copyright" ).
            artist( "artist" ).
            parent( ContentPath.ROOT );

        Mockito.reset( auditLogService );

        final Content content = this.contentService.create( createMediaParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.create" ) ;
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "id" ), result -> result.getString( "path" ) )
            .containsExactly( content.getId().toString(), content.getPath().toString() );
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
