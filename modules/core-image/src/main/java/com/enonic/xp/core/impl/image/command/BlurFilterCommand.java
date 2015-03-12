/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.GaussianFilter;

public final class BlurFilterCommand
    extends FilterCommand
{
    public BlurFilterCommand()
    {
        super( "blur" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        GaussianFilter filter = new GaussianFilter();
        filter.setRadius( getIntArg( args, 0, 2 ) );
        return filter;
    }
}
