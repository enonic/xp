/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query.expr;

import com.enonic.wem.query.Expression;

/**
 * This class implements the value expression.
 */
public final class ArrayExpr
    extends ValueExpr<ValueExpr[]>
{
    private final String between;

    /**
     * Construct the value.
     */
    public ArrayExpr( ValueExpr[] values, final String between )
    {
        super( values );
        this.between = between;
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        return buildString( this.between );
    }

    @Override
    public String getValueAsString()
    {
        return buildString( this.between );
    }

    private String buildString( String between )
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( between.charAt( 0 ) );

        for ( int i = 0; i < this.value.length; i++ )
        {
            if ( i > 0 )
            {
                stringBuilder.append( ", " );
            }

            stringBuilder.append( this.value[i].toString() );
        }

        stringBuilder.append( between.charAt( 1 ) );
        return stringBuilder.toString();
    }
}
