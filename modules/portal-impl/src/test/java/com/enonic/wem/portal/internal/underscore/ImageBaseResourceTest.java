package com.enonic.wem.portal.internal.underscore;

import java.time.Instant;

import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Media;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.image.BuilderContext;
import com.enonic.wem.api.image.ImageFilter;
import com.enonic.wem.api.image.ImageFilterBuilder;
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
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build();

        final Content content = createContent( "content-id", "path/to/content", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( ByteSource.wrap( imageData ) );
        Mockito.when( this.imageFilterBuilder.build( Mockito.isA( BuilderContext.class ), Mockito.isA( String.class ) ) ).
            thenReturn( getImageFilterBuilder() );
    }

    private ImageFilter getImageFilterBuilder()
    {
        return source -> source;
    }

    private Content createContent( final String id, final String contentPath, final Attachment... attachments )
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
}
