package com.enonic.wem.api.schema.content;

import java.util.LinkedHashMap;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;

public class ContentTypeForms
{
    public static final Form SITE = Form.newForm().
        addFormItem( Input.newInput().
            name( "description" ).
            label( "Description" ).
            inputType( InputTypes.TEXT_AREA ).
            occurrences( 0, 1 ).
            helpText( "Description of the site. Optional" ).
            build() ).
        addFormItem( Input.newInput().
            name( "moduleConfig" ).
            helpText( "Configure modules needed for the Site" ).
            inputType( InputTypes.MODULE_CONFIGURATOR ).
            required( false ).
            multiple( true ).
            build() ).
        build();

    public static final Form PAGE_TEMPLATE = Form.newForm().
        addFormItem( Input.newInput().
            name( "supports" ).
            label( "Supports" ).
            helpText( "Choose which content types this page template supports" ).
            inputType( InputTypes.CONTENT_TYPE_FILTER ).
            required( false ).
            multiple( true ).
            build() ).
        build();

    public static final Form MEDIA_IMAGE = Form.newForm().
        addFormItem( Input.newInput().name( "media" ).
            inputType( InputTypes.IMAGE_UPLOADER ).build() ).
        addFormItem( Input.newInput().name( "caption" ).
            inputType( InputTypes.TEXT_AREA ).
            label( "Caption" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.newInput().name( "artist" ).
            inputType( InputTypes.TAG ).
            label( "Artist" ).
            occurrences( 0, 0 ).
            build() ).
        addFormItem( Input.newInput().name( "copyright" ).
            inputType( InputTypes.TEXT_LINE ).
            label( "Copyright" ).
            occurrences( 0, 1 ).
            build() ).
        addFormItem( Input.newInput().name( "tags" ).
            inputType( InputTypes.TAG ).
            label( "Tags" ).
            occurrences( 0, 0 ).
            build() ).

        addFormItem( FormItemSet.newFormItemSet().label( "Metadata" ).name( "metadata" ).occurrences( 0, 1 ).
            addFormItem( createImmutableTextLine( "exifVersion" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "compressionType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sensorPixelSize" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "imageDescription" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "thumbnailCompression" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "numberOfComponents" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "cfaPattern" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "component2" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "focalLength" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "unknown20" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "component1" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffResolutionunit" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dateTimeOriginal" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffMake" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "relatedSoundFile" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "previewIfd" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "component3" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "FNumber" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "afFocusPosition" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffBitspersample" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lensType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "userComment" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "fileSource" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "metaCreationDate" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "autoFlashMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "creationDate" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "make" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "meteringMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "contrast" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "interoperabilityIndex" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffSoftware" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "thumbnailOffset" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "gainControl" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "flashUsed" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifImageHeight" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lens" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffYresolution" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "YResolution" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "shotInfo" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dcDescription" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "whiteBalance" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sensingMethod" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lastModified" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifExposuretime" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lensStops" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "thumbnailLength" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "programShift" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "noiseReduction" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sharpening" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exposureDifference" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "iso" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dateTime" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "whiteBalanceFine" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifImageWidth" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "imageHeight" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffModel" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "cameraHueAdjustment" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "model" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "autoFlashCompensation" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffImagewidth" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "subSecTime" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "whiteBalanceMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "date" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "componentsConfiguration" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "subSecTimeDigitized" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "ycbcrPositioning" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "XResolution" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "interoperabilityVersion" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "focalLength35" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "modified" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "qualityFileFormat" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "colorBalance" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exposureProgram" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "digitalZoomRatio" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifFnumber" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "afType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exposureTime" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "colourMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifFocallength" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lensData" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "software" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sceneType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lightSource" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "firmwareVersion" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "flashSyncMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "flashpixVersion" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dataPrecision" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "compressedBitsPerPixel" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "subSecTimeOriginal" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffImagelength" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "description" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dctermsCreated" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dctermsModified" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifFlash" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "lastSaveDate" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "colorSpace" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "metaSaveDate" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "toneCompensation" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "dateTimeDigitized" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "flash" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "shootingMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "contentType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "XParsedBy" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "tiffXresolution" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "saturation" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exifDatetimeoriginal" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sharpness" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "imageWidth" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "resolutionUnit" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exposureBiasValue" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "subjectDistanceRange" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "maxApertureValue" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "exposureMode" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "sceneCaptureType" ).occurrences( 0, 1 ).build() ).
            addFormItem( createImmutableTextLine( "customRendered" ).occurrences( 0, 1 ).build() ).
            build() ).
        build();

    public static final Form MEDIA_DEFAULT = Form.newForm().
        addFormItem( Input.newInput().name( "media" ).
            inputType( InputTypes.FILE_UPLOADER ).build() ).
        build();

    private final static LinkedHashMap<ContentTypeName, Form> formByName = new LinkedHashMap<>();

    static
    {
        formByName.put( ContentTypeName.pageTemplate(), PAGE_TEMPLATE );
    }

    public static Form get( final ContentTypeName name )
    {
        return formByName.get( name );
    }

    private static Input.Builder createImmutableTextLine( final String name )
    {
        return Input.newInput().inputType( InputTypes.TEXT_LINE ).label( name ).name( name ).immutable( true );
    }
}
