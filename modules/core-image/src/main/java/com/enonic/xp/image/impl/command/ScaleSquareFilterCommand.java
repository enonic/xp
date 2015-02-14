/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl.command;

import com.enonic.wem.api.image.BuilderContext;
import com.enonic.wem.api.image.filter.ScaleSquareFilter;

public final class ScaleSquareFilterCommand
    extends FilterCommand
{
    public ScaleSquareFilterCommand()
    {
        super( "scalesquare" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleSquareFilter( getIntArg( args, 0, 0 ) );
    }
}