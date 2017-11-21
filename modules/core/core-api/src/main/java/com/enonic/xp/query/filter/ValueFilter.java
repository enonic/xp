package com.enonic.xp.query.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

@Beta
public class ValueFilter
    extends FieldFilter
{
    private final ImmutableSet<Value> values;

    ValueFilter( final Builder builder )
    {
        super( builder );
        this.values = ImmutableSet.copyOf( builder.values );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).omitNullValues().
            add( "fieldName", fieldName ).
            add( "values", values ).
            toString();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public ImmutableSet<Value> getValues()
    {
        return values;
    }

    public static class Builder
        extends FieldFilter.Builder<Builder>
    {
        private final Set<Value> values = Sets.newHashSet();

        public Builder addValue( final Value value )
        {
            this.values.add( value );
            return this;
        }

        public Builder addValues( final Value... values )
        {
            this.values.addAll( Arrays.asList( values ) );
            return this;
        }

        public Builder addValues( final String... values )
        {
            return doAddValues( Arrays.asList( values ) );
        }

        public Builder addValues( final Collection<String> values )
        {
            return doAddValues( values );
        }

        public Builder addAllValues( final Collection<Value> values )
        {
            this.values.addAll( values );
            return this;
        }

        private Builder doAddValues( final Collection<String> values )
        {
            this.values.addAll( Collections2.transform( values, ValueFactory::newString ) );
            return this;
        }

        public ValueFilter build()
        {
            return new ValueFilter( this );
        }

    }
}
