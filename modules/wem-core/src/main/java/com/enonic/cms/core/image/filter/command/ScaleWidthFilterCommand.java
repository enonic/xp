/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.command;

import com.enonic.cms.core.image.filter.BuilderContext;
import com.enonic.cms.core.image.filter.effect.ScaleWidthFilter;

public final class ScaleWidthFilterCommand
    extends FilterCommand
{
    public ScaleWidthFilterCommand()
    {
        super( "scalewidth" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleWidthFilter( getIntArg( args, 0, 100 ) );
    }
}