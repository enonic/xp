package com.enonic.xp.media;

import java.util.HashMap;
import java.util.Map;

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

    private static final ImageOrientation DEFAULT = ImageOrientation.TopLeft; // no rotation needed

    private static final Map<Integer, ImageOrientation> LOOKUP_TABLE = new HashMap<>();

    static
    {
        for ( final ImageOrientation imageOrientation : ImageOrientation.values() )
        {
            LOOKUP_TABLE.put( imageOrientation.value, imageOrientation );
        }
    }

    private final int value;

    private ImageOrientation( final int value )
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static ImageOrientation valueOf( final int value )
    {
        final ImageOrientation orientation = LOOKUP_TABLE.get( value );
        return orientation == null ? DEFAULT : orientation;
    }

    public static ImageOrientation from( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        try
        {
            final Integer intValue = Integer.valueOf( value );
            final ImageOrientation orientation = LOOKUP_TABLE.get( intValue );
            return orientation == null ? DEFAULT : orientation;
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
            final Integer intValue = Integer.valueOf( value );
            final ImageOrientation orientation = LOOKUP_TABLE.get( intValue );
            return orientation != null;
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
    }
}
