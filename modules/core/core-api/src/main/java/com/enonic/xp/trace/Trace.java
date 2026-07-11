package com.enonic.xp.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A single trace with attributes.
 * <p>
 * Attributes are recorded with the typed {@code attribute} methods and are restricted to a closed value model so
 * traces can be exported losslessly to external tracing systems: {@code String}, {@code boolean}, {@code long},
 * {@code double} or {@code List<String>}. An attribute is an immutable snapshot taken when it is recorded, never
 * a live object. Attributes can be overwritten but not removed, and recording a {@code null} value is ignored.
 * <p>
 * Numeric attributes that may exceed 2^53 (identifiers, hashes, nanosecond timestamps) must be recorded as
 * strings: trace consumers include JavaScript environments where larger integers silently lose precision.
 * Pairwise-related values should be recorded as one list of composed elements (for example
 * {@code ["repo1:branch1", "repo2:branch2"]}) - attribute lists carry no index alignment across keys.
 * <p>
 * Attribute names should be constant and low-cardinality; dynamic data belongs in values. The attribute name
 * {@code "error"} is reserved by convention for a short failure description (typically the exception class name
 * or message): trace exporters map its presence to an error status.
 * <p>
 * The {@link Map} inheritance is deprecated and will be removed: the map mutators bypass the typed value model.
 * Implementations normalize values recorded through the deprecated {@code put}: integral numbers are widened to
 * {@code Long}, floating-point numbers to {@code Double}, iterables become immutable lists of strings, any other
 * object is converted with {@code String.valueOf}, and a {@code null} value removes the attribute. Reading
 * attributes through the map view remains supported until a typed read view replaces it.
 */
@NullMarked
public interface Trace
    extends Map<String, Object>
{
    String getId();

    @Nullable String getParentId();

    String getName();

    @Nullable TraceLocation getLocation();

    @Nullable Instant getStartTime();

    @Nullable Instant getEndTime();

    boolean inProgress();

    Duration getDuration();

    void start();

    void end();

    /**
     * Records a string attribute. Ignored when the value is {@code null}.
     *
     * @return this trace, for chaining
     */
    default Trace attribute( final String key, final @Nullable String value )
    {
        if ( value != null )
        {
            put( key, value );
        }
        return this;
    }

    /**
     * Records an integer attribute. Values that may exceed 2^53 must be recorded as strings instead.
     *
     * @return this trace, for chaining
     */
    default Trace attribute( final String key, final long value )
    {
        put( key, value );
        return this;
    }

    /**
     * Records a floating-point attribute.
     *
     * @return this trace, for chaining
     */
    default Trace attribute( final String key, final double value )
    {
        put( key, value );
        return this;
    }

    /**
     * Records a boolean attribute.
     *
     * @return this trace, for chaining
     */
    default Trace attribute( final String key, final boolean value )
    {
        put( key, value );
        return this;
    }

    /**
     * Records a list-of-strings attribute. Ignored when the value is {@code null}.
     *
     * @return this trace, for chaining
     */
    default Trace attribute( final String key, final @Nullable List<String> values )
    {
        if ( values != null )
        {
            put( key, values );
        }
        return this;
    }

    /**
     * @deprecated Record attributes with the typed {@code attribute} methods instead. Trace will stop extending
     * {@code Map}.
     */
    @Deprecated
    @Override
    @Nullable Object put( String key, @Nullable Object value );

    /**
     * @deprecated Record attributes with the typed {@code attribute} methods instead. Trace will stop extending
     * {@code Map}.
     */
    @Deprecated
    @Override
    void putAll( Map<? extends String, ?> map );

    /**
     * @deprecated Attributes cannot be removed - overwrite them instead. Trace will stop extending {@code Map}.
     */
    @Deprecated
    @Override
    @Nullable Object remove( Object key );

    /**
     * @deprecated Attributes cannot be removed - overwrite them instead. Trace will stop extending {@code Map}.
     */
    @Deprecated
    @Override
    void clear();
}
