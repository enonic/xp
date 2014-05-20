package com.enonic.wem.portal.underscore;


import java.awt.image.BufferedImage;
import java.io.IOException;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.DefaultResourceConfig;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
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

    private ImageFilterBuilder imageFilterBuilder;

    private AttachmentService attachmentService;

    private BlobService blobService;

    private ContentService contentService;

    @Override
    protected void configure( final DefaultResourceConfig config )
    {
        resource = new ImageResource();

        imageFilterBuilder = mock( ImageFilterBuilder.class );
        resource.imageFilterBuilder = imageFilterBuilder;

        attachmentService = mock( AttachmentService.class );
        resource.attachmentService = attachmentService;

        blobService = mock( BlobService.class );
        resource.blobService = blobService;

        contentService = mock( ContentService.class );
        resource.contentService = contentService;

        config.getSingletons().add( resource );
    }

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    @Ignore
    public void getImageFound()
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "image" );
        when( contentService.getByPath( contentPath ) ).thenReturn( content );

        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );
        final Attachment attachment = newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();
        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );
        when( attachmentService.get( isA( ContentId.class ), isA( String.class ) ) ).thenReturn( attachment );
        final Blob blob = new MemoryBlobRecord( blobKey, imageData );
        when( blobService.get( isA( BlobKey.class ) ) ).thenReturn( blob );

        // resource.mode = "live";
        // resource.contentPath = "path/to/content";
        // resource.fileName = "enonic-logo.png";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 200, resp.getStatus() );
        assertEquals( "image/png", resp.getHeaders().getFirst( "content-type" ) );
    }

    @Test
    public void getImageNotFound()
        throws Exception
    {
        when( contentService.getByPath( ContentPath.from( "path/to/content" ) ) ).thenReturn( null );

        // resource.mode = "live";
        // resource.contentPath = "path/to/content";
        // resource.fileName = "enonic-logo.png";
        final ClientResponse resp = resource().path( "/portal/live/path/to/content/_/image/enonic-logo.png" ).get( ClientResponse.class );

        assertEquals( 404, resp.getStatus() );
    }

    @Test
    @Ignore
    public void getImageWithFilter()
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "image" );
        when( contentService.getByPath( contentPath ) ).thenReturn( content );

        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );
        final Attachment attachment = newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();
        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );
        when( attachmentService.get( isA( ContentId.class ), isA( String.class ) ) ).thenReturn( attachment );
        final Blob blob = new MemoryBlobRecord( blobKey, imageData );
        when( blobService.get( isA( BlobKey.class ) ) ).thenReturn( blob );
        when( imageFilterBuilder.build( isA( BuilderContext.class ), isA( String.class ) ) ).thenReturn( getImageFilterBuilder() );

        // resource.mode = "live";
        // resource.contentPath = "path/to/content";
        // resource.fileName = "enonic-logo.png";
        // resource.filter = "sepia()";
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

    private Content createContent( final String id, final ContentPath contentPath, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( contentPath ).
            createdTime( Instant.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }
}
