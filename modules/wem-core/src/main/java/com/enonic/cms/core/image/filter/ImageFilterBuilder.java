/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter;

import com.enonic.cms.core.image.filter.command.FilterCommand;
import com.enonic.cms.core.image.filter.command.FilterCommandRegistry;
import com.enonic.cms.core.image.filter.parser.FilterExpr;
import com.enonic.cms.core.image.filter.parser.FilterExprParser;
import com.enonic.cms.core.image.filter.parser.FilterSetExpr;

public final class ImageFilterBuilder
{
    private final FilterExprParser parser;

    private final FilterCommandRegistry commandRegistry;

    public ImageFilterBuilder()
    {
        this.parser = new FilterExprParser();
        this.commandRegistry = new FilterCommandRegistry();
    }

    public ImageFilter build( BuilderContext context, String expr )
    {
        return build( context, this.parser.parse( expr ) );
    }

    private ImageFilter build( BuilderContext context, FilterSetExpr set )
    {
        ImageFilterSet filter = new ImageFilterSet();

        for ( FilterExpr expr : set.getList() )
        {
            filter.addFilter( build( context, expr ) );
        }

        return filter;
    }

    private ImageFilter build( BuilderContext context, FilterExpr expr )
    {
        return createFilter( context, expr.getName(), expr.getArguments() );
    }

    private ImageFilter createFilter( BuilderContext context, String name, Object[] args )
    {
        FilterCommand command = this.commandRegistry.getCommand( name );
        if ( command != null )
        {
            return command.build( context, args );
        }
        else
        {
            return null;
        }
    }
}
