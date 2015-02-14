/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import com.enonic.xp.image.ImageFilter;

public final class OperationImageFilter
    implements ImageFilter
{
    private final BufferedImageOp operation;

    public OperationImageFilter( BufferedImageOp operation )
    {
        this.operation = operation;
    }

    public BufferedImage filter( BufferedImage source )
    {
        return this.operation.filter( source, null );
    }
}
