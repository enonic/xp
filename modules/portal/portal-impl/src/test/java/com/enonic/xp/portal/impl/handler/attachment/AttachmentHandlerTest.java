package com.enonic.xp.portal.impl.handler.attachment;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.Assert.*;

public class AttachmentHandlerTest
    extends BaseHandlerTest
{
    private AttachmentHandler handler;

    private PortalRequest request;

    private ContentService contentService;

    private ByteSource mediaBytes;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.contentService = Mockito.mock( ContentService.class );
        this.handler = new AttachmentHandler();
        this.handler.setContentService( this.contentService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setBaseUri( "/portal" );
        this.request.setContentPath( ContentPath.from( "/path/to/content" ) );
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );

        setupMedia();
    }

    private void setupMedia()
        throws Exception
    {
        final Attachment attachment = Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();

        final Media content = createMedia( "123456", "path/to/content", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        this.mediaBytes = ByteSource.wrap( new byte[]{'0', '1', '2', '3', '4', '5', '6'} );
        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( this.mediaBytes );
    }

    private Media createMedia( final String id, final String contentPath, final Attachment... attachments )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "media", attachments[0].getName() );

        return Media.create().
            id( ContentId.from( id ) ).
            path( contentPath ).
            createdTime( Instant.now() ).
            type( ContentTypeName.imageMedia() ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            data( data ).
            attachments( Attachments.from( attachments ) ).
            build();
    }

    @Test
    public void testOrder()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    public void testMatch()
    {
        this.request.setEndpointPath( null );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/inline/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/attachment/inline/a/b" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/attachment/inline/a/b" );
        assertEquals( true, this.handler.canHandle( this.request ) );
    }

    @Test
    public void testMethodNotAllowed()
        throws Exception
    {
        assertMethodNotAllowed( this.handler, HttpMethod.POST, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.DELETE, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.PUT, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.TRACE, this.request );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/123456/logo.png" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid attachment url pattern", e.getMessage() );
        }
    }

    @Test
    public void testInline()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testDownload()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "attachment; filename=\"logo.png\"; filename*=UTF-8''logo.png", res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testIdNotFound()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/1/logo.png" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Content with id [1] not found", e.getMessage() );
        }
    }

    @Test
    public void testNameNotFound()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/123456/other.png" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Attachment [other.png] not found for [/path/to/content]", e.getMessage() );
        }
    }

    @Test
    public void testByteServing()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=2-4" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 3, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[2], mediaBytesData[3], mediaBytesData[4]}, responseBody );
    }

    @Test
    public void testByteServingSuffixLength()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=-3" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 4, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[3], mediaBytesData[4], mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    public void testByteServingSuffixFrom()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=4-" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 3, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[4], mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    public void testByteServingLongerThanFile()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=5-1000" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 2, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    public void testByteServingMultipleRanges()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=0-1,3-4,6-" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.parse( "multipart/byteranges" ), res.getContentType().withoutParameters() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();

        final String responseMultipartString = new String( responseBody, StandardCharsets.UTF_8 );
        String responseMultipartLines[] = responseMultipartString.split( "\\r?\\n" );

        assertEquals( "Content-Type: image/png", responseMultipartLines[2] );
        assertEquals( "Content-Range: bytes 0-1/7", responseMultipartLines[3] );
        assertEquals( "01", responseMultipartLines[5] );

        assertEquals( "Content-Type: image/png", responseMultipartLines[7] );
        assertEquals( "Content-Range: bytes 3-4/7", responseMultipartLines[8] );
        assertEquals( "34", responseMultipartLines[10] );

        assertEquals( "Content-Type: image/png", responseMultipartLines[12] );
        assertEquals( "Content-Range: bytes 6-6/7", responseMultipartLines[13] );
        assertEquals( "6", responseMultipartLines[15] );
    }

    @Test
    public void testByteServingInvalidRange()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=many" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, res.getStatus() );
        assertEquals( MediaType.PNG.withoutParameters(), res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );

        assertNull( res.getBody() );
    }
}
