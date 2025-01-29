package com.enonic.xp.portal.impl.handler.image;

import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
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
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageHandlerTest
    extends BaseHandlerTest
{
    private ImageHandler handler;

    private ContentService contentService;

    private ImageService imageService;

    private MediaInfoService mediaInfoService;

    private PortalRequest request;

    @BeforeEach
    final void setup()
    {
        this.request = new PortalRequest();
        this.contentService = mock( ContentService.class );
        this.imageService = mock( ImageService.class );
        this.mediaInfoService = mock( MediaInfoService.class );

        this.handler = new ImageHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setImageService( this.imageService );
        this.handler.setMediaInfoService( this.mediaInfoService );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        this.request.setMethod( HttpMethod.GET );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
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

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
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

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
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

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).
            thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }

    private void setupImageContent( final String type )
        throws Exception
    {
        final Attachment attachment =
            Attachment.create().name( "enonic-logo." + type ).mimeType( "image/" + type ).label( "source" ).build();

        final Content content = createContent( "123456", "path/to/image-name." + type, attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
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
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( RoleKeys.EVERYONE ).
                    allow( Permission.READ ).
                    build() ).
                build() ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            data( data ).
            attachments( Attachments.from( attachments ) ).
            build();
    }

    private void mockCachableContent()
        throws IOException
    {
        final Attachment attachment = Attachment.create().
            name( "image-name.jpg" ).
            mimeType( "image/jpeg" ).
            label( "source" ).
            build();

        final Content content = createContent( "123456", "path/to/image-name.jpg", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        final ByteSource imageBytes = ByteSource.wrap( new byte[0] );

        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( imageBytes );

        when( this.imageService.readImage( isA( ReadImageParams.class ) ) ).thenReturn( imageBytes );
    }


    @Test
    void order()
    {
        assertEquals( 0, this.handler.getOrder() );
    }

    @Test
    void match()
    {
        this.request.setEndpointPath( null );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/other/123456/scale-100-100/image-name.jpg" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/image/123456/scale-100-100/image-name.jpg" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    void methodNotAllowed()
        throws Exception
    {
        assertMethodNotAllowed( this.handler, HttpMethod.POST, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.DELETE, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.PUT, this.request );
        assertMethodNotAllowed( this.handler, HttpMethod.TRACE, this.request );
    }

    @Test
    void options()
        throws Exception
    {
        setupContent();
        this.request.setMethod( HttpMethod.OPTIONS );
        this.request.setBaseUri( "/site" );
        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void notValidUrlPattern()
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
    void imageFound()
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
    void imageWithFilter()
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
    void imageNotFound()
        throws Exception
    {
        when( this.contentService.getById( ContentId.from( "654321" ) ) ).thenThrow( ContentNotFoundException.class );

        this.request.setEndpointPath( "/_/image/654321/scale-100-100/image-name.jpg" );
        final WebException webException =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.NOT_FOUND, webException.getStatus() );
        assertEquals( "Content with id [654321] not found", webException.getMessage() );
    }

    @Test
    void imageWithOrientation()
        throws Exception
    {
        setupContent();
        when( this.mediaInfoService.getImageOrientation( any( ByteSource.class ) ) ).thenReturn( ImageOrientation.LeftBottom );

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
    void invalidQuality()
        throws Exception
    {
        setupContent();
        when( this.mediaInfoService.getImageOrientation( any( ByteSource.class ) ) ).thenReturn( ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg" );
        this.request.getParams().put( "quality", "-1" );

        final WebException webException =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.BAD_REQUEST, webException.getStatus() );
    }

    @Test
    void nameMissmatch()
        throws Exception
    {
        setupContent();

        this.request.setEndpointPath( "/_/image/123456/full/image-name.png" );

        final WebException webException =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.NOT_FOUND, webException.getStatus() );
    }

    @Test
    void gifImage()
        throws Exception
    {
        setupImageContent("gif");

        this.request.setEndpointPath( "/_/image/123456/full/image-name.gif" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.GIF, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
    }

    @Test
    void webpImage()
        throws Exception
    {
        setupImageContent("webp");

        this.request.setEndpointPath( "/_/image/123456/full/image-name.webp" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.WEBP, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
    }

    @Test
    void avifImage()
        throws Exception
    {
        setupImageContent("avif");

        this.request.setEndpointPath( "/_/image/123456/full/image-name.avif" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.create( "image", "avif" ), res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
    }


    @Test
    void svgImage()
        throws Exception
    {
        setupContentSvg();
        when( this.mediaInfoService.getImageOrientation( any( ByteSource.class ) ) ).thenReturn( ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/full/image-name.svg" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.SVG_UTF_8.withoutParameters(), res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertNull( res.getHeaders().get( "Content-Encoding" ) );
        assertEquals( "default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'", res.getHeaders().get( "Content-Security-Policy" ) );
    }

    @Test
    void svgzImage()
        throws Exception
    {
        setupContentSvgz();
        when( this.mediaInfoService.getImageOrientation( any( ByteSource.class ) ) ).thenReturn( ImageOrientation.LeftBottom );

        this.request.setEndpointPath( "/_/image/123456/full/image-name.svgz" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.SVG_UTF_8.withoutParameters(), res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
        assertEquals( "gzip", res.getHeaders().get( "Content-Encoding" ) );
        assertEquals( "default-src 'none'; base-uri 'none'; form-action 'none'; style-src 'self' 'unsafe-inline'", res.getHeaders().get( "Content-Security-Policy" ) );
    }

    @Test
    void get()
        throws Exception
    {
        mockCachableContent();

        this.request.setEndpointPath( "/_/image/123456/scale-100-100/image-name.jpg.png" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertAll( () -> assertEquals( HttpStatus.OK, res.getStatus() ), () -> assertEquals( MediaType.PNG, res.getContentType() ),
                   () -> assertThat( res.getHeaders() ).doesNotContainKey( "Cache-Control" ) );
    }

    @Test
    void cacheHeader()
        throws Exception
    {
        mockCachableContent();

        this.request.setEndpointPath( "/_/image/123456:bb6d2c0f3112f562ec454654b9aebe7ab47ba865/scale-100-100/image-name.jpg.png" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );

        assertEquals( "public, max-age=31536000, immutable", res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    void cacheHeader_draft_branch()
        throws Exception
    {
        mockCachableContent();

        this.request.setEndpointPath( "/_/image/123456:bb6d2c0f3112f562ec454654b9aebe7ab47ba865/scale-100-100/image-name.jpg.png" );

        this.request.setBranch( ContentConstants.BRANCH_DRAFT );
        final WebResponse resDraft = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertEquals( "private, max-age=31536000, immutable", resDraft.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    void cacheHeader_fingerprint_missmatch()
        throws Exception
    {
        mockCachableContent();

        this.request.setEndpointPath( "/_/image/123456:654321/scale-100-100/image-name.jpg.png" );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );

        assertThat( res.getHeaders() ).doesNotContainKey( "Cache-Control" );
    }
}
