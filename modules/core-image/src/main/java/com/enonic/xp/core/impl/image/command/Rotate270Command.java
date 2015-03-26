/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.FlipFilter;

public final class Rotate270Command
    extends FilterCommand
{
    public Rotate270Command()
    {
        super( "rotate270" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new FlipFilter( FlipFilter.FLIP_90CCW );
    }
}