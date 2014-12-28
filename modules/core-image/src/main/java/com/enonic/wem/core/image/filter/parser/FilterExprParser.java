/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.image.filter.parser;

public final class FilterExprParser
{
    public FilterSetExpr parse( String value )
    {
        if ( value == null )
        {
            return new FilterSetExpr();
        }

        value = value.trim();
        if ( value.length() == 0 )
        {
            return new FilterSetExpr();
        }

        return new FilterExprParserContext( value ).parseFilterSet();
    }
}
