/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.command;

import com.jhlabs.image.FlipFilter;

import com.enonic.cms.core.image.filter.BuilderContext;

public final class FlipHorizontalCommand
    extends FilterCommand
{
    public FlipHorizontalCommand()
    {
        super( "fliph" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new FlipFilter( FlipFilter.FLIP_H );
    }
}