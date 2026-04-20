package com.enonic.xp.query.expr;

import com.enonic.xp.index.IndexPath;

import static java.util.Objects.requireNonNull;


public final class FieldExpr
    implements Expression
{
    private final IndexPath indexPath;

    private FieldExpr( final IndexPath indexPath )
    {
        this.indexPath = requireNonNull( indexPath );
    }

    @Deprecated
    public String getFieldPath()
    {
        return this.indexPath.getPath();
    }

    public IndexPath getIndexPath()
    {
        return this.indexPath;
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
        return this == o || o instanceof FieldExpr && indexPath.equals( ( (FieldExpr) o ).indexPath );
    }

    @Override
    public int hashCode()
    {
        return indexPath.hashCode();
    }
}
