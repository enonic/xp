package com.enonic.wem.api.query.facet;

import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.filter.Filter;

public class QueryFacetQuery
    extends FacetQuery
{

    private final QueryExpr query;

    private final Filter queryFilter;

    private QueryFacetQuery( final Builder builder )
    {
        super( builder.name );
        this.query = builder.query;
        this.queryFilter = builder.queryFilter;
    }

    public QueryExpr getQuery()
    {
        return query;
    }

    public Filter getQueryFilter()
    {
        return queryFilter;
    }

    public static class Builder
        extends FacetQuery.Builder
    {
        private QueryExpr query;

        private Filter queryFilter;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder filter( final Filter filter )
        {
            queryFilter = filter;
            return this;
        }

        public Builder query( final QueryExpr query )
        {
            this.query = query;
            return this;
        }

        public QueryFacetQuery build()
        {
            return new QueryFacetQuery( this );
        }

    }
}
