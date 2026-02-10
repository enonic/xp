package com.enonic.xp.portal.impl.handler;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttachmentHandlerTest
{
    private AttachmentHandler handler;

    private PortalRequest request;

    private ContentService contentService;

    private ByteSource mediaBytes;

    @BeforeEach
    final void setup()
        throws Exception
    {
        this.contentService = mock( ContentService.class );

        this.handler = new AttachmentHandler( this.contentService );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        this.request = new PortalRequest();
        this.request.setMode( RenderMode.LIVE );
        this.request.setMethod( HttpMethod.GET );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/path/to/content" ) );
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );

        setupMedia();
    }

    private void setupMedia()
    {
        final Attachment attachment = Attachment.create().name( "logo.png" ).mimeType( "image/png" ).label( "small" ).build();

        final Media content = createMedia( "123456", "path/to/content", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );
        when(
            this.contentService.getBinaryKey( eq( content.getId() ), eq( content.getMediaAttachment().getBinaryReference() ) ) ).thenReturn(
            "98765" );

        this.mediaBytes = ByteSource.wrap( new byte[]{'0', '1', '2', '3', '4', '5', '6'} );
        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( this.mediaBytes );
    }

    private Media createMedia( final String id, final String contentPath, final Attachment... attachments )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "media", attachments[0].getName() );

        return Media.create()
            .id( ContentId.from( id ) )
            .path( contentPath )
            .createdTime( Instant.now() )
            .type( ContentTypeName.imageMedia() )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
                              .build() )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .displayName( "My Content" )
            .modifiedTime( Instant.now() )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .data( data )
            .attachments( Attachments.from( attachments ) )
            .build();
    }

    @Test
    void options()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/download/123456/logo.png" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void notValidUrlPattern()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Not a valid attachment url pattern", e.getMessage() );
        }
    }

    @Test
    void inline()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void download()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/download/123456/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "attachment; filename=\"logo.png\"; filename*=UTF-8''logo.png", res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void idNotFound()
        throws Exception
    {
        when( this.contentService.getById( any() ) ).thenThrow( ContentNotFoundException.class );

        this.request.setRawPath( "/_/attachment/download/1/logo.png" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Content with id [1] not found", e.getMessage() );
        }
    }

    @Test
    void nameNotFound()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/download/123456/other.png" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final WebException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Attachment [other.png] not found for [/path/to/content]", e.getMessage() );
        }
    }

    @Test
    void byteServing()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=2-4" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 3, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[2], mediaBytesData[3], mediaBytesData[4]}, responseBody );
    }

    @Test
    void byteServingSuffixLength()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=-3" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 4, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[3], mediaBytesData[4], mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    void byteServingSuffixFrom()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=4-" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 3, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[4], mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    void byteServingLongerThanFile()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=5-1000" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();
        final byte[] mediaBytesData = mediaBytes.read();

        assertEquals( 2, responseBody.length );
        assertArrayEquals( new byte[]{mediaBytesData[5], mediaBytesData[6]}, responseBody );
    }

    @Test
    void byteServingMultipleRanges()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=0-1,3-4,6-" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( MediaType.parse( "multipart/byteranges" ), res.getContentType().withoutParameters() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );

        final byte[] responseBody = ( (ByteSource) res.getBody() ).read();

        final String responseMultipartString = new String( responseBody, StandardCharsets.UTF_8 );
        String[] responseMultipartLines = responseMultipartString.split( "\\r?\\n" );

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
    void byteServingInvalidRange()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456/logo.png" );
        this.request.getHeaders().put( "Range", "bytes=many" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertEquals( "bytes", res.getHeaders().get( "accept-ranges" ) );

        assertNull( res.getBody() );
    }

    @Test
    void cacheHeader()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456:98765/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertEquals( "public, max-age=31536000, immutable", res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    void cacheHeader_draft_branch()
        throws Exception
    {
        this.request.setBranch( ContentConstants.BRANCH_DRAFT );
        this.request.setRawPath( "/_/attachment/inline/123456:98765/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertEquals( "private, max-age=31536000, immutable", res.getHeaders().get( "Cache-Control" ) );
    }

    @Test
    void cacheHeader_fingerprint_mismatch()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456:123456/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );

        assertThat( res.getHeaders() ).doesNotContainKey( "Cache-Control" );
    }

    @Test
    void contentSecurityPolicy()
        throws Exception
    {
        this.request.setRawPath( "/_/attachment/inline/123456:98765/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertEquals( "default-src 'none'; base-uri 'none'; form-action 'none'", res.getHeaders().get( "Content-Security-Policy" ) );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.DELETE );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method DELETE not allowed", ex.getMessage() );
    }

    @Test
    void testHandleNotSiteBase()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/unknown" );
        portalRequest.setRawPath( "path-to-content/_/attachment/mode/id:version/name" );
        portalRequest.setRawPath( "/_/attachment/mode/id:version/name" );
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        when( rawRequest.isUserInRole( RoleKeys.ADMIN_LOGIN_ID ) ).thenReturn( true );
        portalRequest.setRawRequest( rawRequest );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( portalRequest ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid request", ex.getMessage() );
    }

}
