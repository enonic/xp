/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.SharpenFilter;

public final class SharpenFilterCommand
    extends FilterCommand
{
    public SharpenFilterCommand()
    {
        super( "sharpen" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new SharpenFilter();
    }
}