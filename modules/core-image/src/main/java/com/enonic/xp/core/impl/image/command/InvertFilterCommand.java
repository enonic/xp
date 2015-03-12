/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.InvertFilter;

public final class InvertFilterCommand
    extends FilterCommand
{
    public InvertFilterCommand()
    {
        super( "invert" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new InvertFilter();
    }
}
