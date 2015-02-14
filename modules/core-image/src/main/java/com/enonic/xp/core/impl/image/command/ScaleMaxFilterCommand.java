/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.BuilderContext;
import com.enonic.xp.image.filter.ScaleMaxFilter;

public final class ScaleMaxFilterCommand
    extends FilterCommand
{
    public ScaleMaxFilterCommand()
    {
        super( "scalemax" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleMaxFilter( getIntArg( args, 0, 0 ) );
    }
}