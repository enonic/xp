package com.enonic.xp.core.impl.schema.mixin;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.Mixins;

import static com.enonic.xp.media.MediaInfo.GPS_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_HEIGHT;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_IMAGE_WIDTH;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_METADATA_NAME;
import static com.enonic.xp.media.MediaInfo.IMAGE_INFO_PIXEL_SIZE;
import static com.enonic.xp.media.MediaInfo.MEDIA_INFO_BYTE_SIZE;
import static com.enonic.xp.media.MediaInfo.PHOTO_INFO_METADATA_NAME;

@Component(immediate = true)
public final class BuiltinMixinsLoader
{
    private static final String MIXINS_FOLDER = "mixins";

    private static final Mixin IMAGE_METADATA = Mixin.create().
        name( IMAGE_INFO_METADATA_NAME ).
        displayName( "Image Info" ).
        formItems( createImageInfoMixinForm() ).
        build();

    private static final Mixin PHOTO_METADATA = Mixin.create().
        name( PHOTO_INFO_METADATA_NAME ).
        displayName( "Photo Info" ).
        formItems( createPhotoInfoMixinForm() ).
        build();

    private static final Mixin GPS_METADATA = Mixin.create().
        name( GPS_INFO_METADATA_NAME ).
        displayName( "Gps Info" ).
        formItems( createGpsInfoMixinForm() ).
        build();

    private static final Mixins MIXINS = Mixins.from( IMAGE_METADATA, PHOTO_METADATA, GPS_METADATA );

    private static FormItems createImageInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( createLong( IMAGE_INFO_PIXEL_SIZE, "Size (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( IMAGE_INFO_IMAGE_HEIGHT, "Height (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( IMAGE_INFO_IMAGE_WIDTH, "Width (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "contentType", "Content Type" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "description", "Description" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( MEDIA_INFO_BYTE_SIZE, "Size (bytes)" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "colorSpace", "Color Space" ).occurrences( 0, 0 ).build() );
        formItems.add( createTextLine( "fileSource", "File Source" ).occurrences( 0, 1 ).build() );

        return formItems;
    }

    private static FormItems createGpsInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( createGeoPoint( "geoPoint", "Geo Point" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "altitude", "Altitude" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "direction", "Direction" ).occurrences( 0, 1 ).build() );

        return formItems;
    }

    private static FormItems createPhotoInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( createDate( "date", "Date" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "make", "Make" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "model", "Model" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "lens", "Lens" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "iso", "ISO" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "focalLength", "Focal Length" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "focalLength35", "Focal Length 35mm" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "exposureBias", "Exposure Bias" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "aperture", "Aperture" ).occurrences( 0, 0 ).build() );
        formItems.add( createTextLine( "shutterTime", "Shutter Time" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "flash", "Flash" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "autoFlashCompensation", "Auto Flash Compensation" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "whiteBalance", "White Balance" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "exposureProgram", "Exposure Program" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "shootingMode", "Shooting Mode" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "meteringMode", "Metering Mode" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "exposureMode", "Exposure Mode" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "focusDistance", "Focus Distance" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "orientation", "Orientation" ).occurrences( 0, 1 ).build() );

        return formItems;
    }

    private static Input.Builder createTextLine( final String name, final String label )
    {
        return Input.create().inputType( InputTypes.TEXT_LINE ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createLong( final String name, final String label )
    {
        return Input.create().inputType( InputTypes.LONG ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createDate( final String name, final String label )
    {
        return Input.create().inputType( InputTypes.DATE_TIME ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createGeoPoint( final String name, final String label )
    {
        return Input.create().inputType( InputTypes.GEO_POINT ).label( label ).name( name ).immutable( true );
    }

    private List<Mixin> generateSystemMixins()
    {
        return generateSystemMixins( MIXINS );
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

    public Mixins load()
    {
        final List<Mixin> generatedSystemMixins = generateSystemMixins();
        return Mixins.from( generatedSystemMixins );
    }

    public Mixins loadByModule( final ApplicationKey applicationKey )
    {
        final List<Mixin> systemMixinsByApplicationKey = MIXINS.stream().
            filter( mixin -> mixin.getName().getApplicationKey().equals( applicationKey ) ).
            collect( Collectors.toList() );
        final List<Mixin> generatedSystemMixins = generateSystemMixins( systemMixinsByApplicationKey );
        return Mixins.from( generatedSystemMixins );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try (final InputStream stream = this.getClass().getResourceAsStream( filePath ))
        {
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }
}
