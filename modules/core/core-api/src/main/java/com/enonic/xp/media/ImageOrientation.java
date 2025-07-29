package com.enonic.xp.media;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Image orientation values from EXIF metadata
// See http://www.impulseadventure.com/photo/exif-orientation.html
public enum ImageOrientation
{
    TopLeft( 1 ), // 0th row at top, 0th column at left
    TopRight( 2 ), // 0th row at top, 0th column at right
    BottomRight( 3 ), // 0th row at bottom, 0th column at right
    BottomLeft( 4 ), // 0th row at bottom, 0th column at left
    LeftTop( 5 ), // 0th row at left, 0th column at top
    RightTop( 6 ), // 0th row at right, 0th column at top
    RightBottom( 7 ), // 0th row at right, 0th column at bottom
    LeftBottom( 8 ); // 0th row at left, 0th column at bottom

    public static final ImageOrientation DEFAULT = ImageOrientation.TopLeft; // no rotation needed

    private static final Map<Integer, ImageOrientation> LOOKUP_TABLE =
        Arrays.stream( values() ).collect( Collectors.toUnmodifiableMap( e -> e.value, Function.identity() ) );

    private final int value;

    ImageOrientation( final int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static ImageOrientation valueOf( final int value )
    {
        return LOOKUP_TABLE.getOrDefault( value, DEFAULT );
    }

    public static ImageOrientation from( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        try
        {
            return valueOf( Integer.parseInt( value ) );
        }
        catch ( NumberFormatException e )
        {
            return DEFAULT;
        }
    }

    public static boolean isValid( final String value )
    {
        if ( value == null )
        {
            return false;
        }
        try
        {
            return LOOKUP_TABLE.containsKey( Integer.parseInt( value ) );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }
}
