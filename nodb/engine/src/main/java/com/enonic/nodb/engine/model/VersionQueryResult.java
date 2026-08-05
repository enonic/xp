package com.enonic.nodb.engine.model;

import java.util.List;

/**
 * Result of a {@link VersionQuery}: {@code totalHits} is the full match count regardless
 * of the query's paging window (mirrors ES totalHits, which the enumerated callers rely
 * on independently of page size — nodb/BUILD-PHASE-3.5.md Gate 0).
 */
public record VersionQueryResult(long totalHits, List<VersionRecord> versions)
{
}
