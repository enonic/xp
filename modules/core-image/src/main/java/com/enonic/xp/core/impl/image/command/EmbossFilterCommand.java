/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.EmbossFilter;

import com.enonic.xp.image.BuilderContext;

public final class EmbossFilterCommand
    extends FilterCommand
{
    public EmbossFilterCommand()
    {
        super( "emboss" );
    }

    @Override
    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new EmbossFilter();
    }
}
