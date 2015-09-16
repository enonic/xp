package com.enonic.xp.portal.impl.handler.image;

import java.time.Instant;

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
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.portal.impl.handler.BaseHandlerTest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class ImageHandlerTest
    extends BaseHandlerTest
{
    private ImageHandler handler;

    private ContentService contentService;

    private ImageService imageService;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.imageService = Mockito.mock( ImageService.class );

        this.handler = new ImageHandler();
        this.handler.setContentService( this.contentService );
        this.handler.setImageService( this.imageService );

        this.request.setMethod( HttpMethod.GET );
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
        assertMethodNotAllowed( this.handler, HttpMethod.POST );
        assertMethodNotAllowed( this.handler, HttpMethod.DELETE );
        assertMethodNotAllowed( this.handler, HttpMethod.PUT );
        assertMethodNotAllowed( this.handler, HttpMethod.TRACE );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( HttpMethod.OPTIONS );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/image/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
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

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
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

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
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
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Content with id [123456] not found", e.getMessage() );
        }
    }
}
