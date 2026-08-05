package com.enonic.xp.storage.spi;

import java.util.List;

import org.jspecify.annotations.NullMarked;

/**
 * Result of {@link NodeStore#findVersions}: {@code totalHits} is the full match count
 * regardless of the query's paging window (the callers rely on it independently of page
 * size — e.g. count-only checks with size 0).
 */
@NullMarked
public record VersionQueryResult(long totalHits, List<VersionRecord> versions)
{
    public VersionQueryResult
    {
        versions = List.copyOf( versions );
    }
}
