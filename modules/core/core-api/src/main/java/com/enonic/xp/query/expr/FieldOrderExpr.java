package com.enonic.xp.query.expr;

import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.index.IndexPath;

import static java.util.Objects.requireNonNull;


public final class FieldOrderExpr
    extends OrderExpr
{
    private final FieldExpr field;

    private final Locale language;

    public FieldOrderExpr( final FieldExpr field, final Direction direction )
    {
        this( field, null, direction );
    }

    public FieldOrderExpr( final FieldExpr field, final Locale language, final Direction direction )
    {
        super( direction );
        this.field = requireNonNull( field );
        this.language = language;
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    public static FieldOrderExpr create( final IndexPath indexPath, Direction direction, final Locale language )
    {
        return new FieldOrderExpr( FieldExpr.from( indexPath ), language, direction );
    }

    public static FieldOrderExpr create( final IndexPath indexPath, Direction direction )
    {
        return new FieldOrderExpr( FieldExpr.from( indexPath ), direction );
    }

    public Locale getLanguage()
    {
        return language;
    }

    @Override
    public String toString()
    {
        return this.field + ( language != null ? " COLLATE " + language.toLanguageTag() : "" ) +
            ( getDirection() != null ? " " + getDirection().name() : "" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FieldOrderExpr ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final FieldOrderExpr that = (FieldOrderExpr) o;
        return field.equals( that.field ) && Objects.equals( language, that.language );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), field, language );
    }
}
