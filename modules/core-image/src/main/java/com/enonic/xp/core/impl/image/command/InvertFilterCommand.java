/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.InvertFilter;

import com.enonic.xp.image.BuilderContext;

public final class InvertFilterCommand
    extends FilterCommand
{
    public InvertFilterCommand()
    {
        super( "invert" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new InvertFilter();
    }
}
