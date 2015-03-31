package com.enonic.xp.core.impl.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.Metadata;
import com.enonic.xp.content.Metadatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.attachment.CreateAttachment;
import com.enonic.xp.content.attachment.CreateAttachments;
import com.enonic.xp.content.attachment.image.ImageAttachmentScale;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.image.ImageHelper;
import com.enonic.xp.image.filter.ScaleWidthFilter;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.util.Exceptions;
import com.enonic.xp.util.GeoPoint;

public final class ImageContentProcessor
{
    private MixinService mixinService;

    private MediaInfo mediaInfo;

    private ContentType contentType;

    public ImageContentProcessor( final Builder builder )
    {
        this.mediaInfo = builder.mediaInfo;
        this.contentType = builder.contentType;
        this.mixinService = builder.mixinService;
    }

    public static Builder create()
    {
        return new Builder();
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

        final Mixins contentMixins = mixinService.getByContentType( contentType );

        Metadatas metadatas = null;
        if ( mediaInfo != null )
        {
            metadatas = extractMetadata( mediaInfo, contentMixins );
        }

        final CreateAttachments.Builder builder = CreateAttachments.builder();
        builder.add( sourceAttachment );
        builder.add( scaleImages( sourceImage, sourceAttachment ) );

        return CreateContentParams.create( params ).
            createAttachments( builder.build() ).metadata( metadatas ).
            build();
    }

    private CreateAttachments scaleImages( final BufferedImage sourceImage, final CreateAttachment sourceAttachment )
    {
        final CreateAttachments.Builder attachments = CreateAttachments.builder();
        for ( ImageAttachmentScale scale : ImageAttachmentScale.getScalesOrderedBySizeAsc() )
        {
            final boolean doScale = scale.getSize() < sourceImage.getWidth();
            if ( doScale )
            {
                final BufferedImage scaledImage = new ScaleWidthFilter( scale.getSize() ).filter( sourceImage );
                final String imageFormat = sourceAttachment.getExtension();
                final ByteSource scaledImageBytes = ImageHelper.toByteSource( scaledImage, imageFormat );
                final String name = sourceAttachment.getNameWithoutExtension() + "_" + scale.getLabel() + "." + sourceAttachment.getExtension();
                final CreateAttachment scaledImageAttachment = CreateAttachment.create().
                    mimeType( sourceAttachment.getMimeType() ).
                    name( name ).
                    label( scale.getLabel() ).
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

                Mixins contentMixins = mixinService.getByContentType( contentType );
                Metadatas metadatas = extractMetadata( mediaInfo, contentMixins );
                editable.metadata = metadatas;

            };
        }
        else
        {
            editor = null;
        }
        return new ProcessUpdateResult( processedCreateAttachments, editor );
    }

    private Metadatas extractMetadata( MediaInfo mediaInfo, Mixins mixins )
    {

        final Metadatas.Builder metadatasBuilder = Metadatas.builder();

        Map<MixinName, Metadata> metadataMap = new HashMap<>();

        for ( Map.Entry<String, Collection<String>> entry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            for ( Mixin mixin : mixins )
            {

                final String formItemName = TikaFieldNameFormatter.getConformityName( entry.getKey() );
                final FormItem formItem = mixin.getFormItems().getItemByName( formItemName );
                if ( formItem != null )
                {

                    Metadata metadata = metadataMap.get( mixin.getName() );

                    if ( metadata == null )
                    {
                        metadata = new Metadata( mixin.getName(), new PropertyTree() );
                        metadataMap.put( mixin.getName(), metadata );
                        metadatasBuilder.add( metadata );
                    }
                    if ( FormItemType.INPUT.equals( formItem.getType() ) )
                    {
                        Input input = (Input) formItem;
                        if ( InputTypes.DATE_TIME.equals( input.getInputType() ) )
                        {
                            metadata.getData().addLocalDateTime( formItemName,
                                                                 ValueTypes.LOCAL_DATE_TIME.convert( entry.getValue().toArray()[0] ) );
                        }
                        else
                        {
                            metadata.getData().addStrings( formItemName, entry.getValue() );
                        }
                    }
                }

            }
        }
        TikaFieldNameFormatter.fillComputedFormItems( metadataMap.values(), mediaInfo );

        return metadatasBuilder.build();
    }

    public static class Builder
    {

        private MediaInfo mediaInfo;

        private ContentType contentType;

        private MixinService mixinService;

        public Builder mediaInfo( final MediaInfo mediaInfo )
        {
            this.mediaInfo = mediaInfo;
            return this;
        }

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder mixinService( final MixinService mixinService )
        {
            this.mixinService = mixinService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.mixinService );
            Preconditions.checkNotNull( this.contentType );
        }

        public ImageContentProcessor build()
        {
            this.validate();
            return new ImageContentProcessor( this );
        }
    }

    private static class TikaFieldNameFormatter
    {
        private static final Map<String, String> fieldConformityMap = new HashMap<>();

        static
        {
            fieldConformityMap.put( "tiffImagelength", "imageHeight" );
            fieldConformityMap.put( "tiffImagewidth", "imageWidth" );
            fieldConformityMap.put( "exposureBiasValue", "exposureBias" );
            fieldConformityMap.put( "FNumber", "aperture" );
            fieldConformityMap.put( "exposureTime", "shutterTime" );
            fieldConformityMap.put( "subjectDistanceRange", "focusDistance" );
            fieldConformityMap.put( "gpsAltitude", "altitude" );
            fieldConformityMap.put( "gpsImgDirection", "direction" );
            fieldConformityMap.put( "whiteBalanceMode", "whiteBalance" );
            fieldConformityMap.put( "isoSpeedRatings", "iso" );
        }

        public static String getConformityName( String tikaFieldValue )
        {
            if ( fieldConformityMap.containsValue( tikaFieldValue ) )
            {
                return null;
            }
            return fieldConformityMap.containsKey( tikaFieldValue ) ? fieldConformityMap.get( tikaFieldValue ) : tikaFieldValue;
        }

        public static void fillComputedFormItems( Collection<Metadata> metadataList, MediaInfo mediaInfo )
        {
            for ( Metadata metadata : metadataList )
            {
                if ( "image-info".equals( metadata.getName().getLocalName() ) )
                {
                    final Collection<String> tiffImageLengths = mediaInfo.getMetadata().get( "tiffImagelength" );
                    final Collection<String> tiffImageWidths = mediaInfo.getMetadata().get( "tiffImagewidth" );
                    if ( tiffImageLengths.size() > 0 && tiffImageWidths.size() > 0 )
                    {
                        final Integer tiffImageLength = Integer.valueOf( tiffImageLengths.toArray()[0].toString() );
                        final Integer tiffImageWidth = Integer.valueOf( tiffImageWidths.toArray()[0].toString() );
                        metadata.getData().addLong( "pixelSize", (long) tiffImageLength * tiffImageWidth );
                    }
                }
                if ( "gps-info".equals( metadata.getName().getLocalName() ) )
                {
                    if ( mediaInfo.getMetadata().get( "geoLat" ).size() > 0 && mediaInfo.getMetadata().get( "geoLong" ).size() > 0 )
                    {
                        metadata.getData().addGeoPoint( "geoPoint", new GeoPoint(
                            Double.valueOf( mediaInfo.getMetadata().get( "geoLat" ).toArray()[0].toString() ),
                            Double.valueOf( mediaInfo.getMetadata().get( "geoLong" ).toArray()[0].toString() ) ) );
                    }
                }
            }
        }
    }
}
