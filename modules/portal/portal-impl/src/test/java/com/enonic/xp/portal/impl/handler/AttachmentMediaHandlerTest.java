package com.enonic.xp.portal.impl.handler;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttachmentMediaHandlerTest
{
    private static final String ETAG = "\"ec25d6e4126c7064f82aaab8b34693fc\"";

    private AttachmentMediaHandler handler;

    private PortalRequest request;

    private ContentService contentService;

    private ByteSource mediaBytes;

    @BeforeEach
    final void setup()
    {
        this.contentService = mock( ContentService.class );
        this.handler = new AttachmentMediaHandler( this.contentService, mock( ProjectService.class ) );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        this.request = new PortalRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/" ) );
    }

    private void setupMedia()
    {
        final Attachment attachment = Attachment.create()
            .name( "logo.png" )
            .mimeType( "image/png" )
            .label( "small" )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" )
            .build();

        final Media content = createMedia( "123456", "path/to/content", attachment );

        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( this.contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );

        this.mediaBytes = ByteSource.wrap( new byte[]{'0', '1', '2', '3', '4', '5', '6'} );
        when( this.contentService.getBinary( isA( ContentId.class ), isA( BinaryReference.class ) ) ).thenReturn( this.mediaBytes );
    }

    @Test
    void testInvalidUrl()
    {
        this.request.setBaseUri( "" );
        // missing name segment — only project and id
        this.request.setRawPath( "/api/media:attachment/myproject/123456" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testInvalidUrlWithoutIdAndFingerprint()
    {
        this.request.setBaseUri( "" );
        // must be /api/media:attachment/myproject/<id>[:<fingerprint>]/logo.png
        this.request.setRawPath( "/api/media:attachment/myproject/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
    }

    @Test
    void testAttachment()
    {
        setupMedia();

        WebRequest request = new WebRequest();

        request.setMethod( HttpMethod.GET );
        request.setRawPath( "/api/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
        assertNotNull( res.getHeaders().get( HttpHeaders.CACHE_CONTROL ) );
    }

    @Test
    void testAttachmentForEndpointOnAdmin()
    {
        setupMedia();

        this.request.setBaseUri( "/admin" );
        this.request.setRawPath( "/admin/app/toolName/_/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/logo.png" );

        WebResponse response = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, response.getStatus() );
    }

    @Test
    void testAttachmentForEndpointOnWebApp()
    {
        setupMedia();

        this.request.setBaseUri( "/webapp/com.enonic.app.mywebapp" );
        this.request.setRawPath( "/webapp/com.enonic.app.mywebapp/_/media:attachment/myproject/123456/logo.png" );

        WebResponse res = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, res.getStatus() );
    }

    @Test
    void testAttachmentForEndpointOnSite()
    {
        setupMedia();

        this.request.setBaseUri( "/site" );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setContentPath( ContentPath.from( "/mysite" ) );
        this.request.setRawPath( "/site/myproject/master/mysite/_/media:attachment/myproject/123456/logo.png" );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        this.request.setSite( site );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void testMediaEndpointBaseUrlContextIsDifferentPathContext()
    {
        setupMedia();

        this.request.setBaseUri( "/site" );
        this.request.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject1" ) );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setContentPath( ContentPath.from( "/mysite" ) );
        this.request.setRawPath( "/site/myproject1/master/mysite/_/media:attachment/myproject/123456/logo.png" );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        this.request.setSite( site );

        // media.scope does not specify
        WebResponse webResponse = this.handler.handle( this.request );
        assertEquals( HttpStatus.OK, webResponse.getStatus() );

        // media.scope is different from the baseUrl context
        ContextBuilder.copyOf( ContextAccessor.current() ).attribute( "media.scope", "myproject:draft" ).build().runWith( () -> {
            WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
            assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
            assertEquals( "Not a valid media url pattern", ex.getMessage() );
        } );
    }

    @Test
    void testAttachmentDownload()
    {
        setupMedia();

        WebRequest request = new WebRequest();

        request.setMethod( HttpMethod.GET );
        request.setRawPath( "/api/media:attachment/myproject/123456/logo.png" );
        request.getParams().put( "q1", "v1" );
        request.getParams().put( "q2", "v2" );
        request.getParams().put( "download", "" );

        PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );

        res = (PortalResponse) this.handler.handle( request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void testOptions()
    {
        this.request.setBaseUri( "" );
        this.request.setRawPath( "/api/media:attachment/myproject:draft/123456/logo.png" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setBaseUri( "" );
        this.request.setMethod( HttpMethod.DELETE );
        this.request.setRawPath( "/api/media:attachment/myproject:draft/123456/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method DELETE not allowed", ex.getMessage() );
    }

    @Test
    void testAttachmentUnderAdminSite()
    {
        setupMedia();

        this.request.setBaseUri( "/admin/site/preview" );
        this.request.setRawPath( "/_/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/logo.png" );
        this.request.setRawPath(
            "/admin/site/preview/myproject/master/mysite/_/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    private Media createMedia( final String id, final String contentPath, final Attachment... attachments )
    {
        final PropertyTree data = new PropertyTree();
        data.addSet( "media" ).addString( "attachment", attachments[0].getName() );

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

    private WebRequest attachmentRequest( final String rawPath )
    {
        setupMedia();
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.GET );
        request.setRawPath( rawPath );
        return request;
    }

    private WebRequest attachmentRequest()
    {
        return attachmentRequest( "/api/media:attachment/myproject/123456/logo.png" );
    }

    @Test
    void etagFromAttachmentHash()
    {
        final PortalResponse res = (PortalResponse) this.handler.handle( attachmentRequest() );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( ETAG, res.getHeaders().get( "ETag" ) );
        assertEquals( "bytes", res.getHeaders().get( "Accept-Ranges" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void ifNoneMatchMatches()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "If-None-Match", ETAG );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.NOT_MODIFIED, res.getStatus() );
        assertEquals( Map.of( "ETag", ETAG ), res.getHeaders() );
        assertNull( res.getBody() );
        verify( this.contentService, never() ).getBinary( any(), any() );
    }

    @Test
    void ifNoneMatchOmitsRepresentationHeaders()
    {
        final WebRequest request = attachmentRequest( "/api/media:attachment/myproject/123456/logo.png" );
        request.getParams().put( "download", "" );

        final PortalResponse ok = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.OK, ok.getStatus() );
        assertNotNull( ok.getHeaders().get( "Content-Disposition" ) );
        assertNotNull( ok.getHeaders().get( "Content-Security-Policy" ) );

        request.getHeaders().put( "If-None-Match", ETAG );
        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.NOT_MODIFIED, res.getStatus() );
        assertEquals( Map.of( "ETag", ETAG ), res.getHeaders() );
    }

    @Test
    void ifNoneMatchHead()
    {
        final WebRequest request = attachmentRequest();
        request.setMethod( HttpMethod.HEAD );
        request.getHeaders().put( "If-None-Match", ETAG );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.NOT_MODIFIED, res.getStatus() );
        assertNull( res.getBody() );
    }

    @Test
    void ifNoneMatchWeakTag()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "If-None-Match", "W/" + ETAG );

        assertEquals( HttpStatus.NOT_MODIFIED, this.handler.handle( request ).getStatus() );
    }

    @Test
    void ifNoneMatchList()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "If-None-Match", "\"other\", " + ETAG );

        assertEquals( HttpStatus.NOT_MODIFIED, this.handler.handle( request ).getStatus() );
    }

    @Test
    void ifNoneMatchStar()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "If-None-Match", "*" );

        assertEquals( HttpStatus.NOT_MODIFIED, this.handler.handle( request ).getStatus() );
    }

    @Test
    void ifNoneMatchMismatch()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "If-None-Match", "\"other\"" );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( ETAG, res.getHeaders().get( "ETag" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void ifNoneMatchKeepsCacheControl()
    {
        final WebRequest request = attachmentRequest( "/api/media:attachment/myproject/123456:ec25d6e4126c7064f82aaab8b34693fc/logo.png" );
        request.getHeaders().put( "If-None-Match", ETAG );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.NOT_MODIFIED, res.getStatus() );
        assertEquals( Map.of( "ETag", ETAG, "Cache-Control", "public, max-age=31536000, immutable" ), res.getHeaders() );
    }

    @Test
    void ifRangeMatches()
        throws Exception
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "Range", "bytes=2-4" );
        request.getHeaders().put( "If-Range", ETAG );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.PARTIAL_CONTENT, res.getStatus() );
        assertEquals( "bytes 2-4/7", res.getHeaders().get( "Content-Range" ) );
        assertEquals( ETAG, res.getHeaders().get( "ETag" ) );
        assertEquals( 3, ( (ByteSource) res.getBody() ).read().length );
    }

    @Test
    void ifRangeMismatchIgnoresRange()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "Range", "bytes=2-4" );
        request.getHeaders().put( "If-Range", "\"other\"" );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertNull( res.getHeaders().get( "Content-Range" ) );
        assertEquals( "bytes", res.getHeaders().get( "Accept-Ranges" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void ifRangeWeakTagIgnoresRange()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "Range", "bytes=2-4" );
        request.getHeaders().put( "If-Range", "W/" + ETAG );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    void ifRangeDateIgnoresRange()
    {
        final WebRequest request = attachmentRequest();
        request.getHeaders().put( "Range", "bytes=2-4" );
        request.getHeaders().put( "If-Range", "Wed, 21 Oct 2015 07:28:00 GMT" );

        final PortalResponse res = (PortalResponse) this.handler.handle( request );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertSame( this.mediaBytes, res.getBody() );
    }
}
