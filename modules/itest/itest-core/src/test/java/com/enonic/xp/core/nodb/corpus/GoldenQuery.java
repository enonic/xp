package com.enonic.xp.core.nodb.corpus;

import java.util.function.Supplier;

import com.enonic.xp.node.NodeQuery;

/**
 * One row of the golden-query corpus table (see {@link GoldenCorpus}).
 *
 * @param id         stable identifier; also the artifact's sort key, so it must never be reused
 *                   for a different query
 * @param family     translator family this row belongs to (Gate 0(c) inventory naming), used to
 *                   slice the corpus per Gate C/D/E batch
 * @param acceptance the acceptance rule this row is diffed under
 * @param source     which search source / principal set the query is issued with
 * @param intent     one-line human description of what the row pins
 * @param allowEmpty {@code true} for rows that legitimately return nothing (the G-2/G-4 gap
 *                   cases, count-only paging, fail-closed ACL). Every other row must produce a
 *                   non-empty result -- a corpus query that silently returns nothing proves
 *                   nothing
 * @param query      builds the query; called once per run
 */
record GoldenQuery(String id, String family, Acceptance acceptance, SourceKind source, String intent, boolean allowEmpty,
                   Supplier<NodeQuery> query)
{
}
