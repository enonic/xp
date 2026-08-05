package com.enonic.xp.core.nodb.corpus;

import java.util.List;

/**
 * Everything the corpus records about one query execution. Deliberately flat and string-typed so
 * the artifact is a faithful, diffable transcript rather than a re-rendering of live objects:
 * every float is written with {@link Float#toString}/{@link Double#toString} so {@code NaN},
 * {@code Infinity} and negative zero survive a round-trip verbatim.
 * <p>
 * Fields documented as optional are simply nullable; there are no annotations here so the harness
 * stays free of any dependency the itest test classpath does not already guarantee.
 */
record QueryOutcome(String id, String family, String acceptance, String source, String intent, long totalHits, String maxScore,
                    List<Hit> hits, List<Agg> aggregations, List<Suggest> suggestions, String error)
{
    /**
     * @param id        node id, in result order
     * @param path      {@code _path} return value; optional, present only for rows built with
     *                  withPath
     * @param score     per-hit score, recorded but never a failure criterion
     * @param sort      raw sort values as returned; for non-ICU field sorts these are the
     *                  pre-encoded lexi-sortable ASCII keys and must port verbatim
     * @param index     physical index name; optional, recorded for multi-source rows (attribution)
     * @param type      ES mapping type == branch name; optional, recorded for multi-source rows
     * @param highlight highlighted properties, fragments sorted (the upstream container is a
     *                  HashSet)
     */
    record Hit(String id, String path, String score, List<String> sort, String index, String type, List<Highlight> highlight)
    {
    }

    record Highlight(String name, List<String> fragments)
    {
    }

    /**
     * @param name    aggregation name
     * @param kind    {@code bucket}, {@code stats} or {@code singleValue} -- the response side has
     *                no type identity on a JSON wire, so NoDB must tag this explicitly (Gate 0(c)
     *                item 1)
     * @param metrics sorted {@code key=value} pairs for metric aggregations
     * @param buckets buckets in returned order
     */
    record Agg(String name, String kind, List<String> metrics, List<Bucket> buckets)
    {
    }

    record Bucket(String key, long docCount, List<Agg> subAggregations)
    {
    }

    record Suggest(String name, List<SuggestEntry> entries)
    {
    }

    record SuggestEntry(String text, Integer offset, Integer length, List<SuggestOption> options)
    {
    }

    record SuggestOption(String text, String score, Integer freq)
    {
    }

    boolean isMeaningful()
    {
        return !hits.isEmpty() || !aggregations.isEmpty() || !suggestions.isEmpty();
    }
}
