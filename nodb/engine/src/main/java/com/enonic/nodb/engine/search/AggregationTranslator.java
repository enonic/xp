package com.enonic.nodb.engine.search;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Aggregations, both directions: XP's canonical aggregation config → an OpenSearch {@code aggs}
 * block, and the response's {@code aggregations} section → a <b>TAGGED</b> document the client turns
 * back into XP's {@code Aggregations} object graph.
 *
 * <h2>Why the response has to be tagged (D9)</h2>
 * XP's {@code AggregationsFactory} discriminates results by {@code instanceof} on Elasticsearch
 * INTERNAL classes, and one of those tests is load-bearing in a way a JSON wire cannot reproduce:
 * a date histogram is told apart from a numeric one ONLY by {@code instanceof InternalHistogram}
 * appearing before {@code instanceof Histogram} in the chain, and a date RANGE from a numeric one
 * only by {@code InternalDateRange} preceding {@code Range}. <b>An OpenSearch response carries no
 * such marker.</b> A {@code terms}, a {@code histogram}, a {@code date_histogram} and a
 * {@code range} all come back as {@code {"buckets": [...]}}, and the bucket key of a date histogram
 * is an ordinary JSON number. Guessing from shape — "does the key look like millis?" — is exactly
 * the silent-wrong-answer failure this port keeps finding.
 * <p>
 * So the kind is not inferred at all, in either direction: <b>it is copied from the REQUEST</b>,
 * which this server has in hand, and written onto the response as an explicit {@code type} plus an
 * explicit {@code keyType}. {@link #decode} therefore walks the canonical request and the engine
 * response TOGETHER, pairing them by aggregation name and recursing through sub-aggregations in
 * lockstep. Three consequences worth stating:
 * <ol>
 * <li>the client's decoder is a table lookup on a tag, with no type identity anywhere in it;</li>
 * <li>an aggregation the request asked for and the response does not contain is a loud failure, not
 * a silently missing entry — the case that would otherwise look like "aggregations came back but
 * threw";</li>
 * <li>the response order is the REQUEST's order (which the renderer sorted by name), so it is
 * deterministic independently of how the engine happens to serialize its own map.</li>
 * </ol>
 *
 * <h2>Sub-aggregations: the explicit per-type rule</h2>
 * ES 2.4 gated nesting on {@code aggregationBuilder instanceof AggregationBuilder} (as opposed to
 * the base {@code AbstractAggregationBuilder}), a distinction that no longer exists. It is replaced
 * by {@link #SUB_AGGREGATION_CAPABLE}: the six BUCKET types accept sub-aggregations, the four METRIC
 * types do not. A sub-aggregation under a metric type is REJECTED rather than dropped — note that
 * the old guard dropped it silently, and that XP itself cannot express it (only
 * {@code BucketAggregationQuery} has {@code getSubQueries}), so rejecting costs nothing and closes
 * the hole for any hand-built envelope.
 *
 * <h2>The interval rule (documented, because the engine's API no longer decides it)</h2>
 * See {@link #dateHistogramInterval}.
 */
final class AggregationTranslator
{
    /** Canonical (XP) aggregation type → the engine's own name for it. */
    private static final Map<String, String> ENGINE_TYPES =
        Map.ofEntries( Map.entry( "terms", "terms" ), Map.entry( "stats", "stats" ), Map.entry( "min", "min" ),
                       Map.entry( "max", "max" ), Map.entry( "valueCount", "value_count" ), Map.entry( "numericRange", "range" ),
                       Map.entry( "dateRange", "date_range" ), Map.entry( "geoDistance", "geo_distance" ),
                       Map.entry( "histogram", "histogram" ), Map.entry( "dateHistogram", "date_histogram" ) );

    /** The bucket types. Exactly these six may carry an {@code aggregations} member. */
    private static final Set<String> SUB_AGGREGATION_CAPABLE =
        Set.of( "terms", "numericRange", "dateRange", "geoDistance", "histogram", "dateHistogram" );

    /** The bucket types, in the response direction: exactly these decode to {@code buckets}. */
    private static final Set<String> BUCKET_TYPES = SUB_AGGREGATION_CAPABLE;

    /**
     * Every interval string ES 2.4's {@code DateHistogramParser.DATE_FIELD_UNITS} recognised, which
     * is precisely the set it rounded by CALENDAR FIELD rather than by a fixed duration — and, not
     * by coincidence, precisely the set OpenSearch's {@code calendar_interval} accepts. See
     * {@link #dateHistogramInterval}.
     *
     * <p><b>Case is significant and must not be normalised</b>: {@code 1M} is a month and
     * {@code 1m} is a minute. Lower-casing this input would silently turn every monthly histogram
     * into a per-minute one.
     */
    private static final Set<String> CALENDAR_INTERVALS =
        Set.of( "year", "1y", "quarter", "1q", "month", "1M", "week", "1w", "day", "1d", "hour", "1h", "minute", "1m", "second", "1s" );

    /** A fixed interval is {@code <positive integer><ms|s|m|h|d>} — the engine's own grammar. */
    private static final java.util.regex.Pattern FIXED_INTERVAL = java.util.regex.Pattern.compile( "^[0-9]+(ms|s|m|h|d)$" );

    /**
     * The date format used for a date aggregation's bucket keys when XP asks for none.
     *
     * <p>It cannot be left unset. The {@code *._datetime} mapping declares
     * {@code epoch_millis||strict_date_optional_time}, and a range/histogram aggregation formats its
     * bucket keys with the field's FIRST pattern — so an unset format would key every date bucket by
     * epoch millis ({@code *-1609459200000}) where ES 2.4 keyed it {@code *-2021-01-01T00:00:00.000Z}
     * (its default date formatter was {@code dateOptionalTime}, this pattern's ancestor).
     *
     * <p>{@code strict_date_optional_time} rather than {@code strict_date_time}, and the difference
     * matters twice over: this format is also the PARSER for a {@code date_range} bound, and XP's own
     * itests pass both {@code 2014-12-10T10:00:00Z||-5h} (no millis) and
     * {@code 2014-12-10T10:00:00.000Z||-3h} (with millis) plus {@code now-5h}. The strict form
     * demands millis and would reject the first; the optional-time form parses all three and still
     * PRINTS the millis, which is what the recorded keys need.
     */
    private static final String DEFAULT_DATE_FORMAT = "strict_date_optional_time";

    /** XP's {@code TermsAggregationQuery.TERM_DEFAULT_SIZE}, for an envelope that omits it. */
    private static final int DEFAULT_TERMS_SIZE = 10;

    /** XP's builder default on terms and on both histograms — NOT the engine's, which is 0. */
    private static final long DEFAULT_MIN_DOC_COUNT = 1;

    private AggregationTranslator()
    {
    }

    // --- request ---------------------------------------------------------------------

    /**
     * @param canonical canonical config: {@code {"<name>": {"<type>": {..}, "aggregations": {..}}}}
     * @return the {@code aggs} block, or {@code null} when nothing was requested
     */
    static ObjectNode translate( JsonNode canonical )
    {
        if ( canonical == null || !canonical.isObject() || canonical.isEmpty() )
        {
            return null;
        }

        ObjectNode aggs = object();
        canonical.properties().forEach( entry -> aggs.set( entry.getKey(), aggregation( entry.getKey(), entry.getValue() ) ) );
        return aggs;
    }

    private static ObjectNode aggregation( String name, JsonNode config )
    {
        String type = type( name, config );
        JsonNode params = config.get( type );

        ObjectNode body = switch ( type )
        {
            case "terms" -> terms( name, params );
            case "stats", "min", "max" -> metric( name, params, "._number" );
            case "valueCount" -> metric( name, params, "" );
            case "numericRange" -> numericRange( name, params );
            case "dateRange" -> dateRange( name, params );
            case "geoDistance" -> geoDistance( name, params );
            case "histogram" -> histogram( name, params );
            case "dateHistogram" -> dateHistogram( name, params );
            default -> throw unsupported( "Aggregation type '" + type + "' is not supported" );
        };

        ObjectNode out = object();
        out.set( ENGINE_TYPES.get( type ), body );

        JsonNode subs = config.get( "aggregations" );
        if ( subs != null && !subs.isNull() && !subs.isEmpty() )
        {
            if ( !SUB_AGGREGATION_CAPABLE.contains( type ) )
            {
                throw unsupported( "Aggregation '" + name + "' is a '" + type +
                                       "' metric aggregation and cannot carry sub-aggregations; only bucket aggregations can (" +
                                       SUB_AGGREGATION_CAPABLE + ")" );
            }
            out.set( "aggs", translate( subs ) );
        }
        return out;
    }

    /**
     * The single type key in an aggregation config. {@code aggregations} is the only other member
     * allowed, so a misspelled type is a loud failure instead of an aggregation with no body.
     */
    private static String type( String name, JsonNode config )
    {
        if ( config == null || !config.isObject() )
        {
            throw unsupported( "Aggregation '" + name + "' must be a JSON object naming one aggregation type" );
        }

        String found = null;
        var names = config.fieldNames();
        while ( names.hasNext() )
        {
            String member = names.next();
            if ( "aggregations".equals( member ) )
            {
                continue;
            }
            if ( !ENGINE_TYPES.containsKey( member ) )
            {
                throw unsupported( "Aggregation '" + name + "' names an unsupported type '" + member + "' (supported: " +
                                       new java.util.TreeSet<>( ENGINE_TYPES.keySet() ) + ")" );
            }
            if ( found != null )
            {
                throw unsupported( "Aggregation '" + name + "' names two types, '" + found + "' and '" + member + "'" );
            }
            found = member;
        }
        if ( found == null )
        {
            throw unsupported( "Aggregation '" + name + "' names no aggregation type" );
        }
        return found;
    }

    /**
     * {@code order} is {@code {"_key": "asc"}} / {@code {"_count": "desc"}} — ES 2.4's
     * {@code Terms.Order.term(..)} spelled {@code _term} on the wire, and OpenSearch renamed it to
     * {@code _key}. Both halves are always present because XP's builder always sets them.
     *
     * <p>{@code size <= 0} becomes {@code Integer.MAX_VALUE}: in ES 2.4 a terms size of 0 meant
     * "unlimited", while OpenSearch rejects it outright. Nothing in XP passes 0 today (the builder
     * default is 10), so this is bug-for-bug parity for a value only a script can produce, kept
     * because the alternative — falling through to the engine's own default of 10 — would silently
     * TRUNCATE a caller who explicitly asked for everything.
     */
    private static ObjectNode terms( String name, JsonNode params )
    {
        ObjectNode body = object();
        body.put( "field", field( name, params, "" ) );

        int size = intOr( params, "size", DEFAULT_TERMS_SIZE );
        body.put( "size", size <= 0 ? Integer.MAX_VALUE : size );
        body.put( "min_doc_count", longOr( params, "minDocCount", DEFAULT_MIN_DOC_COUNT ) );

        JsonNode order = params.get( "order" );
        String orderType = text( order, "type" );
        String direction = text( order, "direction" );
        if ( orderType == null || direction == null )
        {
            throw unsupported( "Terms aggregation '" + name + "' needs an order with a 'type' and a 'direction'" );
        }
        body.set( "order", object().put( termsOrderKey( name, orderType ), sortDirection( name, direction ) ) );
        return body;
    }

    private static String termsOrderKey( String name, String orderType )
    {
        return switch ( orderType )
        {
            case "TERM" -> "_key";
            case "DOC_COUNT" -> "_count";
            default -> throw unsupported( "Terms aggregation '" + name + "' has an unknown order type '" + orderType + "'" );
        };
    }

    private static String sortDirection( String name, String direction )
    {
        return switch ( direction )
        {
            case "ASC" -> "asc";
            case "DESC" -> "desc";
            default -> throw unsupported( "Aggregation '" + name + "' has an unknown order direction '" + direction + "'" );
        };
    }

    private static ObjectNode metric( String name, JsonNode params, String postfix )
    {
        ObjectNode body = object();
        body.put( "field", field( name, params, postfix ) );
        return body;
    }

    private static ObjectNode numericRange( String name, JsonNode params )
    {
        ObjectNode body = object();
        body.put( "field", field( name, params, "._number" ) );
        body.set( "ranges", ranges( name, params, false ) );
        return body;
    }

    private static ObjectNode dateRange( String name, JsonNode params )
    {
        ObjectNode body = object();
        body.put( "field", field( name, params, "._datetime" ) );
        body.put( "format", dateFormat( params ) );
        body.set( "ranges", ranges( name, params, true ) );
        return body;
    }

    private static ObjectNode geoDistance( String name, JsonNode params )
    {
        JsonNode origin = params.get( "origin" );
        if ( origin == null || origin.get( "lat" ) == null || origin.get( "lon" ) == null )
        {
            throw unsupported( "Geo-distance aggregation '" + name + "' needs an 'origin' with 'lat' and 'lon'" );
        }

        ObjectNode body = object();
        body.put( "field", field( name, params, "._geopoint" ) );
        body.set( "origin", object().put( "lat", origin.get( "lat" ).doubleValue() ).put( "lon", origin.get( "lon" ).doubleValue() ) );

        String unit = text( params, "unit" );
        if ( unit != null && !unit.isBlank() )
        {
            body.put( "unit", unit );
        }
        // distance_type is NOT set, exactly as XP never set it. Note that the engine default moved
        // from ES 2.4's `sloppy_arc` to `arc`, i.e. distances are now exact rather than approximate
        // (~0.13% apart) -- the same documented delta the geo-distance SORT carries, and it cannot be
        // pinned back because `sloppy_arc` no longer exists.
        body.set( "ranges", ascending( ranges( name, params, false ) ) );
        return body;
    }

    /**
     * Sorts a geo-distance range list by {@code from} — <b>a correctness requirement, not tidiness</b>,
     * and the second layer of a fix whose first layer is XP's renderer emitting them sorted.
     *
     * <p>Measured against a real engine: {@code range} and {@code date_range} sort their own range
     * array (a reversed request comes back ascending and correctly counted), but {@code geo_distance}
     * does NOT — and the shared {@code RangeAggregator} binary-searches that array assuming it is
     * ordered. So an unsorted geo-distance range list does not merely return its buckets in an odd
     * order, it <b>SILENTLY LOSES DOCUMENTS</b>: {@code [{from:1000},{to:1000}]} over two documents at
     * 0 km and 5900 km answers {@code 1000.0-*: 1} and {@code *-1000.0: 0}, dropping the document at
     * the origin. No error, a plausible number.
     *
     * <p>It is sorted HERE as well as in the renderer because the two guard different things: the
     * renderer guards wire determinism (XP's ranges live in a {@code HashSet}, so identity-hash order
     * would otherwise reach the wire), while this guards the ENGINE contract for any envelope at all,
     * including a hand-built one. Only numeric ranges can be ordered this way, which is exactly the
     * case that needs it: a geo bound is always a distance.
     */
    private static ArrayNode ascending( ArrayNode ranges )
    {
        java.util.List<JsonNode> sorted = new java.util.ArrayList<>( ranges.size() );
        ranges.forEach( sorted::add );
        sorted.sort( java.util.Comparator.comparingDouble( ( JsonNode range ) -> bound( range, "from", Double.NEGATIVE_INFINITY ) )
                         .thenComparingDouble( range -> bound( range, "to", Double.POSITIVE_INFINITY ) ) );

        ArrayNode out = OpenSearchClient.mapper().createArrayNode();
        sorted.forEach( out::add );
        return out;
    }

    private static double bound( JsonNode range, String member, double open )
    {
        JsonNode value = range.get( member );
        return value == null || !value.isNumber() ? open : value.doubleValue();
    }

    private static ObjectNode histogram( String name, JsonNode params )
    {
        JsonNode interval = params.get( "interval" );
        if ( interval == null || !interval.isNumber() )
        {
            throw unsupported( "Histogram aggregation '" + name + "' needs a numeric 'interval'" );
        }

        ObjectNode body = object();
        body.put( "field", field( name, params, "._number" ) );
        body.set( "interval", interval );
        body.put( "min_doc_count", longOr( params, "minDocCount", DEFAULT_MIN_DOC_COUNT ) );

        JsonNode bounds = params.get( "extendedBounds" );
        if ( bounds != null && !bounds.isNull() )
        {
            if ( bounds.get( "min" ) == null || bounds.get( "max" ) == null )
            {
                throw unsupported( "Histogram aggregation '" + name + "' has extendedBounds without both a 'min' and a 'max'" );
            }
            // `extended_bounds`, not ES 2.4's `extendedBounds`: DoubleBounds/LongBounds replaced the
            // builder call, but the wire name changed too.
            ObjectNode extended = object();
            extended.set( "min", bounds.get( "min" ) );
            extended.set( "max", bounds.get( "max" ) );
            body.set( "extended_bounds", extended );
        }

        String order = text( params, "order" );
        if ( order != null )
        {
            body.set( "order", histogramOrder( name, order ) );
        }
        return body;
    }

    /** {@code Histogram.Order.KEY_ASC} and friends, as the engine's {@code BucketOrder} JSON. */
    private static ObjectNode histogramOrder( String name, String order )
    {
        return switch ( order )
        {
            case "KEY_ASC" -> object().put( "_key", "asc" );
            case "KEY_DESC" -> object().put( "_key", "desc" );
            case "COUNT_ASC" -> object().put( "_count", "asc" );
            case "COUNT_DESC" -> object().put( "_count", "desc" );
            default -> throw unsupported( "Histogram aggregation '" + name + "' has an unknown order '" + order + "'" );
        };
    }

    private static ObjectNode dateHistogram( String name, JsonNode params )
    {
        String interval = text( params, "interval" );
        if ( interval == null || interval.isBlank() )
        {
            throw unsupported( "Date-histogram aggregation '" + name + "' needs an 'interval'" );
        }

        ObjectNode body = object();
        body.put( "field", field( name, params, "._datetime" ) );
        body.put( dateHistogramInterval( name, interval ), interval );
        body.put( "min_doc_count", longOr( params, "minDocCount", DEFAULT_MIN_DOC_COUNT ) );
        body.put( "format", dateFormat( params ) );
        return body;
    }

    /**
     * <b>THE INTERVAL RULE.</b> Returns {@code "calendar_interval"} or {@code "fixed_interval"} for
     * an untyped XP interval string.
     *
     * <p>XP's date-histogram interval is a bare {@code String} because ES 2.4 accepted one: the
     * builder took {@code DateHistogramInterval}, a value class wrapping a string, and
     * {@code .interval()} accepted any of {@code 1M}, {@code week}, {@code 90m}, {@code 30s}. Both
     * the class and the method are gone; OpenSearch demands that the CALLER say which kind of
     * interval it is, because the two are not the same operation — a calendar interval rounds by
     * calendar field (a month is 28–31 days, a day is 23–25 hours across a DST boundary) while a
     * fixed interval divides a fixed number of milliseconds.
     *
     * <p><b>The rule: an interval is a CALENDAR interval exactly when it is one of the sixteen
     * strings ES 2.4's {@code DATE_FIELD_UNITS} table contained</b> — {@code year 1y quarter 1q
     * month 1M week 1w day 1d hour 1h minute 1m second 1s} — and a FIXED interval otherwise.
     * Everything else ({@code 90m}, {@code 30s}, {@code 12h}, {@code 2d}, {@code 500ms}) is fixed
     * and must match {@link #FIXED_INTERVAL} or it is rejected rather than sent for the engine to
     * reinterpret.
     *
     * <p>That table is not a guess about intent, it is <b>the classification ES 2.4 itself made</b>:
     * {@code DateHistogramParser} looked the interval up in exactly that map and used calendar-field
     * rounding on a hit, a {@code TimeValue}-derived fixed rounding on a miss. So this rule is not
     * new behaviour designed against OpenSearch's API — it reproduces the behaviour the corpus
     * baseline was recorded with. It also happens to be forced in both directions:
     * {@code calendar_interval} accepts only these single-unit forms, and {@code fixed_interval}
     * accepts no {@code w}/{@code M}/{@code q}/{@code y} unit at all, so a week or a month could not
     * be expressed as fixed even if one wanted to.
     *
     * <p><b>The ambiguous cases, and why they resolve this way.</b> {@code 1d}, {@code 1h},
     * {@code 1m}, {@code 1s} are accepted by BOTH parameters in OpenSearch, and they are not
     * equivalent: with a non-UTC time zone {@code calendar_interval: 1d} produces 23- and 25-hour
     * days across DST while {@code fixed_interval: 24h} does not. {@code 1d} is the one the corpus
     * exercises (row {@code AGG-12}), and it is resolved to CALENDAR by the table above — i.e. by
     * what ES 2.4 did with it, which is what the baseline records. (Note the corpus row's own intent
     * text calls {@code 1d} "a FIXED interval in OpenSearch terms"; that describes what OpenSearch
     * WOULD accept, not what ES 2.4 computed, and the recorded buckets are the authority.) The rule
     * is deliberately total and table-driven so this is a decision written down once, rather than
     * one an engine default makes silently.
     *
     * <p>Case is significant: {@code 1M} is a month, {@code 1m} is a minute, and both are in the
     * table. The input is never case-normalised anywhere on this path.
     */
    private static String dateHistogramInterval( String name, String interval )
    {
        String value = interval.trim();
        if ( CALENDAR_INTERVALS.contains( value ) )
        {
            return "calendar_interval";
        }
        if ( FIXED_INTERVAL.matcher( value ).matches() )
        {
            return "fixed_interval";
        }
        throw unsupported( "Date-histogram aggregation '" + name + "' has interval '" + interval +
                               "', which is neither a calendar interval (" + new java.util.TreeSet<>( CALENDAR_INTERVALS ) +
                               ") nor a fixed interval (<number><ms|s|m|h|d>)" );
    }

    private static ArrayNode ranges( String name, JsonNode params, boolean dated )
    {
        JsonNode ranges = params.get( "ranges" );
        if ( ranges == null || !ranges.isArray() || ranges.isEmpty() )
        {
            throw unsupported( "Range aggregation '" + name + "' needs at least one range" );
        }

        ArrayNode out = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode range : ranges )
        {
            ObjectNode body = object();
            String key = text( range, "key" );
            if ( key != null && !key.isEmpty() )
            {
                body.put( "key", key );
            }
            // An absent bound is an OPEN bound and must stay absent: `"from": null` is not the same
            // request. A date bound rides as a string because it may be ES date math (`now-5h`), which
            // the engine evaluates; a numeric bound rides as a number.
            for ( String bound : java.util.List.of( "from", "to" ) )
            {
                JsonNode value = range.get( bound );
                if ( value == null || value.isNull() )
                {
                    continue;
                }
                if ( dated )
                {
                    body.set( bound, value );
                }
                else if ( value.isNumber() )
                {
                    body.put( bound, value.doubleValue() );
                }
                else
                {
                    throw unsupported( "Range aggregation '" + name + "' has a non-numeric '" + bound + "' bound" );
                }
            }
            if ( body.isEmpty() || ( body.size() == 1 && body.has( "key" ) ) )
            {
                throw unsupported( "Range aggregation '" + name + "' has a range with neither a 'from' nor a 'to'" );
            }
            out.add( body );
        }
        return out;
    }

    private static String dateFormat( JsonNode params )
    {
        String format = text( params, "format" );
        return format == null || format.isBlank() ? DEFAULT_DATE_FORMAT : format;
    }

    /**
     * The physical field name. The value type comes from the AGGREGATION TYPE rather than from a
     * value — a terms or value-count aggregation is a keyword aggregation, min/max/stats/histogram
     * are numeric, the date families are {@code ._datetime} and geo-distance is {@code ._geopoint} —
     * which is the resolution rule the ES factories encoded as a {@code StaticIndexValueType}
     * argument each. Note {@code valueCount} resolves to the TEXT variant, not the numeric one:
     * {@code ValueCountAggregationQueryBuilderFactory} passes {@code STRING}, so a value count over
     * a numeric property counts its text variant, and every value has one.
     */
    private static String field( String name, JsonNode params, String postfix )
    {
        String field = text( params, "field" );
        if ( field == null || field.isBlank() )
        {
            throw unsupported( "Aggregation '" + name + "' needs a 'field'" );
        }
        return IndexFields.physicalName( field.trim().toLowerCase( Locale.ROOT ) + postfix );
    }

    // --- response --------------------------------------------------------------------

    /**
     * @param canonical the canonical REQUEST config -- the only place the aggregation kinds exist
     * @param response  the whole search response
     * @return the tagged aggregation document, or {@code ""} when none was requested
     */
    static String decode( JsonNode canonical, JsonNode response )
    {
        if ( canonical == null || !canonical.isObject() || canonical.isEmpty() )
        {
            return "";
        }
        JsonNode aggregations = response == null ? null : response.get( "aggregations" );
        if ( aggregations == null || !aggregations.isObject() )
        {
            // Requested and absent: a search that carried aggregations and came back without them is
            // a translator or engine surprise, not an empty result -- an empty result still has an
            // (empty) buckets array per aggregation.
            throw unsupported( "The search response carries no 'aggregations' section, but " + canonical.size() +
                                   " aggregation(s) were requested" );
        }
        return decodeAll( canonical, aggregations ).toString();
    }

    private static ArrayNode decodeAll( JsonNode canonical, JsonNode aggregations )
    {
        ArrayNode out = OpenSearchClient.mapper().createArrayNode();
        canonical.properties().forEach( entry -> {
            String name = entry.getKey();
            JsonNode raw = aggregations.get( name );
            if ( raw == null || !raw.isObject() )
            {
                throw unsupported( "Aggregation '" + name + "' is missing from the response" );
            }
            out.add( decodeOne( name, type( name, entry.getValue() ), entry.getValue(), raw ) );
        } );
        return out;
    }

    private static ObjectNode decodeOne( String name, String type, JsonNode config, JsonNode raw )
    {
        ObjectNode out = object();
        // The two tags. Everything the client does is driven by these and by nothing else.
        out.put( "name", name );
        out.put( "type", type );
        out.put( "keyType", keyType( type ) );

        if ( BUCKET_TYPES.contains( type ) )
        {
            out.set( "buckets", decodeBuckets( name, type, config.get( "aggregations" ), raw ) );
            return out;
        }
        if ( "stats".equals( type ) )
        {
            // Absent means "no documents": ES 2.4's InternalStats initialised min to +Infinity, max to
            // -Infinity and computed avg as sum/count (0/0 = NaN), and XP reads those sentinels
            // straight out. JSON cannot carry a non-finite number, so an absent metric is what the
            // client turns back into the sentinel -- per tag, which is why min and max can differ.
            copyNumber( out, raw, "count" );
            copyNumber( out, raw, "min" );
            copyNumber( out, raw, "max" );
            copyNumber( out, raw, "avg" );
            copyNumber( out, raw, "sum" );
            return out;
        }
        copyNumber( out, raw, "value" );
        return out;
    }

    /**
     * The bucket-key type tag. It is redundant with {@code type} by construction — and that is the
     * point: it states the ONE fact ES's class chain encoded implicitly (a date histogram's key is an
     * instant, a numeric histogram's is a double, everything else's is a string), so the client can
     * assert the pair instead of trusting a single tag. {@code none} for a metric aggregation, which
     * has no buckets at all.
     */
    private static String keyType( String type )
    {
        return switch ( type )
        {
            case "dateHistogram" -> "instant";
            case "histogram" -> "double";
            case "terms", "numericRange", "dateRange", "geoDistance" -> "string";
            default -> "none";
        };
    }

    private static ArrayNode decodeBuckets( String name, String type, JsonNode subConfig, JsonNode raw )
    {
        JsonNode buckets = raw.get( "buckets" );
        if ( buckets == null || !buckets.isArray() )
        {
            throw unsupported( "Aggregation '" + name + "' is a '" + type + "' but its response carries no bucket array" );
        }

        ArrayNode out = OpenSearchClient.mapper().createArrayNode();
        for ( JsonNode bucket : buckets )
        {
            ObjectNode decoded = object();
            decoded.put( "key", bucketKey( name, type, bucket ) );

            switch ( type )
            {
                case "dateHistogram" -> decoded.put( "keyMillis", bucket.path( "key" ).asLong() );
                case "dateRange" ->
                {
                    // Epoch millis, the wire's date form (Gate A). An unbounded side is ABSENT, and the
                    // client turns that into a null Instant -- which is what ES 2.4's
                    // InternalDateRange.Bucket returned for an infinite bound, unlike the numeric
                    // ranges below.
                    copyMillis( decoded, bucket, "from", "fromMillis" );
                    copyMillis( decoded, bucket, "to", "toMillis" );
                }
                case "numericRange", "geoDistance" ->
                {
                    // Absent means unbounded, and the client fills in -Infinity/+Infinity: that is what
                    // ES 2.4's InternalRange.Bucket.getFrom()/getTo() handed XP for an open bound.
                    copyNumber( decoded, bucket, "from" );
                    copyNumber( decoded, bucket, "to" );
                }
                default ->
                {
                }
            }

            decoded.put( "docCount", bucket.path( "doc_count" ).asLong() );
            decoded.set( "aggregations", subConfig == null || subConfig.isNull() || subConfig.isEmpty()
                ? OpenSearchClient.mapper().createArrayNode()
                : decodeAll( subConfig, bucket ) );
            out.add( decoded );
        }
        return out;
    }

    /**
     * The bucket key as XP's {@code Bucket#getKey} sees it, which is {@code getKeyAsString()} on the
     * ES path — and that is NOT always the engine's own {@code key_as_string}:
     * <ul>
     * <li><b>terms</b>: the term. {@code key_as_string} when the engine emitted one (a numeric or
     * boolean terms field), else the raw key.</li>
     * <li><b>histogram</b>: derived here, NOT read from the response. ES 2.4's
     * {@code ValueFormatter.Raw} rendered an integral double as a LONG, so a histogram over a
     * {@code double} field keyed its buckets {@code "0"}, {@code "200000"} (recorded in the baseline)
     * — while OpenSearch's {@code DocValueFormat.RAW} would render {@code "0.0"} and, with a raw
     * format, emits no {@code key_as_string} at all. Reproducing the old rendering is a one-line rule
     * ({@link #rawNumber}) and a bucket key the corpus compares EXACTLY.</li>
     * <li><b>date histogram</b>: {@code key_as_string}, i.e. the {@code format} — which is why this
     * translator always sends one.</li>
     * <li><b>ranges</b>: the engine's generated or explicit {@code key} ({@code "*-100000.0"},
     * {@code "small"}), which is byte-identical to what ES 2.4 generated.</li>
     * </ul>
     */
    private static String bucketKey( String name, String type, JsonNode bucket )
    {
        if ( "histogram".equals( type ) )
        {
            return rawNumber( bucket.path( "key" ).asDouble() );
        }
        if ( "dateHistogram".equals( type ) )
        {
            JsonNode formatted = bucket.get( "key_as_string" );
            if ( formatted == null || !formatted.isTextual() )
            {
                throw unsupported( "Date-histogram aggregation '" + name +
                                       "' came back without a formatted bucket key; the request always sets a 'format'" );
            }
            return formatted.asText();
        }

        JsonNode formatted = bucket.get( "key_as_string" );
        if ( formatted != null && formatted.isTextual() )
        {
            return formatted.asText();
        }
        JsonNode key = bucket.get( "key" );
        if ( key == null || key.isNull() )
        {
            throw unsupported( "Aggregation '" + name + "' returned a bucket with no key" );
        }
        return key.isNumber() ? rawNumber( key.asDouble() ) : key.asText();
    }

    /**
     * ES 2.4's raw numeric rendering: an integral double is a long, everything else is a double.
     * That single rule is what makes a histogram bucket key {@code "200000"} rather than
     * {@code "200000.0"}.
     */
    private static String rawNumber( double value )
    {
        if ( Double.isFinite( value ) && value == Math.rint( value ) && Math.abs( value ) < 9.007199254740992E15 )
        {
            return Long.toString( (long) value );
        }
        return Double.toString( value );
    }

    private static void copyNumber( ObjectNode target, JsonNode source, String member )
    {
        JsonNode value = source.get( member );
        if ( value != null && value.isNumber() )
        {
            target.set( member, value );
        }
    }

    private static void copyMillis( ObjectNode target, JsonNode source, String member, String as )
    {
        JsonNode value = source.get( member );
        if ( value != null && value.isNumber() )
        {
            target.put( as, value.asLong() );
        }
    }

    // --- json helpers ----------------------------------------------------------------

    private static ObjectNode object()
    {
        return OpenSearchClient.mapper().createObjectNode();
    }

    private static String text( JsonNode node, String member )
    {
        JsonNode value = node == null ? null : node.get( member );
        return value == null || value.isNull() || !value.isTextual() ? null : value.asText();
    }

    private static int intOr( JsonNode node, String member, int fallback )
    {
        JsonNode value = node == null ? null : node.get( member );
        return value == null || !value.isNumber() ? fallback : value.intValue();
    }

    private static long longOr( JsonNode node, String member, long fallback )
    {
        JsonNode value = node == null ? null : node.get( member );
        return value == null || !value.isNumber() ? fallback : value.longValue();
    }

    private static QueryDslTranslator.UnsupportedQueryException unsupported( String message )
    {
        return new QueryDslTranslator.UnsupportedQueryException( message );
    }
}
