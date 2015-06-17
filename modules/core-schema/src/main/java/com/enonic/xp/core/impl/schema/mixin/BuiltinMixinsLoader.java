package com.enonic.xp.core.impl.schema.mixin;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class BuiltinMixinsLoader
{
    public static final MixinName IMAGE_INFO_METADATA_NAME = MixinName.from( ModuleKey.MEDIA_MOD, "image-info" );

    public static final MixinName PHOTO_INFO_METADATA_NAME = MixinName.from( ModuleKey.MEDIA_MOD, "photo-info" );

    public static final MixinName GPS_INFO_METADATA_NAME = MixinName.from( ModuleKey.BASE, "gps-info" );

    private static final String MIXINS_FOLDER = "mixins";

    private static final Mixin IMAGE_METADATA = Mixin.newMixin().
        name( IMAGE_INFO_METADATA_NAME ).
        displayName( "Image Info" ).
        formItems( createImageInfoMixinForm() ).
        build();

    private static final Mixin PHOTO_METADATA = Mixin.newMixin().
        name( PHOTO_INFO_METADATA_NAME ).
        displayName( "Photo Info" ).
        formItems( createPhotoInfoMixinForm() ).
        build();

    private static final Mixin GPS_METADATA = Mixin.newMixin().
        name( GPS_INFO_METADATA_NAME ).
        displayName( "Gps Info" ).
        formItems( createGpsInfoMixinForm() ).
        build();

    private static final Mixins MIXINS = Mixins.from( IMAGE_METADATA, PHOTO_METADATA, GPS_METADATA );

    private final Mixins mixins;

    public BuiltinMixinsLoader()
    {
        this.mixins = Mixins.from( generateSystemMixins() );
    }

    private static FormItems createImageInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( createLong( "pixelSize", "Size (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( "imageHeight", "Height (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( "imageWidth", "Width (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "contentType", "Content Type" ).occurrences( 0, 1 ).build() );
        formItems.add( createTextLine( "description", "Description" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( "bytesize", "Size (bytes)" ).occurrences( 0, 1 ).build() );
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
        formItems.add( createTextLine( "lens", "Lense" ).occurrences( 0, 1 ).build() );
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
        final List<Mixin> mixins = Lists.newArrayList();
        for ( Mixin mixin : MIXINS )
        {
            mixin = Mixin.newMixin( mixin ).
                icon( loadSchemaIcon( MIXINS_FOLDER, mixin.getName().getLocalName() ) ).
                build();
            mixins.add( mixin );
        }
        return mixins;
    }

    public Mixins load()
    {
        return this.mixins;
    }

    public Mixins loadByModule( final ModuleKey moduleKey )
    {
        List<Mixin> mixins = MIXINS.stream().
            filter( elem -> moduleKey.equals( elem.getName().getModuleKey() ) ).
            collect( Collectors.toList() );
        return Mixins.from( mixins );
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
