package com.enonic.nodb.engine.store;

/**
 * A payload attached to a {@link WriteBatchRequest}: either the caller inlines the bytes,
 * or references content it believes NoDB already has by hash alone (avoids re-sending
 * bytes for content that was already dedup-stored by an earlier write).
 */
public sealed interface PayloadRef
{
    record Inline(byte[] bytes) implements PayloadRef
    {
    }

    record HashOnly(String hash) implements PayloadRef
    {
    }
}
