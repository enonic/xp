package com.enonic.xp.core.impl.content.processor;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.imageio.ImageIO;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Input;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.util.GeoPoint;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

@Component
public final class ImageContentProcessor
    implements ContentProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageContentProcessor.class );

    private static final List<String> IMAGE_LENGTH_VALUES = List.of( "tiffImagelength", "imageHeight" );

    private static final List<String> IMAGE_WIDTH_VALUES = List.of( "tiffImagewidth", "imageWidth" );

    private static final List<String> EXPOSURE_BIAS_VALUES = List.of( "exifSubifdExposureBiasValue", "exposureBiasValue", "exposureBias" );

    private static final List<String> APERTURE_VALUES = List.of( "exifSubifdApertureValue", "FNumber", "aperture" );

    private static final List<String> SHUTTER_TIME_VALUES = List.of( "exifSubifdExposureTime", "exposureTime", "shutterTime" );

    private static final List<String> FOCUS_DISTANCE_VALUES = List.of( "subjectDistanceRange", "focusDistance" );

    private static final List<String> ALTITUDE_VALUES = List.of( "gpsAltitude", "globalAltitude", "altitude" );

    private static final List<String> DIRECTION_VALUES = List.of( "gpsImgDirection", "direction" );

    private static final List<String> WHITE_BALANCE_VALUES = List.of( "exifSubifdWhiteBalanceMode", "whiteBalanceMode", "whiteBalance" );

    private static final List<String> ISO_VALUES = List.of( "isoSpeedRatings", "exifIsospeedratings", "iso" );

    private static final List<String> DESCRIPTION_VALUES = List.of( "dcDescription", "description" );

    private static final List<String> COLOR_SPACE_VALUES = List.of( "exifSubifdColorSpace", "iccColorSpace", "colorSpace" );

    private static final List<String> DATE_VALUES = List.of( "dctermsModified", "date" );

    private static final List<String> MAKE_VALUES = List.of( "tiffMake", "make" );

    private static final List<String> MODEL_VALUES = List.of( "tiffModel", "model" );

    private static final List<String> LENS_VALUES = List.of( "exifSubifdLensModel", "lens" );

    private static final List<String> FOCAL_LENGTH_VALUES = List.of( "exifSubifdFocalLength", "focalLength" );

    private static final List<String> EXPOSURE_PROGRAM_VALUES = List.of( "exifSubifdExposureProgram", "exposureProgram" );

    private static final List<String> METERING_MODE_VALUES = List.of( "exifSubifdMeteringMode", "meteringMode" );

    private static final List<String> EXPOSURE_MODE_VALUES = List.of( "exifSubifdExposureMode", "exposureMode" );

    private static final List<String> ORIENTATION_VALUES = List.of( "exifIfd0Orientation", "orientation" );

    private static final List<String> FLASH_VALUES = List.of( "exifSubifdFlash", "flash" );

    private static final Map<String, List<String>> METADATA_PRIORITY_MAP = ImmutableMap.<String, List<String>>builder()
        .put( "tiffImagelength", IMAGE_LENGTH_VALUES )
        .put( "imageHeight", IMAGE_LENGTH_VALUES )
        .put( "tiffImagewidth", IMAGE_WIDTH_VALUES )
        .put( "imageWidth", IMAGE_WIDTH_VALUES )
        .put( "exifSubifdExposureBiasValue", EXPOSURE_BIAS_VALUES )
        .put( "exposureBiasValue", EXPOSURE_BIAS_VALUES )
        .put( "exposureBias", EXPOSURE_BIAS_VALUES )
        .put( "exifSubifdApertureValue", APERTURE_VALUES )
        .put( "FNumber", APERTURE_VALUES )
        .put( "aperture", APERTURE_VALUES )
        .put( "exifSubifdExposureTime", SHUTTER_TIME_VALUES )
        .put( "exposureTime", SHUTTER_TIME_VALUES )
        .put( "shutterTime", SHUTTER_TIME_VALUES )
        .put( "subjectDistanceRange", FOCUS_DISTANCE_VALUES )
        .put( "focusDistance", FOCUS_DISTANCE_VALUES )
        .put( "gpsAltitude", ALTITUDE_VALUES )
        .put( "globalAltitude", ALTITUDE_VALUES )
        .put( "altitude", ALTITUDE_VALUES )
        .put( "gpsImgDirection", DIRECTION_VALUES )
        .put( "direction", DIRECTION_VALUES )
        .put( "exifSubifdWhiteBalanceMode", WHITE_BALANCE_VALUES )
        .put( "whiteBalanceMode", WHITE_BALANCE_VALUES )
        .put( "whiteBalance", WHITE_BALANCE_VALUES )
        .put( "isoSpeedRatings", ISO_VALUES )
        .put( "exifIsospeedratings", ISO_VALUES )
        .put( "iso", ISO_VALUES )
        .put( "dcDescription", DESCRIPTION_VALUES )
        .put( "description", DESCRIPTION_VALUES )
        .put( "exifSubifdColorSpace", COLOR_SPACE_VALUES )
        .put( "iccColorSpace", COLOR_SPACE_VALUES )
        .put( "colorSpace", COLOR_SPACE_VALUES )
        .put( "dctermsModified", DATE_VALUES )
        .put( "date", DATE_VALUES )
        .put( "tiffMake", MAKE_VALUES )
        .put( "make", MAKE_VALUES )
        .put( "tiffModel", MODEL_VALUES )
        .put( "model", MODEL_VALUES )
        .put( "exifSubifdLensModel", LENS_VALUES )
        .put( "lens", LENS_VALUES )
        .put( "exifSubifdFocalLength", FOCAL_LENGTH_VALUES )
        .put( "focalLength", FOCAL_LENGTH_VALUES )
        .put( "exifSubifdExposureProgram", EXPOSURE_PROGRAM_VALUES )
        .put( "exposureProgram", EXPOSURE_PROGRAM_VALUES )
        .put( "exifSubifdMeteringMode", METERING_MODE_VALUES )
        .put( "meteringMode", METERING_MODE_VALUES )
        .put( "exifSubifdExposureMode", EXPOSURE_MODE_VALUES )
        .put( "exposureMode", EXPOSURE_MODE_VALUES )
        .put( "exifIfd0Orientation", ORIENTATION_VALUES )
        .put( "orientation", ORIENTATION_VALUES )
        .put( "flash", FLASH_VALUES )
        .build();

    private static final ImmutableMap<String, String> FORM_CONFORMITY_MAP = ImmutableMap.<String, String>builder()
        .putAll( getFlattenedMap( IMAGE_LENGTH_VALUES, "imageHeight" ) )
        .putAll( getFlattenedMap( IMAGE_WIDTH_VALUES, "imageWidth" ) )
        .putAll( getFlattenedMap( EXPOSURE_BIAS_VALUES, "exposureBias" ) )
        .putAll( getFlattenedMap( APERTURE_VALUES, "aperture" ) )
        .putAll( getFlattenedMap( SHUTTER_TIME_VALUES, "shutterTime" ) )
        .putAll( getFlattenedMap( FOCUS_DISTANCE_VALUES, "focusDistance" ) )
        .putAll( getFlattenedMap( ALTITUDE_VALUES, "altitude" ) )
        .putAll( getFlattenedMap( DIRECTION_VALUES, "direction" ) )
        .putAll( getFlattenedMap( WHITE_BALANCE_VALUES, "whiteBalance" ) )
        .putAll( getFlattenedMap( ISO_VALUES, "iso" ) )
        .putAll( getFlattenedMap( DESCRIPTION_VALUES, "description" ) )
        .putAll( getFlattenedMap( COLOR_SPACE_VALUES, "colorSpace" ) )
        .putAll( getFlattenedMap( DATE_VALUES, "date" ) )
        .putAll( getFlattenedMap( MAKE_VALUES, "make" ) )
        .putAll( getFlattenedMap( MODEL_VALUES, "model" ) )
        .putAll( getFlattenedMap( LENS_VALUES, "lens" ) )
        .putAll( getFlattenedMap( FOCAL_LENGTH_VALUES, "focalLength" ) )
        .putAll( getFlattenedMap( EXPOSURE_PROGRAM_VALUES, "exposureProgram" ) )
        .putAll( getFlattenedMap( METERING_MODE_VALUES, "meteringMode" ) )
        .putAll( getFlattenedMap( EXPOSURE_MODE_VALUES, "exposureMode" ) )
        .putAll( getFlattenedMap( ORIENTATION_VALUES, "orientation" ) )
        .putAll( getFlattenedMap( FLASH_VALUES, "flash" ) )
        .build();

    private static final String GEO_LONGITUDE = "geoLong";

    private static final String GEO_LATITUDE = "geoLat";

    private final ContentService contentService;

    private final XDatas xDatas;

    @Activate
    public ImageContentProcessor( @Reference final ContentService contentService, @Reference final  XDataService xDataService )
    {
        this.contentService = contentService;
        this.xDatas = xDataService.getByNames( XDataNames.from( IMAGE_INFO_METADATA_NAME, CAMERA_INFO_METADATA_NAME, GPS_INFO_METADATA_NAME ) );
    }

    @Override
    public boolean supports( final ContentTypeName contentType )
    {
        return contentType.isImageMedia();
    }

    // Helper function to create a map where each key in the list points to the same value
    private static ImmutableMap<String, String> getFlattenedMap( List<String> keys, String value )
    {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for ( String key : keys )
        {
            builder.put( key, value );
        }
        return builder.build();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();
        final MediaInfo mediaInfo = params.getMediaInfo();

        final CreateAttachments originalAttachments = createContentParams.getCreateAttachments();
        Preconditions.checkArgument( originalAttachments.getSize() == 1, "Expected only one attachment" );

        ExtraDatas extraDatas = null;

        if ( mediaInfo != null )
        {
            extraDatas = extractMetadata( mediaInfo );
        }

        return new ProcessCreateResult( CreateContentParams.create( createContentParams )
                                            .createAttachments( originalAttachments )
                                            .extraDatas( extraDatas )
                                            .build(), params.getProcessedReferences() );
    }


    private ExtraDatas updateImageMetadata( final EditableContent editable )
    {
        final Media media = (Media) editable.source;
        final Attachment mediaAttachment = media.getMediaAttachment();
        if ( mediaAttachment == null )
        {
            return editable.extraDatas;
        }

        final ByteSource binary = contentService.getBinary( editable.source.getId(), mediaAttachment.getBinaryReference() );
        if ( binary == null )
        {
            return editable.extraDatas;
        }

        final BufferedImage image = toBufferedImage( binary );
        if ( image == null )
        {
            return editable.extraDatas;
        }

        final Cropping cropping = media.getCropping();
        final long byteSize = mediaAttachment.getSize();

        final long imageWidth;
        final long imageHeight;
        final long imageSize;
        if ( cropping == null || cropping.isUnmodified() )
        {
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            imageSize = imageWidth * imageHeight;
        }
        else
        {
            final BufferedImage croppedImage = cropImage( image, cropping );
            imageWidth = croppedImage.getWidth();
            imageHeight = croppedImage.getHeight();
            imageSize = imageWidth * imageHeight;
        }

        ExtraData extraData = editable.extraDatas.getMetadata( IMAGE_INFO_METADATA_NAME );
        if ( extraData != null )
        {
            final PropertyTree xData = extraData.getData();
            setLongProperty( xData, IMAGE_INFO_PIXEL_SIZE, imageSize );
            setLongProperty( xData, IMAGE_INFO_IMAGE_HEIGHT, imageHeight );
            setLongProperty( xData, IMAGE_INFO_IMAGE_WIDTH, imageWidth );
            setLongProperty( xData, MEDIA_INFO_BYTE_SIZE, byteSize );
        }

        return editable.extraDatas;
    }

    private void setLongProperty( final PropertyTree propertyTree, final String path, final Long value )
    {
        propertyTree.removeProperties( path );
        propertyTree.setLong( path, value );
    }

    private BufferedImage toBufferedImage( final ByteSource source )
    {
        try (InputStream stream = source.openStream())
        {
            return ImageIO.read( stream );
        }
        catch ( IOException e )
        {
            LOG.warn( "Failed to read BufferedImage from InputStream", e );
            return null;
        }
    }

    private BufferedImage cropImage( final BufferedImage image, final Cropping cropping )
    {
        final double width = image.getWidth();
        final double height = image.getHeight();
        return image.getSubimage( (int) ( width * cropping.left() ), (int) ( height * cropping.top() ), (int) ( width * cropping.width() ),
                                  (int) ( height * cropping.height() ) );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final MediaInfo mediaInfo = params.getMediaInfo();

        final ContentEditor editor;
        if ( mediaInfo != null )
        {
            editor = editable -> {
                final ExtraDatas.Builder builder = ExtraDatas.create();
                builder.addAll( editable.extraDatas );

                extractMetadata( mediaInfo ).forEach( builder::add );

                editable.extraDatas = builder.buildKeepingLast();
            };
        }
        else
        {
            editor = editable -> editable.extraDatas = updateImageMetadata( editable );
        }
        return new ProcessUpdateResult( editor, params.getEditedContent().getProcessedReferences() );
    }

    private ExtraData extractGeoLocation( final MediaInfo mediaInfo )
    {
        final Multimap<String, String> mediaItems = mediaInfo.getMetadata();
        final Double geoLat = parseDouble( mediaItems.get( GEO_LATITUDE ).stream().findFirst().orElse( null ) );
        final Double geoLong = parseDouble( mediaItems.get( GEO_LONGITUDE ).stream().findFirst().orElse( null ) );
        if ( geoLat == null || geoLong == null )
        {
            return null;
        }
        final XData geoMixin = xDatas.getXData( GPS_INFO_METADATA_NAME );
        final ExtraData extraData = new ExtraData( GPS_INFO_METADATA_NAME, new PropertyTree() );
        final FormItem formItem = geoMixin.getForm().getFormItem( FormItemPath.from( MediaInfo.GPS_INFO_GEO_POINT ) );
        if ( FormItemType.INPUT.equals( formItem.getType() ) )
        {
            final Input input = (Input) formItem;
            if ( InputTypeName.GEO_POINT.equals( input.getInputType() ) )
            {
                final GeoPoint geoPoint = new GeoPoint( geoLat, geoLong );
                extraData.getData().addGeoPoint( formItem.getName(), ValueTypes.GEO_POINT.convert( geoPoint ) );
            }
        }
        return extraData;
    }

    private static Double parseDouble( final String str )
    {
        if ( str == null )
        {
            return null;
        }
        try
        {
            return Double.parseDouble( str );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    private ExtraDatas extractMetadata( final MediaInfo mediaInfo )
    {
        final Map<XDataName, ExtraData> metadataMap = new LinkedHashMap<>();

        final ExtraData geoData = extractGeoLocation( mediaInfo );
        if ( geoData != null )
        {
            metadataMap.put( GPS_INFO_METADATA_NAME, geoData );
        }

        final Set<String> visitedFormItems = new HashSet<>();

        for ( Map.Entry<String, Collection<String>> mediaInfoEntry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            String formItemName;
            Collection<String> mediaEntryValues;

            final List<String> priorityList = METADATA_PRIORITY_MAP.get( mediaInfoEntry.getKey() );

            if ( priorityList != null )
            {
                formItemName = FORM_CONFORMITY_MAP.get( mediaInfoEntry.getKey() );

                if ( visitedFormItems.contains( formItemName ) )
                {
                    continue;
                }

                mediaEntryValues =
                    priorityList.stream().map( mediaInfo.getMetadata().asMap()::get ).filter( Objects::nonNull ).findFirst().orElseThrow();

                visitedFormItems.add( formItemName );

            }
            else
            {
                formItemName = mediaInfoEntry.getKey();
                mediaEntryValues = mediaInfoEntry.getValue();
            }

            for ( XData xData : xDatas )
            {
                final FormItem formItem = xData.getForm().getFormItem( FormItemPath.from( formItemName ) );
                if ( formItem == null )
                {
                    continue;
                }
                ExtraData extraData = getOrCreate( metadataMap, xData.getName() );
                if ( extraData == null )
                {
                    extraData = new ExtraData( xData.getName(), new PropertyTree() );
                    metadataMap.put( xData.getName(), extraData );
                }
                if ( FormItemType.INPUT.equals( formItem.getType() ) )
                {
                    Input input = (Input) formItem;
                    if ( InputTypeName.DATE_TIME.equals( input.getInputType() ) )
                    {
                        extraData.getData()
                            .addLocalDateTime( formItemName, ValueTypes.LOCAL_DATE_TIME.convert( mediaEntryValues.toArray()[0] ) );
                    }
                    else if ( InputTypeName.LONG.equals( input.getInputType() ) )
                    {
                        final Long[] longValues = mediaEntryValues.stream().map( Long::parseLong ).toArray( Long[]::new );
                        extraData.getData().addLongs( formItemName, longValues );
                    }
                    else
                    {
                        extraData.getData().addStrings( formItemName, mediaEntryValues );
                    }
                }
            }
        }

        final ExtraData imageInfoExtraData = getOrCreate( metadataMap, IMAGE_INFO_METADATA_NAME );
        final PropertyTree imageInfoExtraDataData = imageInfoExtraData.getData();
        final Long imageHeight = imageInfoExtraDataData.getLong( IMAGE_INFO_IMAGE_HEIGHT );
        final Long imageWidth = imageInfoExtraDataData.getLong( IMAGE_INFO_IMAGE_WIDTH );
        if ( imageHeight != null && imageWidth != null )
        {
            imageInfoExtraDataData.setLong( IMAGE_INFO_PIXEL_SIZE, imageHeight * imageWidth );
        }
        final Collection<String> imageSize = mediaInfo.getMetadata().get( "bytesize" );
        if ( !imageSize.isEmpty() )
        {
            imageInfoExtraDataData.setLong( MEDIA_INFO_BYTE_SIZE, Long.parseLong( imageSize.stream().findFirst().orElseThrow() ) );
        }

        return metadataMap.values().stream().collect( ExtraDatas.collector() );
    }

    private static ExtraData getOrCreate(Map<XDataName, ExtraData> metadataMap, XDataName name) {
        return metadataMap.computeIfAbsent( name, n -> new ExtraData( n, new PropertyTree() ) );
    }
}
