/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public final class ImageFilterSet
    implements ImageFilter
{
    private final List<ImageFilter> list;

    public ImageFilterSet()
    {
        this.list = new ArrayList<>();
    }

    public void addFilter( ImageFilter filter )
    {
        if ( filter != null )
        {
            this.list.add( filter );
        }
    }

    @Override
    public BufferedImage filter( BufferedImage source )
    {
        BufferedImage target = source;

        for ( ImageFilter filter : this.list )
        {
            target = filter.filter( target );
        }

        return target;
    }
}
