/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.image.BuilderContext;
import com.enonic.xp.core.impl.image.effect.HSBColorizeFilter;

public final class HSBColorizeFilterCommand
    extends FilterCommand
{
    public HSBColorizeFilterCommand()
    {
        super( "hsbcolorize" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new HSBColorizeFilter( getIntArg( args, 0, 0xFFFFFF ) );
    }
}