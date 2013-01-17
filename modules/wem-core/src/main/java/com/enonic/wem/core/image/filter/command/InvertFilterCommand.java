/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.command;

import com.jhlabs.image.InvertFilter;

import com.enonic.wem.core.image.filter.BuilderContext;

public final class InvertFilterCommand
    extends FilterCommand
{
    public InvertFilterCommand()
    {
        super( "invert" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new InvertFilter();
    }
}
