/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.GrayscaleFilter;

public final class GrayscaleFilterCommand
    extends FilterCommand
{
    public GrayscaleFilterCommand()
    {
        super( "grayscale" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new GrayscaleFilter();
    }
}