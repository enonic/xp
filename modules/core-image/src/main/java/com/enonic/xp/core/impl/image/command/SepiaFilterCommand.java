/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.image.BuilderContext;
import com.enonic.xp.core.impl.image.effect.SepiaFilter;

public final class SepiaFilterCommand
    extends FilterCommand
{
    public SepiaFilterCommand()
    {
        super( "sepia" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        int d = getIntArg( args, 0, 20 );
        return new SepiaFilter( d );
    }
}