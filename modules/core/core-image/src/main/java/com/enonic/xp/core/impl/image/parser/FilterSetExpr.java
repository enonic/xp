/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.parser;

import java.util.ArrayList;
import java.util.List;

public final class FilterSetExpr
{
    private final List<FilterExpr> list;

    public FilterSetExpr()
    {
        this.list = new ArrayList<>();
    }

    public List<FilterExpr> getList()
    {
        return this.list;
    }

    public void add( FilterExpr expr )
    {
        this.list.add( expr );
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for ( FilterExpr expr : this.list )
        {
            if ( str.length() > 0 )
            {
                str.append( ";" );
            }

            str.append( expr.toString() );
        }

        return str.toString();
    }
}
