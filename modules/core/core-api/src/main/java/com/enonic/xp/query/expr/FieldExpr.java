package com.enonic.xp.query.expr;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.IndexPath;

@PublicApi
public final class FieldExpr
    implements Expression
{
    private final IndexPath indexPath;

    private FieldExpr( final IndexPath indexPath )
    {
        this.indexPath = indexPath;
    }

    public String getFieldPath()
    {
        return this.indexPath.getPath();
    }

    public static FieldExpr from( final IndexPath indexPath )
    {
        return new FieldExpr( indexPath );
    }

    public static FieldExpr from( final String indexPath )
    {
        return new FieldExpr( IndexPath.from( indexPath ) );
    }


    @Override
    public String toString()
    {
        return this.indexPath.toString();
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

        return indexPath != null ? indexPath.equals( fieldExpr.indexPath ) : fieldExpr.indexPath == null;
    }

    @Override
    public int hashCode()
    {
        return indexPath != null ? indexPath.hashCode() : 0;
    }
}
