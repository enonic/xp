package com.enonic.xp.image;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class FocalPoint
{
    public final static FocalPoint DEFAULT = new FocalPoint( 0.5f, 0.5f );

    private final float x;

    private final float y;

    public FocalPoint( final float x, final float y )
    {
        Preconditions.checkArgument( x >= 0.0 && x <= 1.0, "FocalPoint x value must be between 0 and 1 : %s", x );
        Preconditions.checkArgument( y >= 0.0 && y <= 1.0, "FocalPoint y value must be between 0 and 1 : %s", y );
        this.x = x;
        this.y = y;
    }

    public float xOffset()
    {
        return x;
    }

    public float yOffset()
    {
        return y;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).add( "x", x ).add( "y", y ).toString();
    }
}
