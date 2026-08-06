package com.enonic.xp.core.nodb.corpus;

/**
 * The per-query acceptance rule of the golden-query corpus (nodb/BUILD-PHASE-4.md,
 * architecture decision 5, as SPLIT by the Gate 0(b)+(c) inventory item 2).
 * <p>
 * Decision 5 says deterministic sorts must match Elasticsearch <em>exactly</em> while
 * score-ordered results only have to match as <em>sets</em>. The Gate 0(c) inventory added a
 * third bucket: language-aware {@code _orderby_<loc>} sorts resolve to ICU collation keys, and
 * ICU/CLDR version differences legitimately change those keys -- two orderings can differ while
 * both are correct, so those deltas are recorded rather than failed.
 */
enum Acceptance
{
    /**
     * Deterministic: {@code _orderby}/numeric/date/path/manual sorts, structured queries,
     * totals, aggregation buckets. Hit ids must match <em>in order</em>, totals must match, and
     * the pre-encoded lexi-sortable sort values must match verbatim.
     */
    EXACT,

    /**
     * Score-ordered fulltext/ngram/stemmed/pathMatch/suggest results. Same hits (as a multiset)
     * and the same totals are required; ordering deltas caused by ES-2.4-vs-OpenSearch relevance
     * differences are recorded per query, not failed.
     */
    SET,

    /**
     * Language-aware {@code _orderby_<loc>} sorts (NoQL {@code COLLATE}). The hit <em>set</em>
     * and totals must still match exactly -- a collation change must never change WHICH
     * documents match -- but per-locale order deltas against a pinned ICU version are recorded
     * as documented diffs, exactly like today's four {@code FindNodesByQueryCommandTest_icuSort}
     * cases.
     */
    ICU_DOCUMENTED,

    /**
     * A construct whose Elasticsearch behaviour a RECORDED RULING deliberately changes, so the port
     * is required to DIFFER from the baseline (Phase 4 Gate C).
     * <p>
     * The other three tags all say "match the baseline". This one says the opposite, for the two
     * enumerated cases the phase's own decisions created:
     * <ul>
     * <li><b>D4</b> ruled gaps G-2 and G-4 be FIXED rather than preserved — {@code IN} with a
     * dated/numeric value and {@code range()} with upper-case string bounds matched NOTHING in ES,
     * and are meant to match now. The baseline's zero hits are the bug, recorded.</li>
     * <li>a construct ES 2.4 <b>errors</b> on and the port must answer (the multi-repo sort on a
     * field mapped in only one index — Gate 0 item 4).</li>
     * </ul>
     * The rule is therefore: <b>the port must not error, and the difference is reported as a
     * documented delta.</b> It exists because Gate 0's discipline is to MEASURE rather than reason,
     * and measuring occasionally shows that the behaviour a work order asked us to preserve was
     * never correct — pretending otherwise means either a permanently red gate or a silently
     * deleted row.
     * <p>
     * Deliberately narrow, and the weakest tag in the set: it cannot detect a WRONG new answer, only
     * an absent one. A row may carry it only with the ruling named in {@link GoldenQuery#intent}, the
     * actual result is always printed so a reviewer sees what shipped, and the comparator still fails
     * the row if the port errors — so it cannot become a way to quiet an inconvenient failure.
     */
    FIXED
}
