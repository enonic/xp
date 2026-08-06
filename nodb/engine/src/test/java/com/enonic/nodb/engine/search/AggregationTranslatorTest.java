package com.enonic.nodb.engine.search;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate E's aggregation translator, request and response. JSON→JSON with no OpenSearch in sight, for
 * the reason {@link SuggestAndHighlightTranslatorTest} states: the corpus proves the RESULTS, while
 * the traps here are in the request SHAPE and in the response TAGS — a wrong physical field name
 * returns empty buckets with no error, and a wrong tag builds the wrong XP bucket class four layers
 * away from where it was decided.
 */
class AggregationTranslatorTest
{
    // ---- field resolution per aggregation TYPE ---------------------------------------

    /**
     * The postfix comes from the aggregation type, not from a value — the response-side equivalent of
     * the query side's "type from the {@code Value}". {@code valueCount} resolving to the TEXT variant
     * is not a slip: {@code ValueCountAggregationQueryBuilderFactory} passes {@code STRING}, so a
     * value count over a numeric property has always counted its text variant.
     */
    @Test
    void everyAggregationTypeResolvesItsOwnSubField()
    {
        assertEquals( "category._text", field( "{\"a\":{\"terms\":{\"field\":\"category\",\"order\":{\"type\":\"TERM\"," +
                                                   "\"direction\":\"ASC\"}}}}", "terms" ) );
        assertEquals( "population._text", field( "{\"a\":{\"valueCount\":{\"field\":\"population\"}}}", "value_count" ) );
        assertEquals( "population._number", field( "{\"a\":{\"stats\":{\"field\":\"population\"}}}", "stats" ) );
        assertEquals( "population._number", field( "{\"a\":{\"min\":{\"field\":\"population\"}}}", "min" ) );
        assertEquals( "population._number", field( "{\"a\":{\"max\":{\"field\":\"population\"}}}", "max" ) );
        assertEquals( "population._number",
                      field( "{\"a\":{\"histogram\":{\"field\":\"population\",\"interval\":100}}}", "histogram" ) );
        assertEquals( "population._number",
                      field( "{\"a\":{\"numericRange\":{\"field\":\"population\",\"ranges\":[{\"to\":1}]}}}", "range" ) );
        assertEquals( "founded._datetime",
                      field( "{\"a\":{\"dateRange\":{\"field\":\"founded\",\"ranges\":[{\"to\":\"now\"}]}}}", "date_range" ) );
        assertEquals( "founded._datetime",
                      field( "{\"a\":{\"dateHistogram\":{\"field\":\"founded\",\"interval\":\"1M\"}}}", "date_histogram" ) );
        assertEquals( "location._geopoint", field(
            "{\"a\":{\"geoDistance\":{\"field\":\"location\",\"origin\":{\"lat\":1,\"lon\":2},\"ranges\":[{\"to\":10}]}}}",
            "geo_distance" ) );
    }

    /** Rule 1 defensively, exactly as the query translator does it. */
    @Test
    void fieldNamesAreLowercasedAndTrimmed()
    {
        assertEquals( "data.category._text", field( "{\"a\":{\"terms\":{\"field\":\"  Data.Category \",\"order\":{\"type\":\"TERM\"," +
                                                        "\"direction\":\"ASC\"}}}}", "terms" ) );
    }

    // ---- terms ----------------------------------------------------------------------

    @Test
    void termsCarriesEveryParameterXpSetsAndTranslatesTheOrderKey()
    {
        assertEquals( "{\"a\":{\"terms\":{\"field\":\"category._text\",\"size\":2,\"min_doc_count\":3," +
                          "\"order\":{\"_count\":\"desc\"}}}}", translate(
            "{\"a\":{\"terms\":{\"field\":\"category\",\"size\":2,\"minDocCount\":3," +
                "\"order\":{\"type\":\"DOC_COUNT\",\"direction\":\"DESC\"}}}}" ) );

        // _key, not ES 2.4's _term: Terms.Order.term(..) serialized as _term and OpenSearch renamed it.
        assertTrue( translate( "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}}}" ).contains(
            "\"order\":{\"_key\":\"asc\"}" ) );
    }

