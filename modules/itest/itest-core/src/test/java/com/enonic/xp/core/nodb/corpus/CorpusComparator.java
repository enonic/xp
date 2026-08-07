package com.enonic.xp.core.nodb.corpus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    /**
     * Ids of the corpus rows whose query declares an ORDER BY. Supplied by the harness from the
     * corpus TABLE (not from the artifact, which is why adding this needed no re-record): decision
     * 5 as split by D7 makes hit ORDER a hard requirement for DETERMINISTIC SORTS, and a query with
     * no sort has no deterministic order to require -- Elasticsearch returns such hits in
     * {@code _doc} order, which is an engine-internal document layout. Rows outside this set still
     * have their hit SET and totals compared exactly; only the sequence is reported rather than
     * failed.
     */
    private static Set<String> orderedIds = Set.of();

    static void orderedRows( final Set<String> ids )
    {
        orderedIds = Set.copyOf( ids );
    }

    /**
     * Ids of the rows whose sort VALUE is computed by the engine rather than pre-encoded by XP —
     * today exactly the {@code geoDistance} sorts. Supplied by the harness from the corpus table,
     * like {@link #orderedRows}.
     *
     * <p>A third narrowing of decision 5, and Gate D measured the need for it. The rule that EXACT
     * rows must match sort values verbatim is justified by WHAT those values are: order-by fields
     * hold pre-encoded lexi-sortable ASCII that {@code OrderByValueResolver} produced on the XP
     * side, so they port bit-for-bit or something is wrong. A geo distance is not one of those —
     * XP ships a {@code geo_point} and the ENGINE computes metres from it, which puts the value in
     * the same category as {@code _score}: engine arithmetic, already documented-only for exactly
     * this reason.
     *
     * <p>The measurement: ES 2.4 defaulted to {@code sloppy_arc} (a deliberate approximation),
     * OpenSearch defaults to {@code arc}, and XP never set {@code distance_type} on either — so the
     * distances differ by ~0.13% while the ORDER is identical. It cannot be pinned back, because
     * {@code sloppy_arc} was removed from the engine. Failing the row would mean requiring two
     * engines to approximate the earth identically.
     *
     * <p>Deliberately narrow: only the sort VALUE relaxes. Hit ORDER on these rows is still EXACT
     * (they are in {@link #orderedRows} too), which is what actually protects the sort — a geo sort
     * that silently stopped sorting still fails.
     */
    private static Set<String> engineComputedSortIds = Set.of();

    static void engineComputedSortRows( final Set<String> ids )
    {
        engineComputedSortIds = Set.copyOf( ids );
    }

    /**
     * True when the run's backend is the SAME one the baseline was recorded on — i.e. an ES-mode run
     * diffing against the ES baseline, which is a self-diff, not a port comparison.
     *
     * <p>Phase 4 Gate F found this by running the full suites in BOTH modes, which is the one thing
     * that exposes it. The {@link Acceptance#FIXED} tag INVERTS the contract: a recorded ruling says
     * the port must differ from the baseline, so "still failing" is a FAILURE. That is exactly right
     * for nodb and exactly wrong for Elasticsearch, where the row must still error because it is the
     * behaviour the ruling is measured against — so {@code SOURCE-03-multi-repo-sort-unmapped-field}
     * failed the ES-mode suite, and had been failing it since Gate C introduced the tag, unnoticed
     * because the full ES suite had not been run since. On a self-diff a FIXED row is compared like
     * any other row: it must reproduce the baseline exactly, errors included.
     */
    private static boolean selfDiff;

    static void selfDiff( final boolean value )
    {
        selfDiff = value;
    }

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

        // A FIXED row inverts the usual contract: a recorded ruling (D4, or Gate 0 item 4) says the
        // port MUST differ from the baseline, so the difference is the expected outcome and is
        // reported rather than failed. The one hard requirement is that the port answers at all --
        // see Acceptance.FIXED for why this is the weakest tag in the set and how it is fenced.
        // The inversion applies to a PORT comparison only: on a self-diff (see #selfDiff) the
        // baseline's own behaviour, error included, is what must be reproduced.
        if ( rule == Acceptance.FIXED && !selfDiff )
        {
            if ( actual.error() != null )
            {
                deltas.add( new Delta( id, rule, Severity.FAILURE, "error", String.valueOf( expected.error() ),
                                       "still failing: " + actual.error() ) );
            }
            else
            {
                deltas.add( new Delta( id, rule, Severity.DOCUMENTED, "ruled.changed",
                                       "error=" + expected.error() + " totalHits=" + expected.totalHits() + " hits=" +
                                           expected.hits().stream().map( QueryOutcome.Hit::id ).toList(),
                                       "totalHits=" + actual.totalHits() + " hits=" +
                                           actual.hits().stream().map( QueryOutcome.Hit::id ).toList() ) );
            }
            return;
        }

        if ( !Objects.equals( expected.error(), actual.error() ) )
        {
            // WHETHER a construct is rejected is the contract; the exception class and wording are
            // not, and cannot be — two different engines reached through two different client stacks
            // will never spell a rejection identically (Gate 0 already narrowed the recorded message
            // for the same reason). A one-sided error is still a hard failure: that is a construct
            // that started or stopped working.
            final boolean bothRejected = expected.error() != null && actual.error() != null;
            deltas.add( new Delta( id, rule, bothRejected ? Severity.DOCUMENTED : Severity.FAILURE, "error",
                                   String.valueOf( expected.error() ), String.valueOf( actual.error() ) ) );
            if ( !bothRejected )
            {
                return;
            }
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

        if ( rule == Acceptance.EXACT && orderedIds.contains( id ) )
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
        // expected to differ; and on a geo-distance row the value is engine arithmetic rather than
        // an XP-side encoding at all (see engineComputedSortIds).
        final Severity sortSeverity =
            rule == Acceptance.EXACT && !engineComputedSortIds.contains( id ) ? Severity.FAILURE : Severity.DOCUMENTED;

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
            if ( !Objects.equals( attribution( expectedHit ), attribution( actualHit ) ) )
            {
                deltas.add( new Delta( id, rule, Severity.FAILURE, "hits[" + expectedHit.id() + "].attribution",
                                       attribution( expectedHit ), attribution( actualHit ) ) );
            }
            if ( !Objects.equals( expectedHit.sort(), actualHit.sort() ) )
            {
                // One narrow exception: the baseline carries sort values and the port carries NONE.
                // That happens for exactly one shape -- an unsorted GET_ALL, where Elasticsearch's
                // scroll injected `sort: _doc` and returned the LUCENE DOCUMENT ADDRESS as the sort
                // value. That number is an engine-internal address with no portable meaning (the
                // OpenSearch equivalent is a PIT-scoped _shard_doc, which the executor strips for the
                // same reason), so requiring it to match would be requiring the two engines to lay
                // documents out identically. Nothing is lost: if a genuinely sorted query came back
                // unsorted, hits.order above has already failed.
                final Severity severity = missingSortSentinel( expectedHit ) || missingSortSentinel( actualHit ) ? Severity.DOCUMENTED
                    : sortSeverity;
                deltas.add( new Delta( id, rule, severity, "hits[" + expectedHit.id() + "].sort",
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

    /**
     * Whether a hit's sort values are the engine's "this document has no value for the sort field"
     * sentinel, or absent entirely.
     * <p>
     * Two shapes, one cause. {@code 9223372036854775807} is {@code Long.MAX_VALUE}, what
     * Elasticsearch returns for a missing value under {@code unmapped_type: long} —
     * {@code SortQueryBuilderFactory}'s hardcoded type, which Gate 0 item 4 identified as a latent
     * bug and Gate B fixed to {@code keyword}, so the same missing value now comes back as
     * {@code null}. An EMPTY list is the injected-tiebreaker case (see the caller). In both, the two
     * runs agree that the document has no sort value; only the spelling of "none" differs, and
     * requiring the old spelling would be requiring the bug back.
     * <p>
     * Anything else — two real, different sort keys — stays a failure, which is what actually
     * guards the pre-encoded lexi-sortable values the port has to reproduce byte for byte.
     */
    private static boolean missingSortSentinel( final QueryOutcome.Hit hit )
    {
        return hit.sort().isEmpty() || hit.sort().stream().allMatch( v -> v == null || "null".equals( v ) || "9223372036854775807".equals( v ) );
    }

    /**
     * A hit's LOGICAL attribution: {@code <repository>/<branch>}.
     * <p>
     * Both backends answer "where did this hit come from" through the same two SPI fields, but they
     * fill the first one differently, and neither is wrong. Elasticsearch reports the PHYSICAL index
     * name -- {@code search-<repo>} -- because on that backend the repository IS a pair of indices.
     * NoDB reports the repository id, because a Phase-4 hit's attribution rides explicit
     * {@code _repo}/{@code _branch} document fields: under generational names
     * ({@code <tenant>-<repo>+g<N>}) an index name cannot be parsed back into a repository, and
     * DESIGN §5 forbids trying.
     * <p>
     * So the ES index prefix is stripped before comparing. This narrows the assertion to what the
     * corpus is actually for -- did the hit come from the right repository and branch -- and NOT to
     * how a backend spells its own storage. The branch half is compared verbatim, unnormalized: it
     * is the same value on both sides, and it is what the multi-repo rows exist to pin.
     */
    private static String attribution( final QueryOutcome.Hit hit )
    {
        final String index = hit.index() == null ? null : hit.index().replaceFirst( "^(search|storage)-", "" );
        return index + "/" + hit.type();
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
