package com.enonic.xp.core.impl.schema.xdata;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDatas;

import static com.enonic.xp.media.MediaInfo.CAMERA_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_GEO_POINT;
import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;

final class BuiltinXDataTypes
{
    private static final String XDATA_FOLDER = "x-data";

    private static final XData IMAGE_METADATA = XData.create().
        name( IMAGE_INFO_METADATA_NAME ).
        displayName( "Image Info" ).
        displayNameI18nKey( "media.imageInfo.displayName" ).
        form( createImageInfoXDataForm() ).
        build();

    private static final XData CAMERA_METADATA = XData.create().
        name( CAMERA_INFO_METADATA_NAME ).
        displayName( "Photo Info" ).
        displayNameI18nKey( "media.cameraInfo.displayName" ).
        form( createPhotoInfoXDataForm() ).
        build();

    private static final XData GPS_METADATA = XData.create().
        name( GPS_INFO_METADATA_NAME ).
        displayName( "Gps Info" ).
        displayNameI18nKey( "base.gpsInfo.displayName" ).
        form( createGpsInfoXDataForm() ).
        build();

    private static final XDatas MIXINS = XDatas.from( IMAGE_METADATA, CAMERA_METADATA, GPS_METADATA );

    private final XDatas mixins;

    public BuiltinXDataTypes()
    {
        final List<XData> generatedSystemXDatas = generateSystemXDatas( MIXINS );
        this.mixins = XDatas.from( generatedSystemXDatas );
    }

    private static Form createImageInfoXDataForm()
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

    private static Form createGpsInfoXDataForm()
    {
        final String i18n = "base.gpsInfo";
        final Form.Builder form = Form.create();
        form.addFormItem( createGeoPoint( GPS_INFO_GEO_POINT, "Geo Point", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "altitude", "Altitude", i18n ).occurrences( 0, 1 ).build() );
        form.addFormItem( createTextLine( "direction", "Direction", i18n ).occurrences( 0, 1 ).build() );

        return form.build();
    }

    private static Form createPhotoInfoXDataForm()
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

    private List<XData> generateSystemXDatas( Iterable<XData> systemXDatas )
    {
        final List<XData> generatedSystemXDatas = Lists.newArrayList();
        for ( XData mixin : systemXDatas )
        {
            mixin = XData.create( mixin ).
                icon( loadSchemaIcon( XDATA_FOLDER, mixin.getName().getLocalName() ) ).
                build();
            generatedSystemXDatas.add( mixin );
        }
        return generatedSystemXDatas;
    }

    public XDatas getAll()
    {
        return this.mixins;
    }

    public XDatas getByApplication( final ApplicationKey key )
    {
        return this.mixins.filter( ( type ) -> type.getName().getApplicationKey().equals( key ) );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        return SchemaHelper.loadIcon( getClass(), metaInfFolderName, name );
    }
}
