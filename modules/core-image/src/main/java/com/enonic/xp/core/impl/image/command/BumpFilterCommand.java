/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.BumpFilter;

import com.enonic.xp.image.BuilderContext;

public final class BumpFilterCommand
    extends FilterCommand
{
    public BumpFilterCommand()
    {
        super( "bump" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new BumpFilter();
    }
}