    /** XP's own defaults, not the engine's — a histogram/terms min_doc_count of 1 vs the engine's 0. */
    @Test
    void anOmittedSizeOrMinDocCountFallsBackToXpsDefaultNotTheEngines()
    {
        String body = translate( "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}}}" );
        assertTrue( body.contains( "\"size\":10" ), body );
        assertTrue( body.contains( "\"min_doc_count\":1" ), body );

        assertTrue( translate( "{\"a\":{\"histogram\":{\"field\":\"p\",\"interval\":10}}}" ).contains( "\"min_doc_count\":1" ) );
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}}" ).contains( "\"min_doc_count\":1" ) );
    }

    /**
     * ES 2.4 read a terms size of 0 as "unlimited"; OpenSearch rejects it. Falling through to the
     * engine's default of 10 would silently truncate a caller who asked for everything.
     */
    @Test
    void aTermsSizeOfZeroBecomesUnlimitedRatherThanTheEnginesDefault()
    {
        assertTrue( translate( "{\"a\":{\"terms\":{\"field\":\"c\",\"size\":0,\"order\":{\"type\":\"TERM\"," +
                                   "\"direction\":\"ASC\"}}}}" ).contains( "\"size\":2147483647" ) );
    }

    @Test
    void anUnknownOrderTypeOrDirectionFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate(
            "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"SCORE\",\"direction\":\"ASC\"}}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate(
            "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"UP\"}}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"terms\":{\"field\":\"c\"}}}" ) );
    }

    // ---- THE INTERVAL RULE ----------------------------------------------------------

    /**
     * The sixteen strings ES 2.4's {@code DATE_FIELD_UNITS} table held, i.e. the ones it rounded by
     * calendar field. They are also exactly what OpenSearch's {@code calendar_interval} accepts.
     */
    @Test
    void everyEs24CalendarUnitBecomesACalendarInterval()
    {
        for ( String interval : List.of( "year", "1y", "quarter", "1q", "month", "1M", "week", "1w", "day", "1d", "hour", "1h", "minute",
                                         "1m", "second", "1s" ) )
        {
            String body = translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"" + interval + "\"}}}" );
            assertTrue( body.contains( "\"calendar_interval\":\"" + interval + "\"" ),
                        interval + " must be a calendar interval, got " + body );
        }
    }

    /** Anything else is a fixed duration — including a MULTIPLE of a calendar unit. */
    @Test
    void anythingElseBecomesAFixedInterval()
    {
        for ( String interval : List.of( "90m", "30s", "12h", "2d", "500ms", "7d", "1ms" ) )
        {
            String body = translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"" + interval + "\"}}}" );
            assertTrue( body.contains( "\"fixed_interval\":\"" + interval + "\"" ),
                        interval + " must be a fixed interval, got " + body );
        }
    }

    /**
     * <b>The ambiguous case.</b> {@code 1d} is accepted by BOTH {@code calendar_interval} and
     * {@code fixed_interval}, and they are not equivalent (a calendar day is 23 or 25 hours across a
     * DST boundary). The tie is broken by the table above, i.e. by what ES 2.4 computed for it and
     * therefore by what the corpus baseline records — corpus row {@code AGG-12}. {@code 1h},
     * {@code 1m} and {@code 1s} are ambiguous in exactly the same way and resolve the same way, while
     * {@code 24h} — the same duration spelled as a multiple — is FIXED, which is the pair that shows
     * the rule is about the spelling ES 2.4 classified, not about the duration.
     */
    @Test
    void theAmbiguousIntervalsResolveToCalendarBecauseEs24DidToo()
    {
        for ( String ambiguous : List.of( "1d", "1h", "1m", "1s" ) )
        {
            assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"" + ambiguous + "\"}}}" ).contains(
                "\"calendar_interval\":\"" + ambiguous + "\"" ), ambiguous + " must resolve to calendar" );
        }
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"24h\"}}}" ).contains(
            "\"fixed_interval\":\"24h\"" ), "the same duration spelled as a multiple is FIXED" );
    }

    /** {@code 1M} is a month and {@code 1m} is a minute: this input is never case-normalised. */
    @Test
    void intervalCaseIsSignificant()
    {
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}}" ).contains(
            "\"calendar_interval\":\"1M\"" ) );
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1m\"}}}" ).contains(
            "\"calendar_interval\":\"1m\"" ) );
        // …and a multiple of each keeps its case too, where the unit letter is the whole meaning.
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"90m\"}}}" ).contains(
            "\"fixed_interval\":\"90m\"" ) );
    }

    /** An interval that is neither is rejected rather than handed over for the engine to reinterpret. */
    @Test
    void anUninterpretableIntervalFailsLoudly()
    {
        for ( String interval : List.of( "2M", "3w", "fortnight", "1", "d", "1 d", "-1d", "1Y" ) )
        {
            assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                          () -> translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"" + interval + "\"}}}" ),
                          interval + " must be rejected" );
        }
    }

    // ---- histograms and ranges ------------------------------------------------------

    @Test
    void theNumericHistogramCarriesIntervalBoundsAndOrder()
    {
        assertEquals( "{\"a\":{\"histogram\":{\"field\":\"p._number\",\"interval\":100,\"min_doc_count\":0," +
                          "\"extended_bounds\":{\"min\":0,\"max\":900},\"order\":{\"_count\":\"asc\"}}}}", translate(
            "{\"a\":{\"histogram\":{\"field\":\"p\",\"interval\":100,\"minDocCount\":0," +
                "\"extendedBounds\":{\"min\":0,\"max\":900},\"order\":\"COUNT_ASC\"}}}" ) );

        // extended_bounds, not ES 2.4's extendedBounds -- DoubleBounds/LongBounds replaced the builder
        // call AND the wire name changed with it.
        assertFalse( translate( "{\"a\":{\"histogram\":{\"field\":\"p\",\"interval\":1}}}" ).contains( "extended" ),
                     "absent bounds stay absent" );
    }

    @Test
    void allFourHistogramOrdersMap()
    {
        assertTrue( histogramOrder( "KEY_ASC" ).contains( "\"order\":{\"_key\":\"asc\"}" ) );
        assertTrue( histogramOrder( "KEY_DESC" ).contains( "\"order\":{\"_key\":\"desc\"}" ) );
        assertTrue( histogramOrder( "COUNT_ASC" ).contains( "\"order\":{\"_count\":\"asc\"}" ) );
        assertTrue( histogramOrder( "COUNT_DESC" ).contains( "\"order\":{\"_count\":\"desc\"}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> histogramOrder( "SCORE" ) );
    }

    @Test
    void aHistogramWithHalfAnExtendedBoundFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate(
            "{\"a\":{\"histogram\":{\"field\":\"p\",\"interval\":1,\"extendedBounds\":{\"min\":0}}}}" ) );
    }

    /** An absent bound is an OPEN bound and must stay absent: {@code "from": null} is another query. */
    @Test
    void openRangeBoundsStayAbsent()
    {
        assertEquals( "{\"a\":{\"range\":{\"field\":\"p._number\",\"ranges\":[{\"to\":100.0},{\"from\":100.0,\"to\":500.0}," +
                          "{\"from\":500.0}]}}}", translate(
            "{\"a\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"to\":100},{\"from\":100,\"to\":500},{\"from\":500}]}}}" ) );
    }

    @Test
    void aRangeKeyIsCarriedWhenTheCallerGaveOne()
    {
        assertTrue( translate( "{\"a\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"key\":\"small\",\"to\":1}]}}}" ).contains(
            "{\"key\":\"small\",\"to\":1.0}" ) );
    }

    @Test
    void aRangeWithNoBoundsAtAllFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"key\":\"k\"}]}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[]}}}" ) );
    }

    /**
     * A date bound rides as a string and is passed through VERBATIM, because it may be ES date math
     * ({@code now-5h}, {@code 2014-12-10T10:00:00Z||-3h}) which only the engine can evaluate.
     */
    @Test
    void dateRangeBoundsIncludingDateMathArePassedThrough()
    {
        assertEquals( "{\"a\":{\"date_range\":{\"field\":\"f._datetime\",\"format\":\"strict_date_optional_time\"," +
                          "\"ranges\":[{\"to\":\"now-5h\"},{\"from\":\"2014-12-10T10:00:00Z||-3h\"}]}}}", translate(
            "{\"a\":{\"dateRange\":{\"field\":\"f\",\"ranges\":[{\"to\":\"now-5h\"},{\"from\":\"2014-12-10T10:00:00Z||-3h\"}]}}}" ) );
    }

    /**
     * The default date format, and why it cannot be omitted: the {@code *._datetime} mapping declares
     * {@code epoch_millis||strict_date_optional_time}, so an unset format keys date buckets by EPOCH
     * MILLIS where ES 2.4 keyed them by ISO instant. A format XP does set wins.
     */
    @Test
    void dateAggregationsAlwaysCarryAFormatAndXpsWins()
    {
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}}" ).contains(
            "\"format\":\"strict_date_optional_time\"" ) );
        assertTrue( translate( "{\"a\":{\"dateRange\":{\"field\":\"f\",\"ranges\":[{\"to\":\"now\"}]}}}" ).contains(
            "\"format\":\"strict_date_optional_time\"" ) );
        assertTrue( translate( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1h\",\"format\":\"HH:mm\"}}}" ).contains(
            "\"format\":\"HH:mm\"" ) );
    }

    @Test
    void geoDistanceCarriesItsOriginAndUnitAndNoDistanceType()
    {
        String body = translate( "{\"a\":{\"geoDistance\":{\"field\":\"loc\",\"origin\":{\"lat\":59.9,\"lon\":10.7},\"unit\":\"km\"," +
                                     "\"ranges\":[{\"to\":100}]}}}" );
        assertEquals( "{\"a\":{\"geo_distance\":{\"field\":\"loc._geopoint\",\"origin\":{\"lat\":59.9,\"lon\":10.7}," +
                          "\"unit\":\"km\",\"ranges\":[{\"to\":100.0}]}}}", body );
        assertFalse( body.contains( "distance_type" ), "XP never set it; the default moved from sloppy_arc to arc" );
    }

    /**
     * A correctness requirement, measured against a real engine: {@code range} and {@code date_range}
     * sort their own range array, but {@code geo_distance} does not — and the shared
     * {@code RangeAggregator} binary-searches it assuming order, so an unsorted geo range list
     * SILENTLY LOSES DOCUMENTS ({@code [{from:1000},{to:1000}]} over documents at 0 km and 5900 km
     * answers {@code *-1000.0: 0}). Sorted in two places for two different reasons — see
     * {@code AggregationDslRenderer} for the wire-determinism half.
     */
    @Test
    void geoDistanceRangesAreSortedBecauseAnUnsortedListMiscounts()
    {
        assertEquals( "{\"a\":{\"geo_distance\":{\"field\":\"l._geopoint\",\"origin\":{\"lat\":1.0,\"lon\":2.0}," +
                          "\"ranges\":[{\"to\":100.0},{\"from\":100.0,\"to\":1000.0},{\"from\":1000.0}]}}}", translate(
            "{\"a\":{\"geoDistance\":{\"field\":\"l\",\"origin\":{\"lat\":1,\"lon\":2}," +
                "\"ranges\":[{\"from\":1000},{\"from\":100,\"to\":1000},{\"to\":100}]}}}" ) );
    }

    @Test
    void geoDistanceWithoutAnOriginFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"geoDistance\":{\"field\":\"loc\",\"ranges\":[{\"to\":1}]}}}" ) );
    }

    // ---- THE SUB-AGGREGATION PER-TYPE RULE ------------------------------------------

    /**
     * The replacement for the vanished {@code AbstractAggregationBuilder}-vs-{@code AggregationBuilder}
     * discriminator: an explicit list of the six BUCKET types, asserted in both directions.
     */
    @Test
    void allSixBucketTypesAcceptSubAggregationsAndNestUnderAggs()
    {
        for ( String parent : List.of( "{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}}",
                                       "{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"to\":1}]}}",
                                       "{\"dateRange\":{\"field\":\"f\",\"ranges\":[{\"to\":\"now\"}]}}",
                                       "{\"geoDistance\":{\"field\":\"l\",\"origin\":{\"lat\":1,\"lon\":2},\"ranges\":[{\"to\":1}]}}",
                                       "{\"histogram\":{\"field\":\"p\",\"interval\":1}}",
                                       "{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}" ) )
        {
            String config = "{\"a\":" + parent.substring( 0, parent.length() - 1 ) +
                ",\"aggregations\":{\"sub\":{\"stats\":{\"field\":\"p\"}}}}}";
            String body = translate( config );
            assertTrue( body.contains( "\"aggs\":{\"sub\":{\"stats\":{\"field\":\"p._number\"}}}" ), body );
        }
    }

    /** …and none of the four METRIC types does. The old guard dropped these silently. */
    @Test
    void noMetricTypeAcceptsSubAggregations()
    {
        for ( String metric : List.of( "stats", "min", "max", "valueCount" ) )
        {
            String config = "{\"a\":{\"" + metric + "\":{\"field\":\"p\"},\"aggregations\":{\"sub\":{\"min\":{\"field\":\"p\"}}}}}";
            QueryDslTranslator.UnsupportedQueryException e =
                assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate( config ), metric );
            assertTrue( e.getMessage().contains( "cannot carry sub-aggregations" ), e.getMessage() );
        }
    }

    @Test
    void subAggregationsNestArbitrarilyDeep()
    {
        String body = translate( "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}," +
                                     "\"aggregations\":{\"b\":{\"histogram\":{\"field\":\"p\",\"interval\":1}," +
                                     "\"aggregations\":{\"c\":{\"max\":{\"field\":\"p\"}}}}}}}" );
        assertTrue( body.contains( "\"aggs\":{\"c\":{\"max\":{\"field\":\"p._number\"}}}" ), body );
    }

    // ---- request-side validation ----------------------------------------------------

    @Test
    void anUnknownOrMissingOrDoubledTypeFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"cardinality\":{\"field\":\"c\"}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate( "{\"a\":{}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> translate( "{\"a\":{\"min\":{\"field\":\"p\"},\"max\":{\"field\":\"p\"}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate( "{\"a\":\"terms\"}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class, () -> translate( "{\"a\":{\"min\":{}}}" ) );
    }

    @Test
    void anEmptyAggregationConfigProducesNoBlock()
    {
        assertNull( AggregationTranslator.translate( parse( "{}" ) ) );
        assertNull( AggregationTranslator.translate( null ) );
    }

    // ---- RESPONSE-SIDE TAGGING (D9) -------------------------------------------------

    /**
     * The tag is copied from the REQUEST, and this is the case that makes it necessary: the two
     * responses below are BYTE-IDENTICAL, and they must produce different XP bucket classes. On the ES
     * path the only thing that told them apart was {@code instanceof InternalHistogram} preceding
     * {@code instanceof Histogram}.
     */
    @Test
    void aDateHistogramAndANumericHistogramAreToldApartByTheTagAloneNotByShape()
    {
        String response = "{\"aggregations\":{\"h\":{\"buckets\":[{\"key\":1577836800000,\"key_as_string\":" +
            "\"2020-01-01T00:00:00.000Z\",\"doc_count\":2}]}}}";

        assertEquals( "[{\"name\":\"h\",\"type\":\"dateHistogram\",\"keyType\":\"instant\"," +
                          "\"buckets\":[{\"key\":\"2020-01-01T00:00:00.000Z\",\"keyMillis\":1577836800000," +
                          "\"docCount\":2,\"aggregations\":[]}]}]",
                      decode( "{\"h\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}}", response ) );

        assertEquals( "[{\"name\":\"h\",\"type\":\"histogram\",\"keyType\":\"double\"," +
                          "\"buckets\":[{\"key\":\"1577836800000\",\"docCount\":2,\"aggregations\":[]}]}]",
                      decode( "{\"h\":{\"histogram\":{\"field\":\"f\",\"interval\":1}}}", response ) );
    }

    /** Same story for the three range families, which are one engine response shape and three XP types. */
    @Test
    void theThreeRangeFamiliesAreToldApartByTheTagAlone()
    {
        String response = "{\"aggregations\":{\"r\":{\"buckets\":[{\"key\":\"*-100.0\",\"to\":100.0,\"doc_count\":3}]}}}";

        assertTrue( decode( "{\"r\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"to\":100}]}}}", response ).contains(
            "\"type\":\"numericRange\"" ) );
        assertTrue( decode( "{\"r\":{\"geoDistance\":{\"field\":\"l\",\"origin\":{\"lat\":1,\"lon\":2},\"ranges\":[{\"to\":100}]}}}",
                            response ).contains( "\"type\":\"geoDistance\"" ) );

        // …and the date range's bounds are re-tagged as MILLIS, because XP's DateRangeBucket holds
        // Instants where the other two hold Numbers.
        assertEquals( "[{\"name\":\"r\",\"type\":\"dateRange\",\"keyType\":\"string\"," +
                          "\"buckets\":[{\"key\":\"*-2021-01-01T00:00:00.000Z\",\"toMillis\":1609459200000," +
                          "\"docCount\":3,\"aggregations\":[]}]}]",
                      decode( "{\"r\":{\"dateRange\":{\"field\":\"f\",\"ranges\":[{\"to\":\"2021-01-01T00:00:00Z\"}]}}}",
                              "{\"aggregations\":{\"r\":{\"buckets\":[{\"key\":\"*-2021-01-01T00:00:00.000Z\"," +
                                  "\"to\":1.6094592E12,\"to_as_string\":\"2021-01-01T00:00:00.000Z\",\"doc_count\":3}]}}}" ) );
    }

    /**
     * ES 2.4's {@code ValueFormatter.Raw} rendered an integral double as a LONG, so a histogram over a
     * {@code double} field keyed its buckets {@code "0"}/{@code "200000"} — which is what the corpus
     * baseline holds, and what OpenSearch's {@code DocValueFormat.RAW} would render {@code "0.0"}. The
     * key is therefore derived here rather than read from the response.
     */
    @Test
    void anIntegralHistogramKeyIsRenderedAsALongExactlyAsEs24Did()
    {
        assertEquals( "[\"0\",\"200000\",\"3600000\"]", bucketKeys(
            "{\"h\":{\"histogram\":{\"field\":\"p\",\"interval\":100000}}}",
            "{\"aggregations\":{\"h\":{\"buckets\":[{\"key\":0.0,\"doc_count\":1},{\"key\":200000.0,\"doc_count\":1}," +
                "{\"key\":3600000.0,\"doc_count\":1}]}}}" ) );

        assertEquals( "[\"0.5\"]", bucketKeys( "{\"h\":{\"histogram\":{\"field\":\"p\",\"interval\":1}}}",
                                               "{\"aggregations\":{\"h\":{\"buckets\":[{\"key\":0.5,\"doc_count\":1}]}}}" ) );
    }

    @Test
    void aTermsKeyIsTheTermAndSubAggregationsAreDecodedInLockstep()
    {
        assertEquals( "[{\"name\":\"byCategory\",\"type\":\"terms\",\"keyType\":\"string\",\"buckets\":[" +
                          "{\"key\":\"c1\",\"docCount\":4,\"aggregations\":[{\"name\":\"popMin\",\"type\":\"min\"," +
                          "\"keyType\":\"none\",\"value\":45000.0}]}]}]", decode(
            "{\"byCategory\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}," +
                "\"aggregations\":{\"popMin\":{\"min\":{\"field\":\"p\"}}}}}",
            "{\"aggregations\":{\"byCategory\":{\"buckets\":[{\"key\":\"c1\",\"doc_count\":4," +
                "\"popMin\":{\"value\":45000.0}}]}}}" ) );
    }

    @Test
    void metricAggregationsCarryTheirValuesAndAKeyTypeOfNone()
    {
        assertEquals( "[{\"name\":\"s\",\"type\":\"stats\",\"keyType\":\"none\",\"count\":4,\"min\":2.0,\"max\":8.0," +
                          "\"avg\":5.0,\"sum\":20.0}]", decode( "{\"s\":{\"stats\":{\"field\":\"p\"}}}",
                                                                "{\"aggregations\":{\"s\":{\"count\":4,\"min\":2.0,\"max\":8.0," +
                                                                    "\"avg\":5.0,\"sum\":20.0}}}" ) );
        assertEquals( "[{\"name\":\"c\",\"type\":\"valueCount\",\"keyType\":\"none\",\"value\":8}]",
                      decode( "{\"c\":{\"valueCount\":{\"field\":\"p\"}}}", "{\"aggregations\":{\"c\":{\"value\":8}}}" ) );
    }

    /**
     * An empty metric comes back {@code null}, JSON has no literal for the sentinels XP reads
     * ({@code min} {@code +Infinity}, {@code max} {@code -Infinity}, {@code avg} {@code NaN}), so the
     * member is OMITTED and the client restores the sentinel per tag. Asserted here so the two halves
     * of that contract are pinned in one place.
     */
    @Test
    void anEmptyMetricOmitsItsValueRatherThanSendingZero()
    {
        assertEquals( "[{\"name\":\"s\",\"type\":\"stats\",\"keyType\":\"none\",\"count\":0,\"sum\":0.0}]",
                      decode( "{\"s\":{\"stats\":{\"field\":\"p\"}}}",
                              "{\"aggregations\":{\"s\":{\"count\":0,\"min\":null,\"max\":null,\"avg\":null,\"sum\":0.0}}}" ) );
        assertEquals( "[{\"name\":\"m\",\"type\":\"min\",\"keyType\":\"none\"}]",
                      decode( "{\"m\":{\"min\":{\"field\":\"p\"}}}", "{\"aggregations\":{\"m\":{\"value\":null}}}" ) );
    }

    /** An open bound is likewise absent, and the client's sentinel differs by tag. */
    @Test
    void anOpenRangeBoundIsAbsentFromTheDecodedBucket()
    {
        assertEquals( "[{\"name\":\"r\",\"type\":\"numericRange\",\"keyType\":\"string\",\"buckets\":[" +
                          "{\"key\":\"500000.0-*\",\"from\":500000.0,\"docCount\":2,\"aggregations\":[]}]}]",
                      decode( "{\"r\":{\"numericRange\":{\"field\":\"p\",\"ranges\":[{\"from\":500000}]}}}",
                              "{\"aggregations\":{\"r\":{\"buckets\":[{\"key\":\"500000.0-*\",\"from\":500000.0," +
                                  "\"doc_count\":2}]}}}" ) );
    }

    /** Response order is the REQUEST's order, so it does not depend on the engine's own map order. */
    @Test
    void theResponseIsOrderedByTheRequestNotByTheEngine()
    {
        assertEquals( "[\"a\",\"b\",\"c\"]", names(
            "{\"a\":{\"min\":{\"field\":\"p\"}},\"b\":{\"max\":{\"field\":\"p\"}},\"c\":{\"valueCount\":{\"field\":\"p\"}}}",
            "{\"aggregations\":{\"c\":{\"value\":1},\"a\":{\"value\":2},\"b\":{\"value\":3}}}" ) );
    }

    /**
     * An aggregation the request asked for and the response does not contain is the exact shape of
     * "aggregations come back but throw" — a loud failure here, not a silently missing entry.
     */
    @Test
    void aMissingOrMalformedResponseSectionFailsLoudly()
    {
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> decode( "{\"a\":{\"min\":{\"field\":\"p\"}}}", "{\"hits\":{\"hits\":[]}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> decode( "{\"a\":{\"min\":{\"field\":\"p\"}}}", "{\"aggregations\":{\"other\":{\"value\":1}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> decode( "{\"a\":{\"terms\":{\"field\":\"c\",\"order\":{\"type\":\"TERM\",\"direction\":\"ASC\"}}}}",
                                    "{\"aggregations\":{\"a\":{\"value\":1}}}" ) );
        assertThrows( QueryDslTranslator.UnsupportedQueryException.class,
                      () -> decode( "{\"a\":{\"dateHistogram\":{\"field\":\"f\",\"interval\":\"1M\"}}}",
                                    "{\"aggregations\":{\"a\":{\"buckets\":[{\"key\":1,\"doc_count\":1}]}}}" ) );
    }

    @Test
    void aRequestWithNoAggregationsDecodesToNothing()
    {
        assertEquals( "", AggregationTranslator.decode( parse( "{}" ), parse( "{\"hits\":{}}" ) ) );
        assertEquals( "", AggregationTranslator.decode( null, parse( "{\"hits\":{}}" ) ) );
    }

    // ---- helpers --------------------------------------------------------------------

    private static String translate( String canonical )
    {
        return AggregationTranslator.translate( parse( canonical ) ).toString();
    }

    private static String decode( String canonical, String response )
    {
        return AggregationTranslator.decode( parse( canonical ), parse( response ) );
    }

    private static String histogramOrder( String order )
    {
        return translate( "{\"a\":{\"histogram\":{\"field\":\"p\",\"interval\":1,\"order\":\"" + order + "\"}}}" );
    }

    private static String field( String canonical, String engineType )
    {
        ObjectNode aggs = AggregationTranslator.translate( parse( canonical ) );
        return aggs.path( "a" ).path( engineType ).path( "field" ).asText();
    }

    private static String bucketKeys( String canonical, String response )
    {
        JsonNode decoded = parse( decode( canonical, response ) );
        var keys = OpenSearchClient.mapper().createArrayNode();
        decoded.get( 0 ).path( "buckets" ).forEach( bucket -> keys.add( bucket.path( "key" ).asText() ) );
        return keys.toString();
    }

    private static String names( String canonical, String response )
    {
        JsonNode decoded = parse( decode( canonical, response ) );
        var names = OpenSearchClient.mapper().createArrayNode();
        decoded.forEach( agg -> names.add( agg.path( "name" ).asText() ) );
        return names.toString();
    }

    private static JsonNode parse( String json )
    {
        if ( json == null )
        {
            return null;
        }
        try
        {
            return OpenSearchClient.mapper().readTree( json );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( e );
        }
    }
}
