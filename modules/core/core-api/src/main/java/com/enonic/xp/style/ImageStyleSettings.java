package com.enonic.xp.style;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Validated processing settings, independent of a style's name and editor metadata.
 * These values define a rendition independently of its style alias.
 * Filter syntax is validated when an image is processed.
 *
 * @param aspectRatio a positive integer {@code width:height} ratio
 * @param filter the filter specification
 * @param quality the encoder quality from 0 through 100
 * @param background the RGB background from {@code 0x000000} through {@code 0xFFFFFF}
 */
@NullMarked
public record ImageStyleSettings(@Nullable String aspectRatio, @Nullable String filter, int quality, int background)
{
    /**
     * Creates settings and validates the aspect ratio, quality and background ranges.
     *
     * @param aspectRatio a positive integer {@code width:height} ratio
     * @param filter the filter specification
     * @param quality the encoder quality from 0 through 100
     * @param background the RGB background from {@code 0x000000} through {@code 0xFFFFFF}
     * @throws IllegalArgumentException if a ratio component is invalid or outside the integer range,
     *     or quality or background is out of range
     */
    public ImageStyleSettings
    {
        if ( aspectRatio != null )
        {
            if ( !aspectRatio.matches( "[1-9][0-9]*:[1-9][0-9]*" ) )
            {
                throw new IllegalArgumentException( "Invalid image aspect ratio" );
            }
            final String[] ratio = aspectRatio.split( ":" );
            Integer.parseInt( ratio[0] );
            Integer.parseInt( ratio[1] );
        }
        if ( quality < 0 || quality > 100 || background < 0 || background > 0xffffff )
        {
            throw new IllegalArgumentException( "Invalid image style quality or background" );
        }
    }

    /**
     * Resolves and validates a style's processing settings.
     * An unspecified quality becomes 85 and an unspecified background becomes white.
     * Hexadecimal backgrounds may have a {@code 0x} prefix and use either letter case.
     *
     * @param style the image style to resolve
     * @return validated settings with defaults applied
     * @throws IllegalArgumentException if the aspect ratio, quality or background is invalid
     */
    public static ImageStyleSettings from( final ImageStyle style )
    {
        final String background = style.getBackground();
        if ( background != null && !background.matches( "(?:0x)?[0-9a-fA-F]{1,6}" ) )
        {
            throw new IllegalArgumentException( "Invalid image style background" );
        }
        return new ImageStyleSettings( style.getAspectRatio(), style.getFilter(), style.getQuality() == null ? 85 : style.getQuality(),
            background == null ? 0xffffff : Integer.parseInt( background.replaceFirst( "^0x", "" ), 16 ) );
    }

    /**
     * Rejects explicit processing overrides when a style is supplied.
     * This checks only the combination of arguments, not the processing values themselves.
     *
     * @param hasStyle whether a style was supplied
     * @param hasOverrides whether explicit quality, filter or background values were supplied
     * @throws IllegalArgumentException if a style is combined with overrides
     */
    public static void checkOverrides( final boolean hasStyle, final boolean hasOverrides )
    {
        if ( hasStyle && hasOverrides )
        {
            throw new IllegalArgumentException( "Image styles cannot be combined with processing parameters" );
        }
    }
}
