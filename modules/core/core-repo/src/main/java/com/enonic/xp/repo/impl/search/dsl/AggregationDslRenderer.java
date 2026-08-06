package com.enonic.xp.repo.impl.search.dsl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.aggregation.BucketAggregationQuery;
import com.enonic.xp.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.xp.query.aggregation.DateRange;
import com.enonic.xp.query.aggregation.DateRangeAggregationQuery;
import com.enonic.xp.query.aggregation.DistanceRange;
import com.enonic.xp.query.aggregation.GeoDistanceAggregationQuery;
import com.enonic.xp.query.aggregation.HistogramAggregationQuery;
import com.enonic.xp.query.aggregation.NumericRange;
import com.enonic.xp.query.aggregation.NumericRangeAggregationQuery;
import com.enonic.xp.query.aggregation.TermsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.query.aggregation.metric.StatsAggregationQuery;
import com.enonic.xp.query.aggregation.metric.ValueCountAggregationQuery;

/**
 * Renders {@link AggregationQueries} into their canonical wire form:
 * <pre>{ "&lt;name&gt;": { "&lt;type&gt;": { ..params.. }, "aggregations": { ..nested.. } } }</pre>
 * <p>
 * The shape follows the rest of this package: <b>one single-key object names the construct</b> (as
 * the constraint DSL does), field names are emitted post-{@link IndexPath} and WITHOUT a value-type
 * postfix, and every parameter carries XP's own spelling ({@code minDocCount}, not
 * {@code min_doc_count}). Resolving {@code population} to {@code population._number} and
 * {@code minDocCount} to {@code min_doc_count} is the backend's job, and the backend is the only
 * component that knows which physical layout it has.
 *
 * <h2>Only the parameters XP actually sets</h2>
 * The ten aggregation types below are exactly what {@code AggregationQueryBuilderFactory} builds,
 * with exactly the parameters its factories pass — no more. Where XP's builder has a non-null
 * default ({@code TermsAggregationQuery}'s {@code size 10}/{@code minDocCount 1}/{@code order},
 * every histogram's {@code minDocCount 1}) the value is always written, because the ES path always
 * wrote it too and a histogram's {@code minDocCount} default is NOT the engine's (XP says 1, the
 * engine says 0 — inheriting the engine's default would silently add every empty bucket). Where the
 * parameter is genuinely optional ({@code format}, {@code extendedBounds}, a histogram
 * {@code order}, a geo {@code unit}, a range {@code key}) it is absent when unset, so the engine's
 * own default applies on both sides.
 *
 * <h2>Sub-aggregations: an explicit per-type rule</h2>
 * The ES path gated nesting on {@code aggregationQuery instanceof BucketAggregationQuery &&
 * aggregationBuilder instanceof AbstractAggregationBuilder}-vs-{@code AggregationBuilder}. That
 * discriminator no longer exists, and its second half was never load-bearing: every bucket query
 * type maps to a builder that accepted sub-aggregations, and a metric query has no
 * {@code getSubQueries()} to read in the first place. So the rule is stated once, positively, here
 * and mirrored by the backend: <b>the six BUCKET types accept sub-aggregations; the four METRIC
 * types do not.</b> {@link #SUB_AGGREGATION_CAPABLE} is that list, and it is asserted rather than
 * implied — a future metric type that grows sub-queries fails loudly instead of having them
 * silently dropped, which is what the old guard did.
 *
 * <h2>Names are a total order and must be unique</h2>
 * Aggregations arrive in a set-shaped collection, and Gate 0(c) item 5 records set iteration order
 * as a wire-format hazard; the map is therefore keyed and SORTED by name. Two aggregations sharing
 * one name cannot be expressed on the wire (the name is the key, and it is also the key the
 * response comes back under), so a duplicate is a loud failure rather than a silent overwrite.
 *
 * <h2>…and so are RANGES, which is item 5 one level deeper — and it was a live bug</h2>
 * {@code AbstractRangeAggregationQuery} holds its ranges in an {@code ImmutableSet} copied from a
 * {@code HashSet}, and no {@code Range} subclass overrides {@code hashCode}. So range order is
 * IDENTITY-HASH order and varies from JVM run to JVM run. Measured against a real engine, that is
 * not merely a golden-file inconvenience:
 * <ul>
 * <li>{@code range} and {@code date_range} RE-SORT their buckets by the resolved {@code from} (a
 * reversed request, and even one with date-math bounds, comes back ascending), so the wire order is
 * invisible in the result there;</li>
 * <li>{@code geo_distance} does NOT — it returns buckets in the order the request listed them.</li>
 * </ul>
 * Which means a geo-distance aggregation returned its buckets in a DIFFERENT ORDER on different
 * runs, with the same keys and counts. Corpus row {@code AGG-13} caught it on a repeat run: 0
 * failures once, then {@code 1000.0-*} first. Ranges are therefore sorted here into a total order —
 * numerically by {@code from} then {@code to} when the bounds are numeric (always the case for
 * numeric and geo ranges), lexicographically otherwise (a date bound may be date math like
 * {@code now-5h}, which only the engine can evaluate). For geo distance that order IS the bucket
 * order; for the other two it only has to be stable.
 */
