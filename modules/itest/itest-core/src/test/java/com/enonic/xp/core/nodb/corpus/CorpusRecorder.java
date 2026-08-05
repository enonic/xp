package com.enonic.xp.core.nodb.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.Bucket;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.aggregation.SingleValueMetricAggregation;
import com.enonic.xp.aggregation.StatsAggregation;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.highlight.HighlightedProperty;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.sortvalues.SortValuesProperty;
import com.enonic.xp.storage.spi.SearchHit;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.suggester.Suggestion;
import com.enonic.xp.suggester.SuggestionEntry;
import com.enonic.xp.suggester.SuggestionOption;
import com.enonic.xp.suggester.Suggestions;
import com.enonic.xp.suggester.TermSuggestionOption;

/**
 * Maps a live {@code SearchResult} into the recorded {@link QueryOutcome}.
 * <p>
 * Two invariants matter more than anything else here:
 * <ol>
 * <li><b>Nothing is re-ordered that carries meaning</b> -- hits, buckets and suggestion entries
 * keep the order the engine returned, because that order IS the thing decision 5 is about.</li>
 * <li><b>Everything that carries no meaning IS ordered</b> -- highlight fragments arrive in a
 * {@code HashSet} and metric values in a map, so both are sorted before serialization. The Gate
 * 0(c) inventory (item 4) calls out exactly this class of HashSet nondeterminism as a golden-file
 * breaker.</li>
 * </ol>
 */
final class CorpusRecorder
{
    private CorpusRecorder()
    {
    }

    /**
     * @param sanitizer rewrites environment-specific substrings (the timestamped test repository
     *                  id, absolute paths in exception messages) into stable placeholders, so the
     *                  artifact can be committed
     */
    static QueryOutcome record( final GoldenQuery query, final SearchResult result, final UnaryOperator<String> sanitizer )
    {
        return new QueryOutcome( query.id(), query.family(), query.acceptance().name(), query.source().name(), query.intent(),
                                 result.getTotalHits(), Float.toString( result.getMaxScore() ), hits( result, sanitizer ),
                                 aggregations( result.getAggregations() ), suggestions( result.getSuggestions() ), null );
    }

    static QueryOutcome failed( final GoldenQuery query, final Throwable t, final UnaryOperator<String> sanitizer )
    {
        return new QueryOutcome( query.id(), query.family(), query.acceptance().name(), query.source().name(), query.intent(), -1L, "NaN",
                                 List.of(), List.of(), List.of(), errorText( t, sanitizer ) );
    }

    /**
     * Records the exception class and the FIRST LINE of its message only, with any embedded
     * request payload cut off.
     * <p>
     * This is a deliberate narrowing, not laziness. {@code IndexException} embeds the whole
     * generated Elasticsearch request JSON in its message; keeping that would (a) make the row a
     * guaranteed false FAILURE at Gate C, since the OpenSearch request will legitimately look
     * different, and (b) drag HashSet-ordered collections (principal sets, aggregation sets) into
     * the artifact through the back door. What the corpus needs to pin is THAT the construct is
     * rejected and by what, not the exact bytes of a request that is about to be rewritten.
     */
    private static String errorText( final Throwable t, final UnaryOperator<String> sanitizer )
    {
        String message = t.getMessage() == null ? "" : t.getMessage();
        final int payload = message.indexOf( "query: [" );
        if ( payload >= 0 )
        {
            message = message.substring( 0, payload );
        }
        message = message.lines().findFirst().orElse( "" ).trim();
        return t.getClass().getName() + ": " + sanitizer.apply( message );
    }

    private static List<QueryOutcome.Hit> hits( final SearchResult result, final UnaryOperator<String> sanitizer )
    {
        final List<QueryOutcome.Hit> hits = new ArrayList<>();
        for ( final SearchHit hit : result.getHits() )
        {
            hits.add( new QueryOutcome.Hit( hit.getId(), path( hit ), Float.toString( hit.getScore() ), sortValues( hit.getSortValues() ),
                                            sanitizer.apply( hit.getIndexName() ), hit.getIndexType(),
                                            highlight( hit.getHighlightedProperties() ) ) );
        }
        return hits;
    }

    private static String path( final SearchHit hit )
    {
        if ( hit.getReturnValues() == null )
        {
            return null;
        }
        return hit.getReturnValues().getOptional( NodeIndexPath.PATH ).map( Object::toString ).orElse( null );
    }

    private static List<String> sortValues( final SortValuesProperty sort )
    {
        if ( sort == null || sort.getValues() == null )
        {
            return List.of();
        }
        final List<String> values = new ArrayList<>();
        for ( final Object value : sort.getValues() )
        {
            values.add( scalar( value ) );
        }
        return java.util.Collections.unmodifiableList( values );
    }

