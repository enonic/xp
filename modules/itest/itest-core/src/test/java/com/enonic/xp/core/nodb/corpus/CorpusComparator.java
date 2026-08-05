package com.enonic.xp.core.nodb.corpus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Diffs a fresh run against the recorded baseline, per query, under that query's acceptance rule.
 * <p>
 * The whole point of the split in {@link Acceptance} is that the comparator has exactly two
 * verdicts and never a shrug:
 * <ul>
 * <li>{@link Severity#FAILURE} -- a real regression. EXACT rows must match hit ORDER; SET and
 * ICU_DOCUMENTED rows must still match the hit SET and the totals. Aggregation buckets, suggestion
 * structure and thrown errors are compared for every tag: those are deterministic regardless of
 * relevance.</li>
 * <li>{@link Severity#DOCUMENTED} -- a delta that decision 5 explicitly tolerates and requires us
 * to write down: score-order differences on SET rows, per-locale order differences on
 * ICU_DOCUMENTED rows, and raw score/{@code maxScore} values anywhere. These are reported, never
 * failed.</li>
 * </ul>
 * Scores are never a failure criterion: decision 5 talks about order and counts, and an
 * ES-2.4-vs-OpenSearch scoring formula change would otherwise fail every text row for no reason.
 * {@code maxScore} is still recorded and diffed as DOCUMENTED so the GET_ALL NaN quirk is visible
 * either way -- reproduced or deliberately dropped.
 */
final class CorpusComparator
{
    enum Severity
    {
        FAILURE,
        DOCUMENTED
    }

    record Delta(String queryId, Acceptance acceptance, Severity severity, String field, String expected, String actual)
    {
        @Override
        public String toString()
        {
            return severity + " [" + queryId + " / " + acceptance + "] " + field + "\n    baseline: " + expected + "\n    actual  : " +
                actual;
        }
    }

    private CorpusComparator()
    {
    }

    /**
     * @param baseline the committed ES-mode recording
     * @param actual   the fresh run
     * @return every delta found, in a stable order (missing/extra queries first, then per query)
     */
    static List<Delta> compare( final List<QueryOutcome> baseline, final List<QueryOutcome> actual )
    {
        final Map<String, QueryOutcome> base = index( baseline );
        final Map<String, QueryOutcome> act = index( actual );

        final List<Delta> deltas = new ArrayList<>();

        for ( final String id : base.keySet() )
        {
            if ( !act.containsKey( id ) )
            {
                deltas.add( new Delta( id, Acceptance.valueOf( base.get( id ).acceptance() ), Severity.FAILURE, "query.missing", "present",
                                       "absent from this run" ) );
            }
        }
        for ( final String id : act.keySet() )
        {
            if ( !base.containsKey( id ) )
            {
                deltas.add( new Delta( id, Acceptance.valueOf( act.get( id ).acceptance() ), Severity.FAILURE, "query.unrecorded",
                                       "absent from the baseline", "present -- re-record the baseline" ) );
            }
        }

        for ( final Map.Entry<String, QueryOutcome> entry : base.entrySet() )
        {
            final QueryOutcome expected = entry.getValue();
            final QueryOutcome found = act.get( entry.getKey() );
            if ( found != null )
            {
                compareOne( expected, found, deltas );
            }
        }

        return List.copyOf( deltas );
    }

    private static void compareOne( final QueryOutcome expected, final QueryOutcome actual, final List<Delta> deltas )
    {
        final String id = expected.id();
        final Acceptance rule = Acceptance.valueOf( expected.acceptance() );

        // The acceptance tag itself moving is a corpus edit, not a backend difference; flag it so a
        // silent re-tagging cannot weaken the gate.
        if ( !expected.acceptance().equals( actual.acceptance() ) )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "acceptance", expected.acceptance(), actual.acceptance() ) );
        }

        if ( !Objects.equals( expected.error(), actual.error() ) )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "error", String.valueOf( expected.error() ),
                                   String.valueOf( actual.error() ) ) );
            return;
        }

        if ( expected.totalHits() != actual.totalHits() )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "totalHits", Long.toString( expected.totalHits() ),
                                   Long.toString( actual.totalHits() ) ) );
        }

        compareHitIds( id, rule, expected, actual, deltas );

        if ( !expected.maxScore().equals( actual.maxScore() ) )
        {
            deltas.add( new Delta( id, rule, Severity.DOCUMENTED, "maxScore", expected.maxScore(), actual.maxScore() ) );
        }

        compareHitDetails( id, rule, expected, actual, deltas );

        // Aggregations are deterministic under every acceptance rule: buckets, keys, counts, order
        // and metric values must all match.
        final String expectedAggs = renderAggs( expected.aggregations() );
        final String actualAggs = renderAggs( actual.aggregations() );
        if ( !expectedAggs.equals( actualAggs ) )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "aggregations", expectedAggs, actualAggs ) );
        }

        compareSuggestions( id, rule, expected, actual, deltas );
    }

    private static void compareHitIds( final String id, final Acceptance rule, final QueryOutcome expected, final QueryOutcome actual,
                                       final List<Delta> deltas )
    {
        final List<String> expectedIds = expected.hits().stream().map( QueryOutcome.Hit::id ).toList();
        final List<String> actualIds = actual.hits().stream().map( QueryOutcome.Hit::id ).toList();

        if ( expectedIds.equals( actualIds ) )
        {
            return;
        }

        if ( rule == Acceptance.EXACT )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "hits.order", String.join( ",", expectedIds ),
                                   String.join( ",", actualIds ) ) );
            return;
        }

        // SET / ICU_DOCUMENTED: the SET must still be identical -- only the order may move.
        final Map<String, Integer> expectedBag = bag( expectedIds );
        final Map<String, Integer> actualBag = bag( actualIds );
        if ( !expectedBag.equals( actualBag ) )
        {
            deltas.add( new Delta( id, rule, Severity.FAILURE, "hits.set", expectedBag.toString(), actualBag.toString() ) );
        }
        else
        {
            deltas.add( new Delta( id, rule, Severity.DOCUMENTED, "hits.order", String.join( ",", expectedIds ),
                                   String.join( ",", actualIds ) ) );
        }
    }

    /**
     * Per-hit sort values and highlight fragments, matched by node id so a tolerated order delta on
     * a SET row does not cascade into a wall of positional noise.
     */
    private static void compareHitDetails( final String id, final Acceptance rule, final QueryOutcome expected, final QueryOutcome actual,
                                           final List<Delta> deltas )
    {
        final Map<String, QueryOutcome.Hit> actualById = new LinkedHashMap<>();
        actual.hits().forEach( hit -> actualById.putIfAbsent( hit.id(), hit ) );

        // Sort values on an EXACT row are the pre-encoded lexi-sortable keys and must port
        // verbatim; on an ICU row they are collation keys from a specific ICU/CLDR version and are
        // expected to differ.
        final Severity sortSeverity = rule == Acceptance.EXACT ? Severity.FAILURE : Severity.DOCUMENTED;

        for ( final QueryOutcome.Hit expectedHit : expected.hits() )
        {
            final QueryOutcome.Hit actualHit = actualById.get( expectedHit.id() );
            if ( actualHit == null )
            {
                continue; // already reported as a set/order delta
            }

            if ( !Objects.equals( expectedHit.path(), actualHit.path() ) )
            {
                deltas.add( new Delta( id, rule, Severity.FAILURE, "hits[" + expectedHit.id() + "].path",
                                       String.valueOf( expectedHit.path() ), String.valueOf( actualHit.path() ) ) );
            }
            if ( !Objects.equals( expectedHit.index(), actualHit.index() ) ||
                !Objects.equals( expectedHit.type(), actualHit.type() ) )
            {
                deltas.add( new Delta( id, rule, Severity.FAILURE, "hits[" + expectedHit.id() + "].attribution",
                                       expectedHit.index() + "/" + expectedHit.type(), actualHit.index() + "/" + actualHit.type() ) );
            }
            if ( !Objects.equals( expectedHit.sort(), actualHit.sort() ) )
            {
                deltas.add( new Delta( id, rule, sortSeverity, "hits[" + expectedHit.id() + "].sort",
                                       String.valueOf( expectedHit.sort() ), String.valueOf( actualHit.sort() ) ) );
            }
            if ( !Objects.equals( expectedHit.score(), actualHit.score() ) )
            {
                deltas.add( new Delta( id, rule, Severity.DOCUMENTED, "hits[" + expectedHit.id() + "].score", expectedHit.score(),
                                       actualHit.score() ) );
            }
            final String expectedHl = renderHighlight( expectedHit.highlight() );
            final String actualHl = renderHighlight( actualHit.highlight() );
            if ( !expectedHl.equals( actualHl ) )
            {
                // Highlight fragments are engine-generated text; on a scored row they move with the
                // relevance, on a deterministic row they must not.
                deltas.add( new Delta( id, rule, rule == Acceptance.EXACT ? Severity.FAILURE : Severity.DOCUMENTED,
                                       "hits[" + expectedHit.id() + "].highlight", expectedHl, actualHl ) );
            }
        }
    }

    /**
     * Suggestion structure (names, entry texts, offsets, lengths and option texts in order) is
     * compared; option scores and frequencies are documented only, since a suggester's scoring is
     * as engine-specific as a query's.
     */
    private static void compareSuggestions( final String id, final Acceptance rule, final QueryOutcome expected,
                                            final QueryOutcome actual, final List<Delta> deltas )
    {
        final String expectedStructure = renderSuggestStructure( expected.suggestions() );
        final String actualStructure = renderSuggestStructure( actual.suggestions() );
        if ( !expectedStructure.equals( actualStructure ) )
        {
            deltas.add( new Delta( id, rule, rule == Acceptance.EXACT ? Severity.FAILURE : Severity.DOCUMENTED, "suggestions",
                                   expectedStructure, actualStructure ) );
        }
        final String expectedScores = renderSuggestScores( expected.suggestions() );
        final String actualScores = renderSuggestScores( actual.suggestions() );
        if ( !expectedScores.equals( actualScores ) )
        {
            deltas.add( new Delta( id, rule, Severity.DOCUMENTED, "suggestions.scores", expectedScores, actualScores ) );
        }
    }

    // ------------------------------------------------------------------ rendering helpers

    private static Map<String, QueryOutcome> index( final List<QueryOutcome> outcomes )
    {
        final Map<String, QueryOutcome> map = new TreeMap<>();
        outcomes.forEach( outcome -> map.put( outcome.id(), outcome ) );
        return map;
    }

    private static Map<String, Integer> bag( final List<String> ids )
    {
        final Map<String, Integer> bag = new TreeMap<>();
        ids.forEach( id -> bag.merge( id, 1, Integer::sum ) );
        return bag;
    }

    private static String renderAggs( final List<QueryOutcome.Agg> aggregations )
    {
        final StringBuilder sb = new StringBuilder();
        for ( final QueryOutcome.Agg agg : aggregations )
        {
            sb.append( agg.name() ).append( '<' ).append( agg.kind() ).append( '>' ).append( agg.metrics() ).append( '{' );
            for ( final QueryOutcome.Bucket bucket : agg.buckets() )
            {
                sb.append( bucket.key() ).append( '=' ).append( bucket.docCount() );
                if ( !bucket.subAggregations().isEmpty() )
                {
                    sb.append( '(' ).append( renderAggs( bucket.subAggregations() ) ).append( ')' );
                }
                sb.append( ';' );
            }
            sb.append( "} " );
        }
        return sb.toString();
    }

    private static String renderHighlight( final List<QueryOutcome.Highlight> highlights )
    {
        final StringBuilder sb = new StringBuilder();
        highlights.forEach( highlight -> sb.append( highlight.name() ).append( '=' ).append( highlight.fragments() ).append( ' ' ) );
        return sb.toString();
    }

    private static String renderSuggestStructure( final List<QueryOutcome.Suggest> suggestions )
    {
        final StringBuilder sb = new StringBuilder();
        for ( final QueryOutcome.Suggest suggest : suggestions )
        {
            sb.append( suggest.name() ).append( '{' );
            for ( final QueryOutcome.SuggestEntry entry : suggest.entries() )
            {
                sb.append( entry.text() ).append( '@' ).append( entry.offset() ).append( '+' ).append( entry.length() ).append( "->" );
                entry.options().forEach( option -> sb.append( option.text() ).append( ',' ) );
                sb.append( ';' );
            }
            sb.append( "} " );
        }
        return sb.toString();
    }

    private static String renderSuggestScores( final List<QueryOutcome.Suggest> suggestions )
    {
        final StringBuilder sb = new StringBuilder();
        for ( final QueryOutcome.Suggest suggest : suggestions )
        {
            for ( final QueryOutcome.SuggestEntry entry : suggest.entries() )
            {
                entry.options()
                    .forEach( option -> sb.append( suggest.name() )
                        .append( '/' )
                        .append( option.text() )
                        .append( '=' )
                        .append( option.score() )
                        .append( '/' )
                        .append( option.freq() )
                        .append( ' ' ) );
            }
        }
        return sb.toString();
    }
}
