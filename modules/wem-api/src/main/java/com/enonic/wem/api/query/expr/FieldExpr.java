package com.enonic.wem.api.query.expr;

public final class FieldExpr
    implements Expression
{
    public final String name;

    public FieldExpr( final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public static FieldExpr from( final String name )
    {
        return new FieldExpr( name );
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FieldExpr fieldExpr = (FieldExpr) o;

        if ( name != null ? !name.equals( fieldExpr.name ) : fieldExpr.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
