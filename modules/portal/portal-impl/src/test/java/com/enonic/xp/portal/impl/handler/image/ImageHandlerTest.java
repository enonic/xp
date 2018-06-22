package com.enonic.xp.portal.impl.handler.image;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfoService;
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

public class ImageHandlerTest
    extends BaseHandlerTest
{
    private ImageHandler handler;

    private ContentService contentService;

    private ImageService imageService;

    private MediaInfoService mediaInfoService;

    private PortalRequest request;

    @Before
    public final void setup()
        throws Exception
    {
        this.request = new PortalRequest();
        this.contentService = Mockito.mock( ContentService.class );
        this.imageService = Mockito.mock( ImageService.class );
        this.mediaInfoService = Mockito.mock( MediaInfoService.class );

        this.handler = new ImageHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setImageService( this.imageService );
        this.handler.setMediaInfoService( this.mediaInfoService );

        this.request.setMethod( HttpMethod.GET );
        this.request.setBaseUri( "/portal" );
        this.request.setContentPath( ContentPath.from( "/path/to/content" ) );
        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
    }

    private void setupContent()
        throws Exception
    {
        final Attachment attachment = Attachment.create().
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "source" ).
            build();

        final Content content = createContent( "123456", "path/to/image-name.jpg", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        Mockito.when( this.imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private void setupContentSvgz()
        throws Exception
    {
        final Attachment attachment = Attachment.create().
            name( "enonic-logo.svgz" ).
            mimeType( "image/svg+xml" ).
            label( "source" ).
            build();

        final Content content = createContent( "123456", "path/to/image-name.svgz", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        Mockito.when( this.imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private void setupContentSvg()
        throws Exception
    {
        final Attachment attachment = Attachment.create().
            name( "enonic-logo.svg" ).
            mimeType( "image/svg+xml" ).
            label( "source" ).
            build();

        final Content content = createContent( "123456", "path/to/image-name.svg", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        Mockito.when( this.imageService.readImage( Mockito.isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private Content createContent( final String id, final String contentPath, final Attachment... attachments )
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

        this.request.setEndpointPath( "/_/other/123456/scale-100-100/image-name.jpg" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/image/123456/scale-100-100/image-name.jpg" );
        assertEquals( false, this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
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
        setupContent();
        this.request.setMethod( HttpMethod.OPTIONS );
        this.request.setBaseUri( "/portal" );
        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/image/" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid image url pattern", e.getMessage() );
        }
    }

    @Test
    public void testImageFound()
        throws Exception
    {
        setupContent();

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
    }

    @Test
    public void testImageWithFilter()
        throws Exception
    {
        setupContent();

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
        this.request.getParams().put( "filter", "sepia()" );
        this.request.getParams().put( "quality", "75" );
        this.request.getParams().put( "background", "0x0" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
    }

    @Test
    public void testImageNotFound()
        throws Exception
    {
        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );

        try
        {
            this.handler.handle( this.request, PortalResponse.create().build(), null );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Content with id [123456] not found", e.getMessage() );
        }
    }

    @Test
    public void testImageWithOrientation()
        throws Exception
    {
        setupContent();
        Mockito.when( this.mediaInfoService.getImageOrientation( Mockito.any( ByteSource.class ) ) ).thenReturn(
            ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
        this.request.getParams().put( "filter", "sepia()" );
        this.request.getParams().put( "quality", "75" );
        this.request.getParams().put( "background", "0x0" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
    }

    @Test
    public void testSvgImage()
        throws Exception
    {
        setupContentSvg();
        Mockito.when( this.mediaInfoService.getImageOrientation( Mockito.any( ByteSource.class ) ) ).thenReturn(
            ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/full/image-name.svg" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.parse( "image/svg+xml" ), res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
    }

    @Test
    public void testSvgzImage()
        throws Exception
    {
        setupContentSvgz();
        Mockito.when( this.mediaInfoService.getImageOrientation( Mockito.any( ByteSource.class ) ) ).thenReturn(
            ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/full/image-name.svgz" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.parse( "image/svg+xml" ), res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertEquals( "gzip", res.getHeaders().get( "Content-Encoding" ) );
    }
}
