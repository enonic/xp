package com.enonic.wem.query;

import java.util.Collection;

import com.enonic.wem.api.facet.Facets;

public class QueryObjectModel
{
    private Constraint constraint;

    private QueryObjectModel( final Builder builder )
    {
        this.constraint = builder.constraint;
    }

    public Constraint getConstraint()
    {
        return this.constraint;
    }

    public Collection<Ordering> getOrderings()
    {
        return null;
    }

    public Facets getFacets()
    {
        return null;
    }

    public static Builder query()
    {
        return new Builder();
    }


    public static class Builder
    {
        private String name;

        private Constraint constraint;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder constraint( final Constraint constraint )
        {
            this.constraint = constraint;
            return this;
        }

        public QueryObjectModel build()
        {
            return new QueryObjectModel( this );
        }
    }
}