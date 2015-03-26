/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.enonic.xp.core.impl.image.effect.RoundedFilter;

public final class RoundedFilterCommand
    extends FilterCommand
{
    public RoundedFilterCommand()
    {
        super( "rounded" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new RoundedFilter( getIntArg( args, 0, 10 ), getIntArg( args, 1, 0 ), getIntArg( args, 2, 0x000000 ) );
    }
}
