/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageFunctionChain
    implements ImageFunction
{
    private final List<ImageFunction> list;

    public ImageFunctionChain( final List<ImageFunction> list )
    {
        this.list = List.copyOf( list );
    }

    @Override
    public BufferedImage apply( final BufferedImage source )
    {
        BufferedImage target = source;

        for ( ImageFunction filter : this.list )
        {
            target = filter.apply( target );
        }

        return target;
    }
}
