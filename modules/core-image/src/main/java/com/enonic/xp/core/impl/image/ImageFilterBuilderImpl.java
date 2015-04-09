/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.impl.image.command.FilterCommand;
import com.enonic.xp.core.impl.image.command.FilterCommandRegistry;
import com.enonic.xp.core.impl.image.parser.FilterExpr;
import com.enonic.xp.core.impl.image.parser.FilterExprParser;
import com.enonic.xp.core.impl.image.parser.FilterSetExpr;
import com.enonic.xp.image.ImageFilter;
import com.enonic.xp.image.ImageFilterBuilder;

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
    public ImageFilter build( String expr )
    {
        return build( this.parser.parse( expr ) );
    }

    private ImageFilter build( FilterSetExpr set )
    {
        ImageFilterSet filter = new ImageFilterSet();

        for ( FilterExpr expr : set.getList() )
        {
            filter.addFilter( build( expr ) );
        }

        return filter;
    }

    private ImageFilter build( FilterExpr expr )
    {
        return createFilter( expr.getName(), expr.getArguments() );
    }

    private ImageFilter createFilter( String name, Object[] args )
    {
        FilterCommand command = this.commandRegistry.getCommand( name );
        if ( command != null )
        {
            return command.build( args );
        }
        else
        {
            return null;
        }
    }
}
