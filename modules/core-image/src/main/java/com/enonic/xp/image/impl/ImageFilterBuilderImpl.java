/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.image.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.image.BuilderContext;
import com.enonic.wem.api.image.ImageFilter;
import com.enonic.wem.api.image.ImageFilterBuilder;
import com.enonic.xp.image.impl.command.FilterCommand;
import com.enonic.xp.image.impl.command.FilterCommandRegistry;
import com.enonic.xp.image.impl.parser.FilterExpr;
import com.enonic.xp.image.impl.parser.FilterExprParser;
import com.enonic.xp.image.impl.parser.FilterSetExpr;

@Component(immediate = true)
public final class ImageFilterBuilderImpl
    implements ImageFilterBuilder
{
    private final FilterExprParser parser;

    private final FilterCommandRegistry commandRegistry;

    public ImageFilterBuilderImpl()
    {
        this.parser = new FilterExprParser();
        this.commandRegistry = new FilterCommandRegistry();
    }

    @Override
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
