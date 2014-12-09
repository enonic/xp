package com.enonic.wem.portal.internal.underscore;

import java.time.Instant;

import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.mock.memory.MemoryBlob;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.ImageFilter;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.internal.base.BaseResourceTest;

public abstract class ImageBaseResourceTest
    extends BaseResourceTest
{
    private ImageFilterBuilder imageFilterBuilder;

    private BlobService blobService;

    ContentService contentService;

    @Override
    protected void configure()
        throws Exception
    {
        this.imageFilterBuilder = Mockito.mock( ImageFilterBuilder.class );
        this.blobService = Mockito.mock( BlobService.class );
        this.contentService = Mockito.mock( ContentService.class );

        final ImageResourceProvider provider = new ImageResourceProvider();
        provider.setImageFilterBuilder( this.imageFilterBuilder );
        provider.setBlobService( this.blobService );
        provider.setContentService( this.contentService );

        this.servlet.addComponent( provider );
    }

    final void setupContent()
        throws Exception
    {
        final BlobKey blobKey = new BlobKey( "<blobkey-1>" );

        final Attachment attachment = Attachment.newAttachment().
            blobKey( blobKey ).
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();

        final ContentPath contentPath = ContentPath.from( "path/to/content" );
        final Content content = createContent( "content-id", contentPath, "mymodule:image", attachment );
        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).
            thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).
            thenReturn( content );

        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );

        final Blob blob = new MemoryBlob( blobKey, imageData );
        Mockito.when( this.blobService.get( Mockito.isA( BlobKey.class ) ) ).
            thenReturn( blob );
        Mockito.when( this.imageFilterBuilder.build( Mockito.isA( BuilderContext.class ), Mockito.isA( String.class ) ) ).
            thenReturn( getImageFilterBuilder() );
    }

    private ImageFilter getImageFilterBuilder()
    {
        return source -> source;
    }

    private Content createContent( final String id, final ContentPath contentPath, final String contentTypeName,
                                   final Attachment... attachments )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( contentPath ).
            createdTime( Instant.now() ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            attachments( Attachments.from( attachments ) ).
            build();
    }
}
