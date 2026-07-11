package com.enonic.xp.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * A single trace with attributes.
 * <p>
 * Attribute values are restricted to a closed model so traces can be exported losslessly to external tracing
 * systems: {@code String}, {@code Boolean}, {@code Long}, {@code Double} or {@code List<String>}. Convert values
 * eagerly when recording them - an attribute is an immutable snapshot taken at {@code put} time, never a live
 * object. Implementations normalize other values: integral numbers are widened to {@code Long}, floating-point
 * numbers to {@code Double}, iterables become immutable lists of strings, any other object is converted with
 * {@code String.valueOf}, and a {@code null} value removes the attribute.
 * <p>
 * Numeric attributes that may exceed 2^53 (identifiers, hashes, nanosecond timestamps) must be recorded as
 * strings: trace consumers include JavaScript environments where larger integers silently lose precision.
 * Pairwise-related values should be recorded as one list of composed elements (for example
 * {@code ["repo1:branch1", "repo2:branch2"]}) - attribute lists carry no index alignment across keys.
 * <p>
 * Attribute names should be constant and low-cardinality; dynamic data belongs in values. The attribute name
 * {@code "error"} is reserved by convention for a short failure description (typically the exception class name
 * or message): trace exporters map its presence to an error status.
 */
public interface Trace
    extends Map<String, Object>
{
    String getId();

    String getParentId();

    String getName();

    TraceLocation getLocation();

    Instant getStartTime();

    Instant getEndTime();

    boolean inProgress();

    Duration getDuration();

    void start();

    void end();
}
