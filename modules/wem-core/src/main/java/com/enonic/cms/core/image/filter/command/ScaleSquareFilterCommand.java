/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.command;

import com.enonic.cms.core.image.filter.BuilderContext;
import com.enonic.cms.core.image.filter.effect.ScaleSquareFilter;

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