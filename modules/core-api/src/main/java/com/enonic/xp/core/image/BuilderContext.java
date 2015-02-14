package com.enonic.xp.core.image;

public final class BuilderContext
{
    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private int backgroundColor = DEFAULT_BACKGROUND;

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void setBackgroundColor( int backgroundColor )
    {
        this.backgroundColor = backgroundColor;
    }
}
