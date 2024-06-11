package com.enonic.xp.portal.impl.handler;

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
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
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
import com.enonic.xp.site.Site;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaHandlerTest
{
    private MediaHandler handler;

    private PortalRequest request;

    private ContentService contentService;

    private ImageService imageService;

    private ByteSource mediaBytes;

    @BeforeEach
    final void setup()
    {
        this.contentService = mock( ContentService.class );
        this.imageService = mock( ImageService.class );
        MediaInfoService mediaInfoService = mock( MediaInfoService.class );

        this.handler = new MediaHandler( this.contentService, imageService, mediaInfoService );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        this.request = new PortalRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "/site" );
        this.request.setContentPath( ContentPath.from( "/path/to/content" ) );
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

    private void setupContent()
        throws Exception
    {
        final Attachment attachment = Attachment.create().name( "enonic-logo.png" ).mimeType( "image/png" ).label( "source" ).build();

        final Content content = createContent( "123456", "path/to/image-name.jpg", attachment );

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
    public void testAttachment()
        throws Exception
    {
        setupMedia();

        this.request.setRawPath( "/api/media/attachment/myproject/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testAttachmentForEndpointOnAdmin()
        throws Exception
    {
        setupMedia();

        this.request.setEndpointPath( "/_/media/attachment/myproject/123456/logo.png" );
        this.request.setRawPath( "/admin/tool/_/media/attachment/myproject/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testAttachmentForEndpointOnWebApp()
        throws Exception
    {
        setupMedia();

        this.request.setEndpointPath( "/_/media/attachment/myproject/123456/logo.png" );
        this.request.setRawPath( "/webapp/com.enonic.app.mywebapp/_/media/attachment/myproject/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testInvalidContextPath()
    {
        setupMedia();

        this.request.setEndpointPath( "/_/media/attachment/myproject/123456/logo.png" );
        this.request.setRawPath( "/webapp/com.enonic.app.mywebapp/path/_/media/attachment/myproject/123456/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request, WebResponse.create().build() ) );
        assertEquals( HttpStatus.NOT_FOUND, ex.getStatus() );
        assertEquals( "Not a valid media url pattern", ex.getMessage() );
    }

    @Test
    public void testAttachmentForEndpointOnSite()
        throws Exception
    {
        setupMedia();

        this.request.setContentPath( ContentPath.from( "/mysite" ) );
        this.request.setEndpointPath( "/_/media/attachment/myproject/123456/logo.png" );
        this.request.setRawPath( "/site/myproject/master/mysite/_/media/attachment/myproject/123456/logo.png" );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( any() ) ).thenReturn( site );
        when( contentService.findNearestSiteByPath( any() ) ).thenReturn( site );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testAttachmentDownload()
        throws Exception
    {
        setupMedia();

        this.request.setRawPath( "/api/media/attachment/myproject/123456/logo.png?q1=v1&q2=v2&download" );
        this.request.getParams().put( "q1", "v1" );
        this.request.getParams().put( "q2", "v2" );
        this.request.getParams().put( "download", "" );

        PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );

        res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testAttachmentDraftBranchForNotAuthorizedUser()
    {
        this.request.setRawPath( "/api/media/attachment/myproject:draft/123456/logo.png" );
        this.request.setBranch( ContentConstants.BRANCH_DRAFT );

        WebException exception =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build() ) );
        assertEquals( HttpStatus.UNAUTHORIZED, exception.getStatus() );
        assertEquals( "You don't have permission to access this resource", exception.getMessage() );
    }

    @Test
    public void testImage()
        throws Exception
    {
        setupContent();

        this.request.setRawPath( "/api/media/image/myproject/123456/scale-100-100/image-name.jpg" );

        WebResponse res = this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertInstanceOf( ByteSource.class, res.getBody() );

        this.request.setRawPath( "/api/media/image/myproject/123456/scale-100-100/image-name.jpg" );

        res = this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertInstanceOf( ByteSource.class, res.getBody() );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setRawPath( "/api/media/attachment/myproject:draft/123456/logo.png" );
        this.request.setMethod( HttpMethod.OPTIONS );

        final WebResponse res = this.handler.handle( this.request, PortalResponse.create().build() );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    void testHandleMethodNotAllowed()
    {
        this.request.setMethod( HttpMethod.DELETE );
        this.request.setRawPath( "/api/media/attachment/myproject:draft/123456/logo.png" );

        WebException ex = assertThrows( WebException.class, () -> this.handler.handle( this.request, WebResponse.create().build() ) );
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, ex.getStatus() );
        assertEquals( "Method DELETE not allowed", ex.getMessage() );
    }
}
