/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl.command;

import com.enonic.wem.api.image.BuilderContext;
import com.enonic.wem.api.image.filter.ScaleMaxFilter;

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