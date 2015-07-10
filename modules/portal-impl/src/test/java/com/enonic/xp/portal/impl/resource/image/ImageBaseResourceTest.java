package com.enonic.xp.portal.impl.resource.image;

import java.time.Instant;

import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageFilterBuilder;
import com.enonic.xp.image.ImageScaleFunction;
import com.enonic.xp.image.ImageScaleFunctionBuilder;
import com.enonic.xp.image.scale.ScaleParams;
import com.enonic.xp.portal.impl.resource.base.BaseResourceTest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReference;

public abstract class ImageBaseResourceTest
    extends BaseResourceTest
{
    private ImageFilterBuilder imageFilterBuilder;

    private ImageScaleFunctionBuilder imageScaleFunctionBuilder;

    private TemporaryFolder temporaryFolder;

    protected ContentService contentService;

    @Override
    protected void configure()
        throws Exception
    {
        this.imageFilterBuilder = Mockito.mock( ImageFilterBuilder.class );
        this.imageScaleFunctionBuilder = Mockito.mock( ImageScaleFunctionBuilder.class );
        this.temporaryFolder = new TemporaryFolder();
        this.temporaryFolder.create();
        System.setProperty( "xp.home", this.temporaryFolder.getRoot().getPath() );
        this.services.setImageFilterBuilder( this.imageFilterBuilder );
        this.services.setImageScaleFunctionBuilder( this.imageScaleFunctionBuilder );

        this.contentService = Mockito.mock( ContentService.class );
        this.services.setContentService( this.contentService );
    }

    final void setupContent()
        throws Exception
    {
        final Attachment attachment = Attachment.newAttachment().
            name( "enonic-logo.png" ).
            mimeType( "image/png" ).
            label( "source" ).
            build();

        final Content content = createContent( "content-id", "path/to/image-name.jpg", attachment );

        Mockito.when( this.contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( Mockito.eq( content.getPath() ) ) ).thenReturn( content );

        final byte[] imageData = ByteStreams.toByteArray( getClass().getResourceAsStream( "enonic-logo.png" ) );

        Mockito.when( this.contentService.getBinary( Mockito.isA( ContentId.class ), Mockito.isA( BinaryReference.class ) ) ).
            thenReturn( ByteSource.wrap( imageData ) );
        Mockito.when( this.imageFilterBuilder.build( Mockito.isA( String.class ) ) ).
            thenReturn( getImageFilterBuilder() );
        Mockito.when( this.imageScaleFunctionBuilder.build( Mockito.isA( ScaleParams.class ), Mockito.isA( FocalPoint.class ) ) ).
            thenReturn( getImageScaleFunctionBuilder() );
    }

    private ImageFilter getImageFilterBuilder()
    {
        return source -> source;
    }

    private ImageScaleFunction getImageScaleFunctionBuilder()
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
