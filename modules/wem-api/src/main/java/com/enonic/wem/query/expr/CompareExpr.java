/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query.expr;

import com.enonic.wem.query.Expression;

/**
 * This class implements the compare expression.
 */
public class CompareExpr
    extends BinaryExpr
{
    /**
     * Operator constants.
     */
    public final static int EQ = 0;

    public final static int NEQ = 1;

    public final static int LT = 2;

    public final static int LTE = 3;

    public final static int GT = 4;

    public final static int GTE = 5;

    public final static int LIKE = 6;

    public final static int IN = 7;

    public final static int NOT_LIKE = 8;

    public final static int NOT_IN = 9;

    public final static int FT = 10;

    public final static int relationExists = 11;

    public final static int fulltext = 12;


    /**
     * Operator type.
     */
    private final int op;

    /**
     * Construct the operator expression.
     */
    public CompareExpr( int op, Expression left, Expression right )
    {
        super( left, right );
        this.op = op;
    }

    /**
     * Return the operator.
     */
    public int getOperator()
    {
        return this.op;
    }

    /**
     * Return true if relational operator.
     */
    public boolean isRelationalOperator()
    {
        return this.op < LIKE;
    }

    /**
     * Return true if match operator.
     */
    public boolean isMatchOperator()
    {
        return this.op >= LIKE;
    }

    /**
     * Return the operator as string.
     */
    public String getToken()
    {
        switch ( this.op )
        {
            case EQ:
                return "=";
            case NEQ:
                return "!=";
            case LT:
                return "<";
            case LTE:
                return "<=";
            case GT:
                return ">";
            case GTE:
                return ">=";
            case LIKE:
                return "CONTAINS";
            case IN:
                return "IN";
            case NOT_LIKE:
                return "NOT CONTAINS";
            case NOT_IN:
                return "NOT IN";
            case FT:
                return "FT";
            default:
                return null;
        }
    }

    /**
     * Return the expression as string.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( getLeft().toString() );
        if ( this.op != relationExists)
        str.append( " " );
        str.append( getToken() );
        str.append( " " );
        str.append( getRight().toString() );
        return str.toString();
    }

}
