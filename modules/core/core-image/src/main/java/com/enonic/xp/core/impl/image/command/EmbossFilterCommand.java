/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.command;

import com.jhlabs.image.EmbossFilter;

public final class EmbossFilterCommand
    extends FilterCommand
{
    public EmbossFilterCommand()
    {
        super( "emboss" );
    }

    @Override
    protected Object doBuild( Object[] args )
    {
        return new EmbossFilter();
    }
}
