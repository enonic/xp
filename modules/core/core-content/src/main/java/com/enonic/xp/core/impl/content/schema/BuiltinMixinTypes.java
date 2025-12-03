package com.enonic.xp.core.impl.content.schema;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinName;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_GEO_POINT;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

final class BuiltinMixinTypes
{
    private static final String MIXIN_FOLDER = "mixins";

    private static final MixinDescriptor IMAGE_METADATA = MixinDescriptor.create()
        .name( IMAGE_INFO_METADATA_NAME )
        .displayName( "Image Info" )
        .displayNameI18nKey( "media.imageInfo.displayName" )
        .form( createImageInfoForm() )
        .build();

    private static final MixinDescriptor CAMERA_METADATA = MixinDescriptor.create()
        .name( CAMERA_INFO_METADATA_NAME )
        .displayName( "Photo Info" )
        .displayNameI18nKey( "media.cameraInfo.displayName" )
        .form( createPhotoInfoForm() )
        .build();

    private static final MixinDescriptor GPS_METADATA = MixinDescriptor.create()
        .name( GPS_INFO_METADATA_NAME )
        .displayName( "Gps Info" )
        .displayNameI18nKey( "base.gpsInfo.displayName" )
        .form( createGpsInfoForm() )
        .build();

    private final MixinDescriptors mixins;

    private final Map<MixinName, MixinDescriptor> map;

    BuiltinMixinTypes()
    {
        this.mixins = Stream.of( IMAGE_METADATA, CAMERA_METADATA, GPS_METADATA )
            .map( mixin -> MixinDescriptor.create( mixin ).icon( loadSchemaIcon( MIXIN_FOLDER, mixin.getName().getLocalName() ) ).build() )
            .collect( MixinDescriptors.collector() );
        this.map = this.mixins.stream().collect( Collectors.toUnmodifiableMap( MixinDescriptor::getName, Function.identity() ) );
    }

    private static Form createImageInfoForm()
    {
        final String i18n = "media.imageInfo";
        final Form.Builder form = Form.create();
        form.addFormItem( createLong( IMAGE_INFO_PIXEL_SIZE, "Size (px)", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( IMAGE_INFO_IMAGE_HEIGHT, "Height (px)", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( IMAGE_INFO_IMAGE_WIDTH, "Width (px)", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "contentType", "Content Type", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "description", "Description", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( MEDIA_INFO_BYTE_SIZE, "Size (bytes)", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "colorSpace", "Color Space", i18n ).occurrences( 0, 0 ).build() );
        form.addFormItem( createTextLine( "fileSource", "File Source", i18n ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Input.Builder createTextLine( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.TEXT_LINE ).label( label ).name( name ).labelI18nKey( i18n + "." + name + ".label" );
    }

    private static Input.Builder createLong( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.LONG ).label( label ).name( name ).labelI18nKey( i18n + "." + name + ".label" );
    }

    private static Input.Builder createDate( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.DATE_TIME ).label( label ).name( name ).labelI18nKey( i18n + "." + name + ".label" );
    }

    private static Input.Builder createGeoPoint( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.GEO_POINT ).label( label ).name( name ).labelI18nKey( i18n + "." + name + ".label" );
    }

    private static Form createGpsInfoForm()
    {
        final String i18n = "base.gpsInfo";
        final Form.Builder form = Form.create();
        form.addFormItem( createGeoPoint( GPS_INFO_GEO_POINT, "Geo Point", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "altitude", "Altitude", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "direction", "Direction", i18n ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Form createPhotoInfoForm()
    {
        final String i18n = "media.cameraInfo";
        final Form.Builder form = Form.create();
        form.addFormItem( createDate( "date", "Date", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "make", "Make", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "model", "Model", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "lens", "Lens", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "iso", "ISO", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focalLength", "Focal Length", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focalLength35", "Focal Length 35mm", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureBias", "Exposure Bias", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "aperture", "Aperture", i18n ).occurrences( 0, 0 ).build() );
        form.addFormItem( createTextLine( "shutterTime", "Shutter Time", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "flash", "Flash", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "autoFlashCompensation", "Auto Flash Compensation", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "whiteBalance", "White Balance", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureProgram", "Exposure Program", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "shootingMode", "Shooting Mode", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "meteringMode", "Metering Mode", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureMode", "Exposure Mode", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focusDistance", "Focus Distance", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "orientation", "Orientation", i18n ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    public MixinDescriptors getAll()
    {
        return this.mixins;
    }

    public MixinDescriptor getMixinDescriptor( final MixinName mixinName )
    {
        return this.map.get( mixinName );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        return SchemaHelper.loadIcon( getClass(), metaInfFolderName, name );
    }
}
