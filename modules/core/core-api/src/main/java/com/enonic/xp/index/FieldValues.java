package com.enonic.xp.index;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static java.util.Objects.requireNonNull;

/**
 * The fields requested by a query, as returned for a single hit. Lookups accept any casing that an index path accepts. Every field is
 * held as a list, whether or not it is single-valued, and a field for which the hit holds no value is absent rather than empty.
 * <p>
 * Which fields may be requested is determined by the API being queried; each one publishes the set it supports.
 *
 * @param asMap every field held, keyed by its lowercase index-path form. Prefer the lookup methods, which accept any casing.
 * @since 8.1.0
 */
@NullMarked
public record FieldValues(Map<String, List<Object>> asMap)
{
    private static final FieldValues EMPTY = new FieldValues( ImmutableMap.of() );

    public FieldValues
    {
        requireNonNull( asMap, "asMap is required" );
        asMap = asMap.entrySet()
            .stream()
            .collect( ImmutableMap.toImmutableMap( entry -> IndexPath.from( entry.getKey() ).getPath(),
                                                   entry -> ImmutableList.copyOf( entry.getValue() ) ) );
    }

    /**
     * The instance holding no field at all, returned wherever a query requested none.
     */
    public static FieldValues empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Whether no requested field held a value for this hit.
     */
    public boolean isEmpty()
    {
        return asMap.isEmpty();
    }

    /**
     * The fields present for this hit, in lowercase index-path form.
     */
    public Set<String> getFields()
    {
        return asMap.keySet();
    }

    /**
     * All values of a field, in index order, or an empty list where the field is absent.
     */
    public List<Object> getValues( final IndexPath field )
    {
        return asMap.getOrDefault( field.getPath(), List.of() );
    }

    /**
     * All values of a field named as a string, in any casing an index path accepts.
     *
     * @see #getValues(IndexPath)
     */
    public List<Object> getValues( final String field )
    {
        return getValues( IndexPath.from( field ) );
    }

    /**
     * The first value of a field, or empty where the field is absent. Use it for a field known to be single-valued;
     * {@link #getValues(IndexPath)} answers for one that may hold several.
     */
    public Optional<Object> getSingleValue( final IndexPath field )
    {
        final List<Object> values = getValues( field );
        return values.isEmpty() ? Optional.empty() : Optional.of( values.get( 0 ) );
    }

    /**
     * The first value of a field named as a string, in any casing an index path accepts.
     *
     * @see #getSingleValue(IndexPath)
     */
    public Optional<Object> getSingleValue( final String field )
    {
        return getSingleValue( IndexPath.from( field ) );
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<String, List<Object>> values = ImmutableMap.builder();

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
            final Map<String, List<Object>> built = values.buildKeepingLast();
            return built.isEmpty() ? EMPTY : new FieldValues( built );
        }
    }
}