    /**
     * Highlight fragments come back inside an {@code ImmutableSet} built from a {@code HashSet},
     * and the properties themselves inside a {@code HashMap} -- both are sorted here. Note that
     * {@code HighlightedPropertiesFactory} strips the {@code ._analyzed}/{@code ._ngram} postfix,
     * so all three physical fields XP requests collapse onto ONE property name and the last one
     * wins; recording the sorted fragment set is the only stable transcript available.
     */
    private static List<QueryOutcome.Highlight> highlight( final HighlightedProperties properties )
    {
        if ( properties == null || properties.isEmpty() )
        {
            return List.of();
        }
        final TreeMap<String, List<String>> sorted = new TreeMap<>();
        for ( final HighlightedProperty property : properties )
        {
            sorted.put( property.getName(), property.getFragments().stream().sorted().toList() );
        }
        final List<QueryOutcome.Highlight> out = new ArrayList<>();
        sorted.forEach( ( name, fragments ) -> out.add( new QueryOutcome.Highlight( name, fragments ) ) );
        return List.copyOf( out );
    }

    private static List<QueryOutcome.Agg> aggregations( final Aggregations aggregations )
    {
        if ( aggregations == null || aggregations.getSize() == 0 )
        {
            return List.of();
        }
        // Aggregations is an ordered list, but XP builds the REQUEST from a HashSet, so the
        // response order is not something we can rely on: sort by name, which is unique.
        final List<Aggregation> byName = aggregations.stream().sorted( java.util.Comparator.comparing( Aggregation::getName ) ).toList();

        final List<QueryOutcome.Agg> out = new ArrayList<>();
        for ( final Aggregation aggregation : byName )
        {
            out.add( aggregation( aggregation ) );
        }
        return List.copyOf( out );
    }

    private static QueryOutcome.Agg aggregation( final Aggregation aggregation )
    {
        if ( aggregation instanceof BucketAggregation bucketAggregation )
        {
            final List<QueryOutcome.Bucket> buckets = new ArrayList<>();
            if ( bucketAggregation.getBuckets() != null )
            {
                for ( final Bucket bucket : bucketAggregation.getBuckets() )
                {
                    buckets.add( new QueryOutcome.Bucket( bucket.getKey(), bucket.getDocCount(),
                                                          aggregations( bucket.getSubAggregations() ) ) );
                }
            }
            return new QueryOutcome.Agg( aggregation.getName(), "bucket", List.of(), List.copyOf( buckets ) );
        }
        if ( aggregation instanceof StatsAggregation stats )
        {
            final List<String> metrics =
                List.of( "avg=" + scalar( stats.getAvg() ), "count=" + scalar( stats.getCount() ), "max=" + scalar( stats.getMax() ),
                         "min=" + scalar( stats.getMin() ), "sum=" + scalar( stats.getSum() ) );
            return new QueryOutcome.Agg( aggregation.getName(), "stats", metrics, subOf( aggregation ) );
        }
        if ( aggregation instanceof SingleValueMetricAggregation single )
        {
            return new QueryOutcome.Agg( aggregation.getName(), "singleValue", List.of( "value=" + scalar( single.getValue() ) ),
                                         subOf( aggregation ) );
        }
        return new QueryOutcome.Agg( aggregation.getName(), "unknown:" + aggregation.getClass().getName(), List.of(), subOf( aggregation ) );
    }

    private static List<QueryOutcome.Bucket> subOf( final Aggregation aggregation )
    {
        final List<QueryOutcome.Agg> subs = aggregations( aggregation.getSubAggregations() );
        // metric aggregations cannot carry sub-aggregations; represent any as a synthetic bucket
        // rather than dropping them silently
        return subs.isEmpty() ? List.of() : List.of( new QueryOutcome.Bucket( "<sub>", 0L, subs ) );
    }

    private static List<QueryOutcome.Suggest> suggestions( final Suggestions suggestions )
    {
        if ( suggestions == null || suggestions.getSize() == 0 )
        {
            return List.of();
        }
        final List<Suggestion> byName = suggestions.stream().sorted( java.util.Comparator.comparing( Suggestion::getName ) ).toList();

        final List<QueryOutcome.Suggest> out = new ArrayList<>();
        for ( final Suggestion<?> suggestion : byName )
        {
            final List<QueryOutcome.SuggestEntry> entries = new ArrayList<>();
            for ( final SuggestionEntry<?> entry : suggestion.getEntries() )
            {
                final List<QueryOutcome.SuggestOption> options = new ArrayList<>();
                for ( final Object raw : entry.getOptions() )
                {
                    final SuggestionOption option = (SuggestionOption) raw;
                    final Integer freq = option instanceof TermSuggestionOption term ? term.getFreq() : null;
                    options.add( new QueryOutcome.SuggestOption( option.getText(),
                                                                 option.getScore() == null ? "null" : Float.toString( option.getScore() ),
                                                                 freq ) );
                }
                entries.add( new QueryOutcome.SuggestEntry( entry.getText(), entry.getOffset(), entry.getLength(),
                                                            List.copyOf( options ) ) );
            }
            out.add( new QueryOutcome.Suggest( suggestion.getName(), List.copyOf( entries ) ) );
        }
        return List.copyOf( out );
    }

    /**
     * Canonical scalar rendering. {@code Double.toString}/{@code Float.toString} are specified
     * exactly by the JLS, so {@code NaN}, {@code Infinity} and {@code -0.0} all round-trip as
     * themselves -- which is the whole point for the GET_ALL maxScore quirk.
     */
    private static String scalar( final Object value )
    {
        if ( value == null )
        {
            return "null";
        }
        if ( value instanceof Double d )
        {
            return Double.toString( d );
        }
        if ( value instanceof Float f )
        {
            return Float.toString( f );
        }
        return Objects.toString( value );
    }
}
