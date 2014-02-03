package com.enonic.wem.portal.underscore;


import java.awt.image.BufferedImage;
import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.ClientResponse;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.content.attachment.GetAttachment;
import com.enonic.wem.api.command.content.blob.GetBlob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.blobstore.memory.MemoryBlobRecord;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.AbstractResourceTest;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageResourceTest
    extends AbstractResourceTest
{
    private ImageResource resource;

    private Client client;

    private ImageFilterBuilder imageFilterBuilder;

    @Override
    protected Object getResourceInstance()
    {
        client = mock( Client.class );
        imageFilterBuilder = mock( ImageFilterBuilder.class );
        resource = new ImageResource();
        resource.client = client;
        resource.imageFilterBuilder = imageFilterBuilder;
        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void getImageFound()
        throws Exception
    {
        Content content = createContent( "content-id", "path/to/content", "image" );
        when( client.execute( isA( GetContentByPath.class ) ) ).thenReturn( content );
        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );
        final Attachment attachment = newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();
        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );
        when( client.execute( isA( GetAttachment.class ) ) ).thenReturn( attachment );
        final Blob blob = new MemoryBlobRecord( blobKey, imageData );
        when( client.execute( isA( GetBlob.class ) ) ).thenReturn( blob );

        resource.mode = "live";
        resource.contentPath = "path/to/content";
        resource.fileName = "enonic-logo.png";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "image/png", resp.getHeaders().getFirst( "content-type" ) );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        when( client.execute( isA( GetContentByPath.class ) ) ).thenReturn( null );

        resource.mode = "live";
        resource.contentPath = "path/to/content";
        resource.fileName = "enonic-logo.png";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    @Test
    @Ignore
    public void getImageWithFilter()
        throws Exception
    {
        Content content = createContent( "content-id", "path/to/content", "image" );
        when( client.execute( isA( GetContentByPath.class ) ) ).thenReturn( content );
        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );
        final Attachment attachment = newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();
        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );
        when( client.execute( isA( GetAttachment.class ) ) ).thenReturn( attachment );
        final Blob blob = new MemoryBlobRecord( blobKey, imageData );
        when( client.execute( isA( GetBlob.class ) ) ).thenReturn( blob );
        when( imageFilterBuilder.build( isA( BuilderContext.class ), isA( String.class ) ) ).thenReturn( getImageFilterBuilder() );

        resource.mode = "live";
        resource.contentPath = "path/to/content";
        resource.fileName = "enonic-logo.png";
        resource.filter = "sepia()";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "image/png", resp.getHeaders().getFirst( "content-type" ) );
    }

    private ImageFilter getImageFilterBuilder()
    {
        return new ImageFilter()
        {
            @Override
            public BufferedImage filter( final BufferedImage source )
            {
                return source;
            }
        };
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }
}
