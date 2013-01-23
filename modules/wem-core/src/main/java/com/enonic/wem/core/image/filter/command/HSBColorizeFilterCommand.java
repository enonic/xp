/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.enonic.wem.core.image.filter.BuilderContext;
import com.enonic.wem.core.image.filter.effect.HSBColorizeFilter;

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