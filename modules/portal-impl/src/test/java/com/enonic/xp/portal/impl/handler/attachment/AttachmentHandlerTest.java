package com.enonic.xp.portal.impl.handler.attachment;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;
import com.enonic.xp.portal.impl.handler.BaseHandlerTest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

import static org.junit.Assert.*;

public class AttachmentHandlerTest
    extends BaseHandlerTest
{
    private AttachmentHandler handler;

    private ContentService contentService;

    private ByteSource mediaBytes;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.handler = new AttachmentHandler();
        this.handler.setContentService( this.contentService );

        this.request.setMethod( "GET" );
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

        this.mediaBytes = ByteSource.wrap( new byte[0] );
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
        assertMethodNotAllowed( this.handler, HttpMethod.POST );
        assertMethodNotAllowed( this.handler, HttpMethod.DELETE );
        assertMethodNotAllowed( this.handler, HttpMethod.PUT );
        assertMethodNotAllowed( this.handler, HttpMethod.TRACE );
    }

    @Test
    public void testOptions()
        throws Exception
    {
        this.request.setMethod( "OPTIONS" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
        assertEquals( "GET,HEAD,OPTIONS", res.getHeaders().get( "Allow" ) );
    }

    @Test
    public void testNotValidUrlPattern()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
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

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
        assertEquals( "image/png", res.getContentType() );
        assertNull( res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testDownload()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/123456/logo.png" );

        final PortalResponse res = this.handler.handle( this.request );
        assertNotNull( res );
        assertEquals( 200, res.getStatus() );
        assertEquals( "image/png", res.getContentType() );
        assertEquals( "attachment; filename=logo.png", res.getHeaders().get( "Content-Disposition" ) );
        assertSame( this.mediaBytes, res.getBody() );
    }

    @Test
    public void testIdNotFound()
        throws Exception
    {
        this.request.setEndpointPath( "/_/attachment/download/1/logo.png" );

        try
        {
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
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
            this.handler.handle( this.request );
            fail( "Should throw exception" );
        }
        catch ( final PortalException e )
        {
            assertEquals( HttpStatus.NOT_FOUND, e.getStatus() );
            assertEquals( "Attachment [other.png] not found for [/path/to/content]", e.getMessage() );
        }
    }
}
