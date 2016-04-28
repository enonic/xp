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
        form( createImageInfoMixinForm() ).
        build();

    private static final Mixin CAMERA_METADATA = Mixin.create().
        name( CAMERA_INFO_METADATA_NAME ).
        displayName( "Photo Info" ).
        form( createPhotoInfoMixinForm() ).
        build();

    private static final Mixin GPS_METADATA = Mixin.create().
        name( GPS_INFO_METADATA_NAME ).
        displayName( "Gps Info" ).
        form( createGpsInfoMixinForm() ).
        build();

    private static final Mixins MIXINS = Mixins.from( IMAGE_METADATA, CAMERA_METADATA, GPS_METADATA );

    private static Form createImageInfoMixinForm()
    {
        final Form.Builder form = Form.create();
        form.addFormItem( createLong( IMAGE_INFO_PIXEL_SIZE, "Size (px)" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( IMAGE_INFO_IMAGE_HEIGHT, "Height (px)" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( IMAGE_INFO_IMAGE_WIDTH, "Width (px)" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "contentType", "Content Type" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "description", "Description" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createLong( MEDIA_INFO_BYTE_SIZE, "Size (bytes)" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "colorSpace", "Color Space" ).occurrences( 0, 0 ).build() );
        form.addFormItem( createTextLine( "fileSource", "File Source" ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Form createGpsInfoMixinForm()
    {
        final Form.Builder form = Form.create();
        form.addFormItem( createGeoPoint( GPS_INFO_GEO_POINT, "Geo Point" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "altitude", "Altitude" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "direction", "Direction" ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Form createPhotoInfoMixinForm()
    {
        final Form.Builder form = Form.create();
        form.addFormItem( createDate( "date", "Date" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "make", "Make" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "model", "Model" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "lens", "Lens" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "iso", "ISO" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focalLength", "Focal Length" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focalLength35", "Focal Length 35mm" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureBias", "Exposure Bias" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "aperture", "Aperture" ).occurrences( 0, 0 ).build() );
        form.addFormItem( createTextLine( "shutterTime", "Shutter Time" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "flash", "Flash" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "autoFlashCompensation", "Auto Flash Compensation" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "whiteBalance", "White Balance" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureProgram", "Exposure Program" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "shootingMode", "Shooting Mode" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "meteringMode", "Metering Mode" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "exposureMode", "Exposure Mode" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "focusDistance", "Focus Distance" ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "orientation", "Orientation" ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Input.Builder createTextLine( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.TEXT_LINE ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createLong( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.LONG ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createDate( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.DATE_TIME ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createGeoPoint( final String name, final String label )
    {
        return Input.create().inputType( InputTypeName.GEO_POINT ).label( label ).name( name ).immutable( true );
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
