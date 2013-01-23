/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter;

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
