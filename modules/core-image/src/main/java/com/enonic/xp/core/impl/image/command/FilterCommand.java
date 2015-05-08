/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import java.awt.image.BufferedImageOp;

import com.enonic.xp.core.impl.image.AwtImageFilter;
import com.enonic.xp.core.impl.image.OperationImageFilter;
import com.enonic.xp.image.ImageFilter;

public abstract class FilterCommand
    extends BaseImageCommand
{
    public FilterCommand( String name )
    {
        super( name );
    }

    public final ImageFilter build( Object[] args )
    {
        Object filter = doBuild( args );
        if ( filter instanceof BufferedImageOp )
        {
            return wrap( (BufferedImageOp) filter );
        }
        else if ( filter instanceof ImageFilter )
        {
            return (ImageFilter) filter;
        }
        else if ( filter instanceof java.awt.image.ImageFilter )
        {
            return wrap( (java.awt.image.ImageFilter) filter );
        }
        else
        {
            return null;
        }
    }

    protected abstract Object doBuild( Object[] args );

    private ImageFilter wrap( BufferedImageOp operation )
    {
        return new OperationImageFilter( operation );
    }

    private ImageFilter wrap( java.awt.image.ImageFilter operation )
    {
        return new AwtImageFilter( operation );
    }
}
