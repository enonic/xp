package com.enonic.xp.admin.impl.rest.resource.content;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentMediaResourceTest
    extends AdminResourceTestSupport
{
    private static final byte[] ATTACHMENT_DATA_1 = "data1".getBytes( StandardCharsets.UTF_8 );

    private static final byte[] ATTACHMENT_DATA_2 = "data2".getBytes( StandardCharsets.UTF_8 );

    @Rule
    public ExpectedException ex = ExpectedException.none();

    private ContentService contentService;

    private ContentMediaResource contentMediaResource;

    @Override
    protected ContentMediaResource getResourceInstance()
    {
        contentMediaResource = new ContentMediaResource();
        contentService = Mockito.mock( ContentService.class );

        contentMediaResource.setContentService( contentService );

        return contentMediaResource;
    }

    @Test
    public void media()
        throws Exception
    {
        final Media content = mockAttachmentBinary();

        MockRestResponse result = request().path( "content/media/" + content.getId().toString() ).
            queryParam( "contentId", content.getId().toString() ).get();

        assertTrue( result.getHeader( "Content-Disposition" ).startsWith( "attachment; filename=\"document.pdf\"" ) );
        Assert.assertArrayEquals( ATTACHMENT_DATA_1, result.getData() );
    }

    @Test
    public void media_identifier()
        throws Exception
    {
        final Media content = mockAttachmentBinary();

        MockRestResponse result = request().path( "content/media/" + content.getId().toString() + "/byName" ).
            queryParam( "contentId", content.getId().toString() ).get();

        assertTrue( result.getHeader( "Content-Disposition" ).startsWith( "attachment; filename=\"byName.pdf\"" ) );
        Assert.assertArrayEquals( ATTACHMENT_DATA_2, result.getData() );
    }

    @Test
    public void preview_media()
        throws Exception
    {
        final Media content = mockAttachmentBinary();

        MockRestResponse result = request().path( "content/media/preview/" + content.getId().toString() ).
            queryParam( "contentId", content.getId().toString() ).get();

        assertNull( result.getHeader( "Content-Disposition" ) );
        Assert.assertArrayEquals( ATTACHMENT_DATA_1, result.getData() );
    }

    @Test
    public void preview_media_identifier()
        throws Exception
    {
        final Media content = mockAttachmentBinary();

        MockRestResponse result = request().path( "content/media/preview/" + content.getId().toString() + "/byName" ).
            queryParam( "contentId", content.getId().toString() ).get();

        assertNull( result.getHeader( "Content-Disposition" ) );
        Assert.assertArrayEquals( ATTACHMENT_DATA_2, result.getData() );
    }

    @Test
    public void isAllowPreview()
        throws Exception
    {
        final Media content = mockAttachmentBinary();

        String result = request().path( "content/media/isAllowPreview" ).
            queryParam( "contentId", content.getId().toString() ).get().getAsString();

        assertEquals( "true", result );
    }

    @Test
    public void isAllowPreview_empty()
        throws Exception
    {
        final Media content = createMedia();

        String result = request().path( "content/media/isAllowPreview" ).
            queryParam( "contentId", content.getId().toString() ).get().getAsString();

        assertEquals( "false", result );
    }

    @Test
    public void isAllowPreview_unsupported()
        throws Exception
    {
        final Media content = addAttachment( createMediaBuilder(), Attachment.create().
            name( "word.doc" ).
            label( "word" ).
            mimeType( "application/msword" ).
            size( 12345 ).
            build() ).
            build();

        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        String result = request().path( "content/media/isAllowPreview" ).
            queryParam( "contentId", content.getId().toString() ).
            queryParam( "identifier", "word" ).
            get().getAsString();

        assertEquals( "false", result );
    }

    @Test
    public void media_content_empty()
        throws Exception
    {
        ex.expect( WebApplicationException.class );
        ex.expectMessage( "Content [id] was not found" );

        contentMediaResource.media( "id" );
    }

    @Test
    public void media_attachment_empty()
        throws Exception
    {
        Media media = createMedia();

        ex.expect( WebApplicationException.class );
        ex.expectMessage( "Content [" + media.getId().toString() + "] has no attachments" );

        contentMediaResource.media( media.getId().toString() );
    }

    @Test
    public void media_attachment_unsupported()
        throws Exception
    {
        final Media media = addAttachment( createMediaBuilder(), Attachment.create().
            name( "word.doc" ).
            label( "word" ).
            mimeType( "application/msword" ).
            size( 12345 ).
            build() ).
            build();

        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        ex.expect( WebApplicationException.class );
        ex.expectMessage( "Preview for attachment [word.doc] is not supported" );

        contentMediaResource.previewMedia( media.getId().toString(), "word" );
    }

    private Media mockAttachmentBinary()
    {
        Media.Builder builder = createMediaBuilder();

        builder = addAttachment( builder, Attachment.create().
            name( "document.pdf" ).
            label( "source" ).
            mimeType( "application/pdf" ).
            size( 12345 ).
            build() );
        builder = addAttachment( builder, Attachment.create().
            name( "byName.pdf" ).
            label( "byName" ).
            mimeType( "application/pdf" ).
            size( 12345 ).
            build() );

        Media content = builder.build();

        final Attachment attachment1 = content.getAttachments().byName( "document.pdf" );
        final Attachment attachment2 = content.getAttachments().byName( "byName.pdf" );

        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        Mockito.when( this.contentService.getBinary( content.getId(), attachment1.getBinaryReference() ) ).thenReturn(
            ByteSource.wrap( ATTACHMENT_DATA_1 ) );
        Mockito.when( this.contentService.getBinary( content.getId(), attachment2.getBinaryReference() ) ).thenReturn(
            ByteSource.wrap( ATTACHMENT_DATA_2 ) );

        return content;
    }

    private Media.Builder addAttachment( final Media.Builder media, final Attachment attachment )
    {
        final Media source = media.build();
        final Attachments attachments = source.getAttachments().add( attachment );

        return Media.create( source ).attachments( attachments );
    }

    private Media.Builder createMediaBuilder()
    {
        return Media.create().
            id( ContentId.from( "content-id" ) ).
            path( "/path/to/content" ).
            displayName( "Content Display Name" ).
            type( ContentTypeName.documentMedia() ).
            name( "content" );
    }

    private Media createMedia()
    {
        Media media = createMediaBuilder().build();
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );

        return media;
    }
}
