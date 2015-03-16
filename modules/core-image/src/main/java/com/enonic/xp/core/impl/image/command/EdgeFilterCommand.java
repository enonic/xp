/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.EdgeFilter;

import com.enonic.xp.image.BuilderContext;

public final class EdgeFilterCommand
    extends FilterCommand
{
    public EdgeFilterCommand()
    {
        super( "edge" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new EdgeFilter();
    }
}