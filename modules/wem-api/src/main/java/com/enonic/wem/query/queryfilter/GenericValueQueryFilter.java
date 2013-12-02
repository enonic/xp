package com.enonic.wem.query.queryfilter;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Value;

public class GenericValueQueryFilter
    extends ValueQueryFilter
{
    private GenericValueQueryFilter( final Builder builder )
    {
        super( builder.fieldName, builder.values );
    }

    public static class Builder
    {
        private Set<Value> values = Sets.newHashSet();

        private String fieldName;

        public Builder add( final Value... values )
        {
            this.values.addAll( Sets.newHashSet( values ) );
            return this;
        }

        public Builder fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return this;
        }

        public GenericValueQueryFilter build()
        {
            return new GenericValueQueryFilter( this );
        }

    }

}
