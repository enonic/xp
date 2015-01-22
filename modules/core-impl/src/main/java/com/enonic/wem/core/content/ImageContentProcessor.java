package com.enonic.wem.core.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.ContentEditor;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.CreateAttachment;
import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.image.filter.ScaleWidthFilter;
import com.enonic.wem.api.media.MediaInfo;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.api.util.ImageHelper;

public final class ImageContentProcessor
{
    private static final String METADATA_PROPERTY_NAME = "metadata";

    private MediaInfo mediaInfo;

    private static final Scale[] scales =
        new Scale[]{new Scale( "small", 256 ), new Scale( "medium", 512 ), new Scale( "large", 1024 ), new Scale( "extra-large", 2048 )};

    public ImageContentProcessor( final MediaInfo mediaInfo )
    {
        this.mediaInfo = mediaInfo;
    }

    public CreateContentParams processCreate( final CreateContentParams params )
    {
        Preconditions.checkArgument( params.getType().isImageMedia(),
                                     "This processor only accepts [" + ContentTypeName.imageMedia() + "]: " + params.getType() );

        final CreateAttachments originalAttachments = params.getCreateAttachments();
        Preconditions.checkArgument( originalAttachments.getSize() == 1, "Expected only one attachment" );

        final CreateAttachment sourceAttachment = originalAttachments.first();

        final BufferedImage sourceImage;
        try (final InputStream inputStream = sourceAttachment.getByteSource().openStream())
        {
            sourceImage = ImageHelper.toBufferedImage( inputStream );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        final PropertySet rootSet = params.getData().getRoot();
        final PropertySet metadataSet = rootSet.addSet( METADATA_PROPERTY_NAME );
        if ( mediaInfo != null )
        {
            applyMetadata( metadataSet, mediaInfo );
        }

        final CreateAttachments.Builder builder = CreateAttachments.builder();
        builder.add( sourceAttachment );
        builder.add( scaleImages( sourceImage, sourceAttachment ) );

        params.createAttachments( builder.build() );
        return params;
    }

    private CreateAttachments scaleImages( final BufferedImage sourceImage, final CreateAttachment sourceAttachment )
    {
        final CreateAttachments.Builder attachments = CreateAttachments.builder();
        for ( final Scale scale : scales )
        {
            // scale only if the scale is less than the original
            if ( scale.size < sourceImage.getWidth() )
            {
                final BufferedImage scaledImage = new ScaleWidthFilter( scale.size ).filter( sourceImage );
                final String imageFormat = sourceAttachment.getExtension();
                final ByteSource scaledImageBytes = ImageHelper.toByteSource( scaledImage, imageFormat );
                final String name = sourceAttachment.getNameWithoutExtension() + "_" + scale.label + "." + sourceAttachment.getExtension();
                final CreateAttachment scaledImageAttachment = CreateAttachment.create().
                    mimeType( sourceAttachment.getMimeType() ).
                    name( name ).
                    label( scale.label ).
                    byteSource( scaledImageBytes ).
                    build();
                attachments.add( scaledImageAttachment ).build();
            }
        }
        return attachments.build();
    }

    public ProcessUpdateResult processUpdate( final UpdateContentParams params, final CreateAttachments createAttachments )
    {
        final CreateAttachments processedCreateAttachments;
        if ( createAttachments != null && createAttachments.getSize() == 1 )
        {
            final CreateAttachment sourceAttachment = createAttachments.first();
            final BufferedImage sourceImage;
            try (final InputStream inputStream = sourceAttachment.getByteSource().openStream())
            {
                sourceImage = ImageHelper.toBufferedImage( inputStream );
            }
            catch ( IOException e )
            {
                throw Exceptions.unchecked( e );
            }

            final CreateAttachments.Builder builder = CreateAttachments.builder();
            builder.add( sourceAttachment );
            builder.add( scaleImages( sourceImage, sourceAttachment ) );

            processedCreateAttachments = builder.build();
        }
        else
        {
            processedCreateAttachments = createAttachments;
        }

        final ContentEditor editor;
        if ( mediaInfo != null )
        {
            editor = editable -> {

                final PropertySet metadataSet;
                if ( editable.data.hasProperty( METADATA_PROPERTY_NAME ) )
                {
                    metadataSet = editable.data.getSet( METADATA_PROPERTY_NAME );
                }
                else
                {
                    metadataSet = editable.data.addSet( METADATA_PROPERTY_NAME );
                }
                applyMetadata( metadataSet, mediaInfo );
            };
        }
        else
        {
            editor = null;
        }
        return new ProcessUpdateResult( processedCreateAttachments, editor );
    }

    private void applyMetadata( final PropertySet parent, MediaInfo mediaInfo )
    {
        for ( Map.Entry<String, Collection<String>> entry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            for ( String value : entry.getValue() )
            {
                parent.addString( entry.getKey(), value );
            }
        }
    }

    private static class Scale
    {
        private final String label;

        private final int size;

        private Scale( final String label, final int size )
        {
            this.label = label;
            this.size = size;
        }
    }
}
