package com.enonic.xp.core.impl.schema.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.Mixins;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_GEO_POINT;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

final class BuiltinMixinsTypes
{
    private static final String MIXINS_FOLDER = "mixins";

    private static final Mixin IMAGE_METADATA = Mixin.create().
        name( IMAGE_INFO_METADATA_NAME ).
        displayName( "Image Info" ).
        displayNameI18nKey( "media.imageInfo.displayName" ).
        form( createImageInfoMixinForm() ).
        build();

    private static final Mixin CAMERA_METADATA = Mixin.create().
        name( CAMERA_INFO_METADATA_NAME ).
        displayName( "Photo Info" ).
        displayNameI18nKey( "media.cameraInfo.displayName" ).
        form( createPhotoInfoMixinForm() ).
        build();

    private static final Mixin GPS_METADATA = Mixin.create().
        name( GPS_INFO_METADATA_NAME ).
        displayName( "Gps Info" ).
        displayNameI18nKey( "base.gpsInfo.displayName" ).
        form( createGpsInfoMixinForm() ).
        build();

    private static final Mixins MIXINS = Mixins.from( IMAGE_METADATA, CAMERA_METADATA, GPS_METADATA );

    private static Form createImageInfoMixinForm()
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

    private static Form createGpsInfoMixinForm()
    {
        final String i18n = "base.gpsInfo";
        final Form.Builder form = Form.create();
        form.addFormItem( createGeoPoint( GPS_INFO_GEO_POINT, "Geo Point", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "altitude", "Altitude", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "direction", "Direction", i18n ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Form createPhotoInfoMixinForm()
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

    private static Input.Builder createTextLine( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.TEXT_LINE ).label( label ).name( name ).
            labelI18nKey( i18n + "." + name + ".label" ).immutable( true );
    }

    private static Input.Builder createLong( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.LONG ).label( label ).name( name ).
            labelI18nKey( i18n + "." + name + ".label" ).immutable( true );
    }

    private static Input.Builder createDate( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.DATE_TIME ).label( label ).name( name ).
            labelI18nKey( i18n + "." + name + ".label" ).immutable( true );
    }

    private static Input.Builder createGeoPoint( final String name, final String label, final String i18n )
    {
        return Input.create().inputType( InputTypeName.GEO_POINT ).label( label ).name( name ).
            labelI18nKey( i18n + "." + name + ".label" ).immutable( true );
    }

    private final Mixins mixins;

    public BuiltinMixinsTypes()
    {
        final List<Mixin> generatedSystemMixins = generateSystemMixins( MIXINS );
        this.mixins = Mixins.from( generatedSystemMixins );
    }

    private List<Mixin> generateSystemMixins( Iterable<Mixin> systemMixins )
    {
        final List<Mixin> generatedSystemMixins = Lists.newArrayList();
        for ( Mixin mixin : systemMixins )
        {
            mixin = Mixin.create( mixin ).
                icon( loadSchemaIcon( MIXINS_FOLDER, mixin.getName().getLocalName() ) ).
                build();
            generatedSystemMixins.add( mixin );
        }
        return generatedSystemMixins;
    }

    public Mixins getAll()
    {
        return this.mixins;
    }

    public Mixins getByApplication( final ApplicationKey key )
    {
        return this.mixins.filter( ( type ) -> type.getName().getApplicationKey().equals( key ) );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        return SchemaHelper.loadIcon( getClass(), metaInfFolderName, name );
    }
}
