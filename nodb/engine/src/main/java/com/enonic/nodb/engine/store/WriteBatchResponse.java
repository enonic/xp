package com.enonic.nodb.engine.store;

import java.util.List;

/**
 * Result of a {@link WriteBatchRequest}. Exactly one of the two is meaningful:
 * {@code outboxSeq} on success (the max outbox seq written by this batch), or
 * {@code needPayload} (non-empty) when the batch referenced content by hash that NoDB
 * does not have — in that case nothing was persisted and the caller should retry with
 * those hashes inlined.
 */
public record WriteBatchResponse(Long outboxSeq, List<String> needPayload)
{
    public boolean needsPayload()
    {
        return !needPayload.isEmpty();
    }
}
