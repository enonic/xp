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
    ICU_DOCUMENTED
}
