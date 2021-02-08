/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.parser;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;

public final class FilterSetExpr
{
    private final List<FilterExpr> list;

    FilterSetExpr( final List<FilterExpr> list )
    {
        this.list = list;
    }

    public List<FilterExpr> getList()
    {
        return this.list;
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public String toString()
    {
        return this.list.stream().map( FilterExpr::toString ).collect( Collectors.joining( ";" ) );
    }

    public static FilterSetExpr parse( final String value )
    {
        if ( nullToEmpty( value ).isBlank() )
        {
            return new FilterSetExpr( List.of() );
        }

        return new FilterExprParserContext( value ).parseFilterSet();
    }
}
