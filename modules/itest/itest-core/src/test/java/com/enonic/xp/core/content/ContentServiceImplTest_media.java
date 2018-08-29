package com.enonic.xp.core.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDatas;

import static org.junit.Assert.*;

public class ContentServiceImplTest_media
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

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
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

        Mockito.when( this.xDataService.getFromContentType( Mockito.any( ContentType.class ) ) ).thenReturn( XDatas.empty() );
        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().
            content( content.getId() ).
            name( "dart-small" ).
            byteSource( loadImage( "darth-small.jpg" ) );

        this.contentService.update( updateMediaParams );

        final Content updatedContent = this.contentService.getById( storedContent.getId() );

        final Attachments attachments = updatedContent.getAttachments();

        assertNotNull( attachments );
        assertEquals( 1, attachments.getSize() );

        for ( final Attachment attachment : attachments )
        {
            assertTrue( attachment.getName().startsWith( "dart-small" ) );
        }
    }
}
