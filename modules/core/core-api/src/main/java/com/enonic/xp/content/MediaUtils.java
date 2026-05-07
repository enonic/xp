package com.enonic.xp.content;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;

public final class MediaUtils
{
    private MediaUtils()
    {
    }

    public static @Nullable Cropping readCropping( final @Nullable PropertySet mediaSet )
    {
        if ( mediaSet == null )
        {
            return null;
        }
        final PropertySet croppingSet = mediaSet.getSet( ContentPropertyNames.MEDIA_CROPPING );
        if ( croppingSet == null )
        {
            return null;
        }
        final Double top = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_TOP );
        final Double left = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT );
        final Double bottom = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM );
        final Double right = croppingSet.getDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT );
        if ( top == null || left == null || bottom == null || right == null )
        {
            return null;
        }
        return Cropping.create().top( top ).left( left ).bottom( bottom ).right( right ).build();
    }

    public static @Nullable ImageOrientation readOrientation( final @Nullable PropertySet mediaSet )
    {
        if ( mediaSet == null )
        {
            return null;
        }
        try
        {
            final Long value = mediaSet.getLong( ContentPropertyNames.ORIENTATION );
            return value == null ? null : ImageOrientation.valueOf( Math.toIntExact( value ) );
        }
        catch ( ArithmeticException e )
        {
            return ImageOrientation.DEFAULT;
        }
    }

    public static @Nullable FocalPoint readFocalPoint( final @Nullable PropertySet mediaSet )
    {
        if ( mediaSet == null )
        {
            return null;
        }
        final PropertySet focalSet = mediaSet.getSet( ContentPropertyNames.MEDIA_FOCAL_POINT );
        if ( focalSet == null )
        {
            return null;
        }
        final Double x = focalSet.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_X );
        final Double y = focalSet.getDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_Y );
        if ( x == null || y == null )
        {
            return null;
        }
        return new FocalPoint( x, y );
    }
}
