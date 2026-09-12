package com.enonic.xp.style;

/** Validated, immutable processing settings, independent of a style's editor metadata. */
public record ImageStyleSettings(String aspectRatio, String filter, int quality, int background)
{
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

    public static void checkOverrides( final String style, final boolean hasOverrides )
    {
        if ( style != null && hasOverrides )
        {
            throw new IllegalArgumentException( "Image styles cannot be combined with processing parameters" );
        }
    }
}
