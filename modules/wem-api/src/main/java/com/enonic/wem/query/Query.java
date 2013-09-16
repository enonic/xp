package com.enonic.wem.query;

public final class Query
    implements Expression
{
    private final Constraint where;

    private final OrderBy orderBy;

    public Query( final Constraint where, final OrderBy orderBy )
    {
        this.where = where;
        this.orderBy = orderBy;
    }

    public Constraint getWhere()
    {
        return this.where;
    }

    public OrderBy getOrderBy()
    {
        return this.orderBy;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();
        if ( this.where != null )
        {
            str.append( this.where.toString() );
        }

        if ( this.orderBy != null )
        {
            if ( str.length() > 0 )
            {
                str.append( " " );
            }

            str.append( this.orderBy.toString() );
        }

        return str.toString();
    }
}
