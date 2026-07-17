package com.enonic.xp.storage.spi;

import java.util.List;

import org.jspecify.annotations.NullMarked;

/**
 * Phase-0-provisional minimal search result (matched node ids, in rank order, and the
 * total hit count). The full {@code SearchResult}/{@code SearchHit} DTO family (score,
 * highlighting, sort values, aggregations, suggestions) is already ES-free in core-repo
 * and moves into this module as-is in Gate C, superseding this record.
 */
@NullMarked
public record SearchResultRecord(List<String> nodeIds, long totalHits)
{
    public SearchResultRecord
    {
        nodeIds = List.copyOf( nodeIds );
    }
}