final class AggregationDslRenderer
{
    /** The bucket types, i.e. exactly those that may carry an {@code aggregations} member. */
    private static final Set<String> SUB_AGGREGATION_CAPABLE =
        Set.of( "terms", "numericRange", "dateRange", "geoDistance", "histogram", "dateHistogram" );

    private AggregationDslRenderer()
    {
    }

    /**
     * @return the canonical config keyed by aggregation name, or {@code null} when none was requested
     */
    static Map<String, Object> render( final AggregationQueries queries )
    {
        if ( queries == null || queries.isEmpty() )
        {
            return null;
        }

        final Map<String, Object> sorted = new TreeMap<>();
        for ( final AggregationQuery query : queries )
        {
            final String name = query.getName();
            if ( name == null || name.isBlank() )
            {
                throw new DslRenderException( "An aggregation needs a name" );
            }
            if ( sorted.put( name, renderOne( query ) ) != null )
            {
                throw new DslRenderException( "Two aggregations are named '" + name +
                                                  "'; an aggregation name is the wire key and the response key, so it must be unique" );
            }
        }
        return new LinkedHashMap<>( sorted );
    }

    private static Map<String, Object> renderOne( final AggregationQuery query )
    {
        final Map<String, Object> rendered = new LinkedHashMap<>();
        final String type = type( query );
        rendered.put( type, params( query ) );

        if ( query instanceof BucketAggregationQuery && !( (BucketAggregationQuery) query ).getSubQueries().isEmpty() )
        {
            if ( !SUB_AGGREGATION_CAPABLE.contains( type ) )
            {
                throw new DslRenderException( "Aggregation type '" + type + "' cannot carry sub-aggregations" );
            }
            rendered.put( "aggregations", render( ( (BucketAggregationQuery) query ).getSubQueries() ) );
        }
        return rendered;
    }

    private static String type( final AggregationQuery query )
    {
        if ( query instanceof TermsAggregationQuery )
        {
            return "terms";
        }
        if ( query instanceof NumericRangeAggregationQuery )
        {
            return "numericRange";
        }
        if ( query instanceof DateRangeAggregationQuery )
        {
            return "dateRange";
        }
        if ( query instanceof GeoDistanceAggregationQuery )
        {
            return "geoDistance";
        }
        if ( query instanceof DateHistogramAggregationQuery )
        {
            return "dateHistogram";
        }
        if ( query instanceof HistogramAggregationQuery )
        {
            return "histogram";
        }
        if ( query instanceof StatsAggregationQuery )
        {
            return "stats";
        }
        if ( query instanceof MinAggregationQuery )
        {
            return "min";
        }
        if ( query instanceof MaxAggregationQuery )
        {
            return "max";
        }
        if ( query instanceof ValueCountAggregationQuery )
        {
            return "valueCount";
        }
        // Same wording and the same reason as the ES factory's "Unexpected aggregation type": a
        // construct with no wire form is never half-rendered.
        throw new DslRenderException( "Unexpected aggregation type: " + query.getClass().getName() );
    }

