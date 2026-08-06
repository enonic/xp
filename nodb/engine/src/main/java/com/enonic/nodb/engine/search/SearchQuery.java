package com.enonic.nodb.engine.search;

import java.util.List;

/**
 * The search wire envelope, as the engine sees it: sources with their per-source principal
 * sets, the canonical DSL as JSON text, and the paging parameters. Deliberately a plain
 * record rather than the generated proto type — the engine has no protobuf dependency, and
 * keeping the boundary explicit is what lets the translator be unit-tested without a server.
 *
 * @param size         -1 means ALL, paged by {@code batchSize}
 * @param aggregations canonical aggregation config as JSON text, empty when none was requested
 *                     (Gate E). Kept as text on the way in and re-parsed by the executor because
 *                     it is needed TWICE: once to build the request and once, in the response
 *                     direction, as the only place the aggregation kinds exist (D9).
 * @param suggest      canonical suggest config as JSON text, empty when none was requested (Gate D)
 * @param highlight    canonical highlight config as JSON text, empty when none was requested (Gate D)
 */
public record SearchQuery(List<Source> sources, String query, List<String> queryFilters, List<String> postFilters, List<String> sort,
                          String aggregations, String suggest, String highlight, int from, int size, int batchSize, boolean explain,
                          String searchOptimizer, List<String> returnFields)
{
    /**
     * One (repo, branch, principals) triple. {@code branch} is case-preserved — it is matched
     * against the {@code _branch} keyword verbatim. {@code principals} is never empty: an empty
     * ACL is fail-closed to anonymous by the client, and the admin role is an ordinary asserted
     * principal here because the projection injects an admin read key into every document.
     */
    public record Source(String repoId, String branch, List<String> principals)
    {
    }
}
