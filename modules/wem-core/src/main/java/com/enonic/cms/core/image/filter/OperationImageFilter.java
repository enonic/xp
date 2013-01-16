/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

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