    private static Map<String, Object> params( final AggregationQuery query )
    {
        if ( query instanceof TermsAggregationQuery )
        {
            return terms( (TermsAggregationQuery) query );
        }
        if ( query instanceof NumericRangeAggregationQuery )
        {
            return numericRange( (NumericRangeAggregationQuery) query );
        }
        if ( query instanceof DateRangeAggregationQuery )
        {
            return dateRange( (DateRangeAggregationQuery) query );
        }
        if ( query instanceof GeoDistanceAggregationQuery )
        {
            return geoDistance( (GeoDistanceAggregationQuery) query );
        }
        if ( query instanceof DateHistogramAggregationQuery )
        {
            return dateHistogram( (DateHistogramAggregationQuery) query );
        }
        if ( query instanceof HistogramAggregationQuery )
        {
            return histogram( (HistogramAggregationQuery) query );
        }
        if ( query instanceof StatsAggregationQuery )
        {
            return metric( ( (StatsAggregationQuery) query ).getFieldName() );
        }
        if ( query instanceof MinAggregationQuery )
        {
            return metric( ( (MinAggregationQuery) query ).getFieldName() );
        }
        if ( query instanceof MaxAggregationQuery )
        {
            return metric( ( (MaxAggregationQuery) query ).getFieldName() );
        }
        return metric( ( (ValueCountAggregationQuery) query ).getFieldName() );
    }

    /**
     * {@code order} is one object rather than two flat keys because the two halves are meaningless
     * apart, and it carries XP's own enum names: the mapping to the engine's {@code {"_key":"asc"}}
     * / {@code {"_count":"desc"}} — renamed from ES 2.4's {@code _term} — is the backend's.
     */
    private static Map<String, Object> terms( final TermsAggregationQuery query )
    {
        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );
        params.put( "size", query.getSize() );
        params.put( "minDocCount", query.getMinDocCount() );

