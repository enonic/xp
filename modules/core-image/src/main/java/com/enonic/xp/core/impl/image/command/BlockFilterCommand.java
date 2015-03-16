/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.BlockFilter;

import com.enonic.xp.image.BuilderContext;

public final class BlockFilterCommand
    extends FilterCommand
{
    public BlockFilterCommand()
    {
        super( "block" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        BlockFilter filter = new BlockFilter();
        filter.setBlockSize( getIntArg( args, 0, 2 ) );
        return filter;
    }
}
