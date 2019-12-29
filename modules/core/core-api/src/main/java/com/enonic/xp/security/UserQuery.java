package com.enonic.xp.security;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.query.expr.QueryExpr;

@PublicApi
public final class UserQuery
{
    private static final int DEFAULT_SIZE = 10;

    private static final int GET_ALL_SIZE_FLAG = -1;

    private final int from;

    private final int size;

    private final QueryExpr queryExpr;

    public UserQuery( final Builder builder )
    {
        from = builder.from;
        size = builder.size;
        queryExpr = builder.queryExpr;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public QueryExpr getQueryExpr()
    {
        return queryExpr;
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
        final UserQuery that = (UserQuery) o;
        return from == that.from &&
            size == that.size &&
            Objects.equals( queryExpr, that.queryExpr );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( from, size, queryExpr );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int from = 0;

        private int size = DEFAULT_SIZE;

        private QueryExpr queryExpr;

        private Builder()
        {
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder getAll()
        {
            this.size = GET_ALL_SIZE_FLAG;
            return this;
        }

        public Builder queryExpr( final QueryExpr queryExpr )
        {
            this.queryExpr = queryExpr;
            return this;
        }

        public UserQuery build()
        {
            return new UserQuery( this );
        }
    }

}
