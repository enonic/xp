package com.enonic.xp.sortvalues;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SortValuesProperty
{
    /**
     * Can be null or contain null values
     */
    private final List<Object> values;

    private SortValuesProperty( final Builder builder )
    {
        this.values =
            builder.values != null ? Collections.unmodifiableList( Arrays.stream( builder.values ).collect( Collectors.toList() ) ) : null;
    }

    public List<Object> getValues()
    {
        return values;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SortValuesProperty ) )
        {
            return false;
        }
        final SortValuesProperty that = (SortValuesProperty) o;
        return Objects.equals( values, that.values );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( values );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Object[] values;

        private Builder()
        {
        }

        public Builder values( Object... sortValues )
        {
            this.values = sortValues;
            return this;
        }

        public SortValuesProperty build()
        {
            return new SortValuesProperty( this );
        }
    }
}
