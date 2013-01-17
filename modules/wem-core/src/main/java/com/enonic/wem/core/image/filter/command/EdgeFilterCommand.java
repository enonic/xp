/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.jhlabs.image.EdgeFilter;

import com.enonic.wem.core.image.filter.BuilderContext;

public final class EdgeFilterCommand
    extends FilterCommand
{
    public EdgeFilterCommand()
    {
        super( "edge" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new EdgeFilter();
    }
}