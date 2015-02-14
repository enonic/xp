package com.enonic.xp.core.impl.schema.mixin;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.FormItems;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.mixin.Mixins;

@Component(immediate = true)
public final class BuiltinMixinProvider
    implements MixinProvider
{
    public static MixinName IMAGE_INFO_METADATA_NAME = MixinName.from( ModuleKey.from( "media" ), "image-info" );
    public static MixinName PHOTO_INFO_METADATA_NAME = MixinName.from( ModuleKey.from( "media" ), "photo-info" );
    public static MixinName GPS_INFO_METADATA_NAME = MixinName.from( ModuleKey.from( "base" ), "gps-info" );

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

    public BuiltinMixinProvider()
    {
        this.mixins = Mixins.from( generateSystemMixins() );
    }

    private static FormItems createImageInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add( createLong( "pixelSize", "Size (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( "imageHeight", "Height (px)" ).occurrences( 0, 1 ).build() );
        formItems.add( createLong( "imageWidth", "Width (px)" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "contentType", "Content Type" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "description", "Description" ).occurrences( 0, 1 ).build());
        formItems.add(createLong( "bytesize", "Size (bytes)" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "colorSpace", "Color Space" ).occurrences( 0, 0 ).build());
        formItems.add(createTextLine( "fileSource", "File Source" ).occurrences( 0, 1 ).build());

        return formItems;
    }

    private static FormItems createGpsInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add(createGeoPoint( "geoPoint", "Geo Point" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "altitude", "Altitude" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "direction", "Direction" ).occurrences( 0, 1 ).build());

        return formItems;
    }

    private static FormItems createPhotoInfoMixinForm()
    {
        final FormItems formItems = new FormItems();
        formItems.add(createDate( "date", "Date" ).occurrences( 0, 1 ).build() );
        formItems.add(createTextLine( "make", "Make" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "model", "Model" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "lens", "Lense" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "iso", "ISO" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "focalLength", "Focal Length" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "focalLength35", "Focal Length 35mm" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "exposureBias", "Exposure Bias" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "aperture", "Aperture" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "shutterTime", "Shutter Time" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "flash", "Flash" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "autoFlashCompensation", "Auto Flash Compensation" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "whiteBalance", "White Balance" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "exposureProgram", "Exposure Program" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "shootingMode", "Shooting Mode" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "meteringMode", "Metering Mode" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "exposureMode", "Exposure Mode" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "focusDistance", "Focus Distance" ).occurrences( 0, 1 ).build());
        formItems.add(createTextLine( "orientation", "Orientation" ).occurrences( 0, 1 ).build());

        return formItems;
    }

    private static Input.Builder createTextLine( final String name, final String label )
    {
        return Input.newInput().inputType( InputTypes.TEXT_LINE ).label( label ).name( name ).immutable( true );
    }

    private static Input.Builder createLong( final String name, final String label )
    {
        return Input.newInput().inputType( InputTypes.LONG ).label( label ).name( name ).immutable( true );
    }
    private static Input.Builder createDate( final String name, final String label )
    {
        return Input.newInput().inputType( InputTypes.DATE_TIME ).label( label ).name( name ).immutable( true );
    }
    private static Input.Builder createGeoPoint( final String name, final String label )
    {
        return Input.newInput().inputType( InputTypes.GEO_POINT).label( label ).name( name ).immutable( true );
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

    @Override
    public Mixins get()
    {
        return this.mixins;
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
