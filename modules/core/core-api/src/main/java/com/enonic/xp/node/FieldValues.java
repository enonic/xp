package com.enonic.xp.node;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.index.IndexPath;

/**
 * Values of the index fields a query asked for, one instance per hit. Keyed by lowercase index path, and carrying what the search index
 * stores: every field is a list even when single-valued, and a field the index does not hold for the hit is simply absent.
 *
 * @see NodeQuery.Builder#returnFields(IndexPath...)
 * @since 8.1.0
 */
public final class FieldValues
{
    private static final FieldValues EMPTY = new FieldValues( ImmutableMap.of() );

    private final ImmutableMap<String, ImmutableList<Object>> values;

    private FieldValues( final ImmutableMap<String, ImmutableList<Object>> values )
    {
        this.values = values;
    }

    public static FieldValues empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean isEmpty()
    {
        return values.isEmpty();
    }

    /**
     * Index paths present for this hit, in lowercase index-path form.
     */
    public Set<String> getFields()
    {
        return values.keySet();
    }

    /**
     * All values of a field, or an empty list when the field is absent.
     */
    public List<Object> getValues( final IndexPath field )
    {
        return values.getOrDefault( field.getPath(), ImmutableList.of() );
    }

    public List<Object> getValues( final String field )
    {
        return getValues( IndexPath.from( field ) );
    }

    /**
     * The first value of a field, or empty when the field is absent.
     */
    public Optional<Object> getSingleValue( final IndexPath field )
    {
        final List<Object> list = getValues( field );
        return list.isEmpty() ? Optional.empty() : Optional.of( list.get( 0 ) );
    }

    public Optional<Object> getSingleValue( final String field )
    {
        return getSingleValue( IndexPath.from( field ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof FieldValues && values.equals( ( (FieldValues) o ).values );
    }

    @Override
    public int hashCode()
    {
        return values.hashCode();
    }

    @Override
    public String toString()
    {
        return values.toString();
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<String, ImmutableList<Object>> values = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final String field, final Iterable<?> fieldValues )
        {
            this.values.put( IndexPath.from( field ).getPath(), ImmutableList.copyOf( fieldValues ) );
            return this;
        }

        public FieldValues build()
        {
            final ImmutableMap<String, ImmutableList<Object>> built = values.buildKeepingLast();
            return built.isEmpty() ? EMPTY : new FieldValues( built );
        }
    }

    /**
     * Values keyed by index path.
     */
    public Map<String, ? extends List<Object>> asMap()
    {
        return values;
    }
}
