package com.enonic.xp.portal.impl.handler.api;

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
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaApiHandlerTest
    extends BaseHandlerTest
{

    private MediaApiHandler handler;

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

        this.handler = new MediaApiHandler( this.contentService, imageService, mediaInfoService );
        this.handler.activate( mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );

        this.request = new PortalRequest();
        this.request.setMethod( HttpMethod.GET );
        this.request.setBranch( ContentConstants.BRANCH_MASTER );
        this.request.setBaseUri( "" );
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
    void match()
    {
        this.request.setRawPath( "/api/com.enonic.app.appname" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/_/other/123456/scale-100-100/image-name.jpg" );
        assertFalse( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/api/media/image/default/master/123456/scale-100-100/image-name.jpg" );
        assertTrue( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/api/media/attachment/default/master/123456/attachment-name.jpg" );
        assertTrue( this.handler.canHandle( this.request ) );

        this.request.setRawPath( "/api/media/attachment/default/master/123456/attachment-name.jpg?download" );
        assertTrue( this.handler.canHandle( this.request ) );
    }

    @Test
    public void testAttachment()
        throws Exception
    {
        setupMedia();

        this.request.setRawPath( "/api/media/attachment/default/master/123456/logo.png" );

        final PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
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

        this.request.setRawPath( "/api/media/attachment/default/master/123456/logo.png?download" );
        this.request.getParams().put( "download", "" );

        PortalResponse res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );

        res = (PortalResponse) this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertNotNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testAttachmentDraftBranchForNotAuthorizedUser()
    {
        this.request.setRawPath( "/api/media/attachment/default/draft/123456/logo.png" );
        this.request.setBranch( ContentConstants.BRANCH_DRAFT );

        WebException exception =
            assertThrows( WebException.class, () -> this.handler.handle( this.request, PortalResponse.create().build(), null ) );
        assertEquals( HttpStatus.UNAUTHORIZED, exception.getStatus() );
        assertEquals( "You don't have permission to access this resource", exception.getMessage() );
    }

    @Test
    public void testImage()
        throws Exception
    {
        setupContent();

        this.request.setRawPath( "/api/media/image/default/master/123456/scale-100-100/image-name.jpg" );

        WebResponse res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );

        this.request.setRawPath( "/api/media/image/default/master/123456/scale-100-100/image-name.jpg" );

        res = this.handler.handle( this.request, PortalResponse.create().build(), null );
        assertNotNull( res );
        assertEquals( HttpStatus.OK, res.getStatus() );
        assertEquals( MediaType.PNG, res.getContentType() );
        assertTrue( res.getBody() instanceof ByteSource );
    }

}
