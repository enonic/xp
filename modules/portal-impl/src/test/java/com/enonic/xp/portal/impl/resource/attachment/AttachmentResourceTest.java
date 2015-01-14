package com.enonic.xp.portal.impl.resource.attachment;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.xp.portal.impl.resource.base.BaseResourceTest;

import static org.junit.Assert.*;

public class AttachmentResourceTest
    extends BaseResourceTest
{
    private ContentService contentService;

    @Override
    protected void configure()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.services.setContentService( this.contentService );
    }

    private void setupMedia()
        throws Exception
    {
        final Attachment attachment = Attachment.newAttachment().
            name( "source" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();

        final Media content = createMedia( "content-id", "path/to/content", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( ByteSource.wrap( imageData ) );
    }

    private Media createMedia( final String id, final String contentPath, final Attachment... attachments )
    {
        final PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
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
    public void getById()
        throws Exception
    {
        setupMedia();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/attachment/id/content-id" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    public void getById_withLabel()
        throws Exception
    {
        setupMedia();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/attachment/id/content-id/source" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    public void getById_notFound()
        throws Exception
    {
        Mockito.when( this.contentService.getById( Mockito.anyObject() ) ).thenReturn( null );

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/attachment/id/content-id" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
    }

    @Test
    public void getSource()
        throws Exception
    {
        setupMedia();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/attachment" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }

    @Test
    public void getByLabel()
        throws Exception
    {
        setupMedia();

        final MockHttpServletRequest request = newGetRequest( "/prod/path/to/content/_/attachment/source" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );
        assertEquals( "image/png", response.getContentType() );
    }
}