        final Map<String, Object> order = new LinkedHashMap<>();
        order.put( "type", query.getOrderType().name() );
        order.put( "direction", query.getOrderDirection().name() );
        params.put( "order", order );
        return params;
    }

    private static Map<String, Object> numericRange( final NumericRangeAggregationQuery query )
    {
        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );

        final List<Map<String, Object>> ranges = new ArrayList<>();
        for ( final NumericRange range : query.getRanges() )
        {
            ranges.add( range( range.getKey(), range.getFrom(), range.getTo() ) );
        }
        params.put( "ranges", sorted( ranges ) );
        return params;
    }

    /**
     * A {@code DateRange} bound is an untyped {@code Object} that is either an {@link Instant} or a
     * raw string, because XP's builder offers both and the string form is ES <b>date math</b>
     * ({@code now-5h}, {@code 2014-12-10T10:00:00Z||-3h}) which the engine, not XP, evaluates. Both
     * ride as strings: {@code Instant.toString()} is exactly what ES 2.4 put on the wire for the
     * typed form (its {@code XContentBuilder} has no {@code Instant} case and falls through to
     * {@code toString()}), so the two forms were already indistinguishable one layer down.
     */
    private static Map<String, Object> dateRange( final DateRangeAggregationQuery query )
    {
        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );
        if ( query.getFormat() != null && !query.getFormat().isEmpty() )
        {
            params.put( "format", query.getFormat() );
        }

        final List<Map<String, Object>> ranges = new ArrayList<>();
        for ( final DateRange range : query.getRanges() )
        {
            ranges.add( range( range.getKey(), dateBound( range.getFrom() ), dateBound( range.getTo() ) ) );
        }
        params.put( "ranges", sorted( ranges ) );
        return params;
    }

    private static Map<String, Object> geoDistance( final GeoDistanceAggregationQuery query )
    {
        if ( query.getOrigin() == null )
        {
            throw new DslRenderException( "A geoDistance aggregation needs an origin" );
        }

        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );

        final Map<String, Object> origin = new LinkedHashMap<>();
        origin.put( "lat", query.getOrigin().getLatitude() );
        origin.put( "lon", query.getOrigin().getLongitude() );
        params.put( "origin", origin );

        if ( query.getUnit() != null && !query.getUnit().isEmpty() )
        {
            params.put( "unit", query.getUnit() );
        }

        final List<Map<String, Object>> ranges = new ArrayList<>();
        for ( final DistanceRange range : query.getRanges() )
        {
            ranges.add( range( range.getKey(), range.getFrom(), range.getTo() ) );
        }
        params.put( "ranges", sorted( ranges ) );
        return params;
    }

    /**
     * The numeric histogram's interval is a {@code Long} and is a plain multiple of the field's
     * unit — none of the {@code calendar_interval}-vs-{@code fixed_interval} ambiguity the DATE
     * histogram has, which is why the two types are separate wire constructs rather than one with a
     * polymorphic interval.
     */
    private static Map<String, Object> histogram( final HistogramAggregationQuery query )
    {
        if ( query.getInterval() == null )
        {
            throw new DslRenderException( "A histogram aggregation needs an interval" );
        }

        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );
        params.put( "interval", query.getInterval() );
        params.put( "minDocCount", query.getMinDocCount() );

        if ( query.setExtendedBounds() )
        {
            final Map<String, Object> bounds = new LinkedHashMap<>();
            bounds.put( "min", query.getExtendedBoundMin() );
            bounds.put( "max", query.getExtendedBoundMax() );
            params.put( "extendedBounds", bounds );
        }
        if ( query.getOrder() != null )
        {
            params.put( "order", query.getOrder().name() );
        }
        return params;
    }

    /**
     * The date histogram's interval is an UNTYPED STRING ({@code 1M}, {@code 1d}, {@code 90m},
     * {@code week}) and is passed through verbatim. Deciding whether it is a CALENDAR or a FIXED
     * interval is deliberately not done here: {@code DateHistogramInterval} and {@code .interval()}
     * are gone from the engine's API, so the classification is a property of the target engine and
     * belongs with the backend that talks to it, exactly like a field postfix.
     */
    private static Map<String, Object> dateHistogram( final DateHistogramAggregationQuery query )
    {
        if ( query.getInterval() == null || query.getInterval().isBlank() )
        {
            throw new DslRenderException( "A dateHistogram aggregation needs an interval" );
        }

        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( query.getFieldName() ) );
        params.put( "interval", query.getInterval() );
        params.put( "minDocCount", query.getMinDocCount() );
        if ( query.getFormat() != null && !query.getFormat().isEmpty() )
        {
            params.put( "format", query.getFormat() );
        }
        return params;
    }

    private static Map<String, Object> metric( final String fieldName )
    {
        final Map<String, Object> params = new LinkedHashMap<>();
        params.put( "field", field( fieldName ) );
        return params;
    }

    /**
     * The ranges of one aggregation, in a deterministic total order — see the class javadoc for why
     * that is a correctness requirement and not tidiness.
     * <p>
     * {@code from} absent sorts FIRST and {@code to} absent sorts LAST, because those are the open
     * ends: {@code -∞} and {@code +∞}. {@code key} breaks a remaining tie so the order is total even
     * for two ranges with identical bounds.
     */
    private static List<Object> sorted( final List<Map<String, Object>> ranges )
    {
        final List<Map<String, Object>> ordered = new ArrayList<>( ranges );
        ordered.sort( ( first, second ) -> {
            int result = compareBound( first.get( "from" ), second.get( "from" ), -1 );
            if ( result != 0 )
            {
                return result;
            }
            result = compareBound( first.get( "to" ), second.get( "to" ), 1 );
            return result != 0 ? result : compareBound( first.get( "key" ), second.get( "key" ), -1 );
        } );
        return List.copyOf( ordered );
    }

    /**
     * @param absentFirst {@code -1} if an absent value sorts before a present one, {@code 1} if after
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int compareBound( final Object first, final Object second, final int absentFirst )
    {
        if ( first == null || second == null )
        {
            return first == second ? 0 : ( first == null ? absentFirst : -absentFirst );
        }
        if ( first instanceof Number && second instanceof Number )
        {
            return Double.compare( ( (Number) first ).doubleValue(), ( (Number) second ).doubleValue() );
        }
        if ( first.getClass() == second.getClass() && first instanceof Comparable )
        {
            return ( (Comparable) first ).compareTo( second );
        }
        return String.valueOf( first ).compareTo( String.valueOf( second ) );
    }

    /** An absent {@code key} is absent, not empty: the engine generates one from the bounds. */
    private static Map<String, Object> range( final String key, final Object from, final Object to )
    {
        final Map<String, Object> range = new LinkedHashMap<>();
        if ( key != null && !key.isEmpty() )
        {
            range.put( "key", key );
        }
        if ( from != null )
        {
            range.put( "from", from );
        }
        if ( to != null )
        {
            range.put( "to", to );
        }
        return range;
    }

    private static Object dateBound( final Object value )
    {
        return value == null ? null : value.toString();
    }

    private static String field( final String fieldName )
    {
        if ( fieldName == null || fieldName.isBlank() )
        {
            throw new DslRenderException( "An aggregation needs a fieldName" );
        }
        return IndexPath.from( fieldName ).getPath();
    }
}
