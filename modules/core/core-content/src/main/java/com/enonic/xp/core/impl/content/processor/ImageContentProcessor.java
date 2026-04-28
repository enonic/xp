package com.enonic.xp.core.impl.content.processor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.TikaCoreProperties;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.MediaUtils;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.core.impl.content.schema.BuiltinMixinTypes;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.util.GeoPoint;

@Component
public final class ImageContentProcessor
    implements ContentProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageContentProcessor.class );

    private static final List<FormItemSource> FORM_ITEM_SOURCES =
        List.of( source( BuiltinMixinTypes.IMAGE_METADATA, MediaInfo.IMAGE_INFO_IMAGE_HEIGHT, longValue( TIFF.IMAGE_LENGTH.getName() ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, MediaInfo.IMAGE_INFO_IMAGE_WIDTH, longValue( TIFF.IMAGE_WIDTH.getName() ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, MediaInfo.IMAGE_INFO_ORIENTATION, longValue( TIFF.ORIENTATION.getName() ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, "contentType", string( HttpHeaders.CONTENT_TYPE ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, "description", string( TikaCoreProperties.DESCRIPTION.getName() ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, "colorSpace", string( "Exif SubIFD:Color Space" ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, "fileSource", string( "Exif SubIFD:File Source" ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, MediaInfo.MEDIA_INFO_BYTE_SIZE, longValue( MediaInfo.MEDIA_INFO_BYTE_SIZE ) ),
                 source( BuiltinMixinTypes.IMAGE_METADATA, MediaInfo.IMAGE_INFO_PIXEL_SIZE,
                         longProduct( TIFF.IMAGE_LENGTH.getName(), TIFF.IMAGE_WIDTH.getName() ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "date", dateTime( TikaCoreProperties.CREATED.getName() ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "make", string( TIFF.EQUIPMENT_MAKE.getName() ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "model", string( TIFF.EQUIPMENT_MODEL.getName() ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "lens", string( "Exif SubIFD:Lens Model", "aux:Lens" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "iso",
                         string( TIFF.ISO_SPEED_RATINGS.getName(), "Exif SubIFD:ISO Speed Ratings" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "focalLength",
                         string( TIFF.FOCAL_LENGTH.getName(), "Exif SubIFD:Focal Length" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "focalLength35", string( "Exif SubIFD:Focal Length 35" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "exposureBias", string( "Exif SubIFD:Exposure Bias Value" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "aperture", string( TIFF.F_NUMBER.getName(), "Exif SubIFD:F-Number" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "shutterTime",
                         string( TIFF.EXPOSURE_TIME.getName(), "Exif SubIFD:Exposure Time" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "flash", string( TIFF.FLASH_FIRED.getName(), "Exif SubIFD:Flash" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "autoFlashCompensation", string( "aux:FlashCompensation", "Flash Bias" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "whiteBalance", string( "Exif SubIFD:White Balance Mode" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "exposureProgram", string( "Exif SubIFD:Exposure Program" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "shootingMode", string( "Shooting Mode", "Record Mode" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "meteringMode", string( "Exif SubIFD:Metering Mode" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "exposureMode", string( "Exif SubIFD:Exposure Mode" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "focusDistance", string( "Exif SubIFD:Subject Distance Range" ) ),
                 source( BuiltinMixinTypes.CAMERA_METADATA, "orientation", string( "Exif IFD0:Orientation" ) ),
                 source( BuiltinMixinTypes.GPS_METADATA, "altitude", string( Geographic.ALTITUDE.getName(), "GPS:GPS Altitude" ) ),
                 source( BuiltinMixinTypes.GPS_METADATA, "direction", string( "GPS:GPS Img Direction" ) ),
                 source( BuiltinMixinTypes.GPS_METADATA, MediaInfo.GPS_INFO_GEO_POINT,
                         geoPoint( Geographic.LATITUDE.getName(), Geographic.LONGITUDE.getName() ) ) );

    @Override
    public boolean supports( final ContentTypeName contentType )
    {
        return contentType.isImageMedia();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        if ( params.getMediaInfo() == null )
        {
            return new ProcessCreateResult( params.getCreateContentParams(), params.getProcessedReferences() );
        }
        final Mixins mixins = extractMetadata( params.getMediaInfo() );
        final CreateContentParams.Builder newParams = CreateContentParams.create( params.getCreateContentParams() ).mixins( mixins );
        writeEffectiveDimension( params.getCreateContentParams().getData().getSet( ContentPropertyNames.MEDIA ),
                                 mixins.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME ), true );
        return new ProcessCreateResult( newParams.build(), params.getProcessedReferences() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final Content content = params.getContent();
        final Content.Builder<?> builder = Content.create( content );

        Mixins mixins = content.getMixins();
        final boolean binaryChanged = params.getMediaInfo() != null;
        if ( binaryChanged )
        {
            mixins = Mixins.create().addAll( mixins.copy() ).addAll( extractMetadata( params.getMediaInfo() ) ).buildKeepingLast();
            builder.mixins( mixins );
        }
        final Content newContent = builder.build();
        writeEffectiveDimension( newContent.getData().getSet( ContentPropertyNames.MEDIA ),
                                 mixins.getByName( MediaInfo.IMAGE_INFO_METADATA_NAME ), binaryChanged );
        return new ProcessUpdateResult( newContent );
    }

    private static void writeEffectiveDimension( final PropertySet mediaData, final Mixin imageInfo, final boolean refreshOrientation )
    {
        if ( mediaData == null || imageInfo == null )
        {
            return;
        }

        if ( refreshOrientation )
        {
            final Long origOrientation = imageInfo.getData().getLong( MediaInfo.IMAGE_INFO_ORIENTATION );
            if ( origOrientation != null )
            {
                mediaData.removeProperties( ContentPropertyNames.ORIENTATION );
                mediaData.setLong( ContentPropertyNames.ORIENTATION, origOrientation );
            }
        }

        final Long origWidth = imageInfo.getData().getLong( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
        final Long origHeight = imageInfo.getData().getLong( MediaInfo.IMAGE_INFO_IMAGE_HEIGHT );
        if ( origWidth == null || origHeight == null )
        {
            return;
        }

        final ImageOrientation orientation = MediaUtils.readOrientation( mediaData );
        final Cropping cropping = MediaUtils.readCropping( mediaData );

        final boolean swap = switch ( orientation )
        {
            case LeftTop, RightTop, RightBottom, LeftBottom -> true;
            case null, default -> false;
        };
        long width = swap ? origHeight : origWidth;
        long height = swap ? origWidth : origHeight;
        if ( cropping != null && !cropping.isUnmodified() )
        {
            width = (long) ( width * ( cropping.right() - cropping.left() ) );
            height = (long) ( height * ( cropping.bottom() - cropping.top() ) );
        }

        mediaData.removeProperties( ContentPropertyNames.MEDIA_IMAGE_HEIGHT );
        mediaData.removeProperties( ContentPropertyNames.MEDIA_IMAGE_WIDTH );
        mediaData.setLong( ContentPropertyNames.MEDIA_IMAGE_HEIGHT, height );
        mediaData.setLong( ContentPropertyNames.MEDIA_IMAGE_WIDTH, width );
    }

    private static Mixins extractMetadata( final MediaInfo mediaInfo )
    {
        final Map<MixinName, Mixin> metadataMap = new LinkedHashMap<>();

        for ( FormItemSource source : FORM_ITEM_SOURCES )
        {
            source.strategy().resolve( mediaInfo ).ifPresent( value -> {
                final PropertyTree data = getOrCreate( metadataMap, source.mixin() ).getData();
                data.removeProperties( source.formItemName() );
                data.setProperty( source.formItemName(), value );
            } );
        }

        return metadataMap.values().stream().collect( Mixins.collector() );
    }

    private static Mixin getOrCreate( Map<MixinName, Mixin> metadataMap, MixinName name )
    {
        return metadataMap.computeIfAbsent( name, n -> new Mixin( n, new PropertyTree() ) );
    }

    private record FormItemSource(MixinName mixin, String formItemName, Strategy strategy)
    {
    }

    @FunctionalInterface
    private interface Strategy
    {
        Optional<Value> resolve( MediaInfo mediaInfo );
    }

    private static FormItemSource source( final MixinDescriptor mixin, final String formItemName, final Strategy strategy )
    {
        return new FormItemSource( mixin.getName(), formItemName, strategy );
    }

    private static Optional<String> firstMatch( final MediaInfo mediaInfo, final String... priorityKeys )
    {
        return Stream.of( priorityKeys )
            .map( mediaInfo.getMetadata().asMap()::get )
            .filter( Objects::nonNull )
            .findFirst()
            .map( values -> values.iterator().next() );
    }

    private static Strategy string( final String... priorityKeys )
    {
        return mediaInfo -> firstMatch( mediaInfo, priorityKeys ).map( ValueFactory::newString );
    }

    private static Strategy longValue( final String... priorityKeys )
    {
        return mediaInfo -> firstMatch( mediaInfo, priorityKeys ).flatMap( value -> {
            try
            {
                return Optional.of( ValueFactory.newLong( ValueTypes.LONG.convert( value ) ) );
            }
            catch ( ValueTypeException e )
            {
                LOG.debug( "Failed to parse '{}' as {}", value, ValueTypes.LONG.getJavaType(), e );
                return Optional.empty();
            }
        } );
    }

    private static Strategy dateTime( final String... priorityKeys )
    {
        return mediaInfo -> firstMatch( mediaInfo, priorityKeys ).flatMap( value -> {
            try
            {
                return Optional.of( ValueFactory.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) ) );
            }
            catch ( ValueTypeException e )
            {
                LOG.debug( "Failed to parse '{}' as {}", value, ValueTypes.LOCAL_DATE_TIME.getJavaType(), e );
                return Optional.empty();
            }
        } );
    }

    private static Strategy geoPoint( final String latKey, final String lonKey )
    {
        return mediaInfo -> firstMatch( mediaInfo, latKey ).flatMap( latStr -> firstMatch( mediaInfo, lonKey ).flatMap( lonStr -> {
            try
            {
                return Optional.of(
                    ValueFactory.newGeoPoint( new GeoPoint( ValueTypes.DOUBLE.convert( latStr ), ValueTypes.DOUBLE.convert( lonStr ) ) ) );
            }
            catch ( ValueTypeException e )
            {
                LOG.debug( "Failed to build GeoPoint from '{}' / '{}'", latStr, lonStr, e );
                return Optional.empty();
            }
        } ) );
    }

    private static Strategy longProduct( final String firstKey, final String secondKey )
    {
        return mediaInfo -> firstMatch( mediaInfo, firstKey ).flatMap(
            firstStr -> firstMatch( mediaInfo, secondKey ).flatMap( secondStr -> {
                try
                {
                    return Optional.of(
                        ValueFactory.newLong( ValueTypes.LONG.convert( firstStr ) * ValueTypes.LONG.convert( secondStr ) ) );
                }
                catch ( ValueTypeException e )
                {
                    LOG.debug( "Failed to compute long product from '{}' * '{}'", firstStr, secondStr, e );
                    return Optional.empty();
                }
            } ) );
    }
}
