/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.command;

import com.jhlabs.image.SharpenFilter;

import com.enonic.cms.core.image.filter.BuilderContext;

public final class SharpenFilterCommand
    extends FilterCommand
{
    public SharpenFilterCommand()
    {
        super( "sharpen" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new SharpenFilter();
    }
}